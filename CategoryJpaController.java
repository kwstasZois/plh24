/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import project.exceptions.IllegalOrphanException;
import project.exceptions.NonexistentEntityException;
import project.exceptions.PreexistingEntityException;

/**
 *
 * @author kostas
 */
public class CategoryJpaController implements Serializable {

    public CategoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Category category) throws PreexistingEntityException, Exception {
        if (category.getArticleList() == null) {
            category.setArticleList(new ArrayList<Article>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Article> attachedArticleList = new ArrayList<Article>();
            for (Article articleListArticleToAttach : category.getArticleList()) {
                articleListArticleToAttach = em.getReference(articleListArticleToAttach.getClass(), articleListArticleToAttach.getPageid());
                attachedArticleList.add(articleListArticleToAttach);
            }
            category.setArticleList(attachedArticleList);
            em.persist(category);
            for (Article articleListArticle : category.getArticleList()) {
                Category oldCategoryOfArticleListArticle = articleListArticle.getCategory();
                articleListArticle.setCategory(category);
                articleListArticle = em.merge(articleListArticle);
                if (oldCategoryOfArticleListArticle != null) {
                    oldCategoryOfArticleListArticle.getArticleList().remove(articleListArticle);
                    oldCategoryOfArticleListArticle = em.merge(oldCategoryOfArticleListArticle);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCategory(category.getId()) != null) {
                throw new PreexistingEntityException("Category " + category + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Category category) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Category persistentCategory = em.find(Category.class, category.getId());
            List<Article> articleListOld = persistentCategory.getArticleList();
            List<Article> articleListNew = category.getArticleList();
            List<String> illegalOrphanMessages = null;
            for (Article articleListOldArticle : articleListOld) {
                if (!articleListNew.contains(articleListOldArticle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Article " + articleListOldArticle + " since its category field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Article> attachedArticleListNew = new ArrayList<Article>();
            for (Article articleListNewArticleToAttach : articleListNew) {
                articleListNewArticleToAttach = em.getReference(articleListNewArticleToAttach.getClass(), articleListNewArticleToAttach.getPageid());
                attachedArticleListNew.add(articleListNewArticleToAttach);
            }
            articleListNew = attachedArticleListNew;
            category.setArticleList(articleListNew);
            category = em.merge(category);
            for (Article articleListNewArticle : articleListNew) {
                if (!articleListOld.contains(articleListNewArticle)) {
                    Category oldCategoryOfArticleListNewArticle = articleListNewArticle.getCategory();
                    articleListNewArticle.setCategory(category);
                    articleListNewArticle = em.merge(articleListNewArticle);
                    if (oldCategoryOfArticleListNewArticle != null && !oldCategoryOfArticleListNewArticle.equals(category)) {
                        oldCategoryOfArticleListNewArticle.getArticleList().remove(articleListNewArticle);
                        oldCategoryOfArticleListNewArticle = em.merge(oldCategoryOfArticleListNewArticle);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = category.getId();
                if (findCategory(id) == null) {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Category category;
            try {
                category = em.getReference(Category.class, id);
                category.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Article> articleListOrphanCheck = category.getArticleList();
            for (Article articleListOrphanCheckArticle : articleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Category (" + category + ") cannot be destroyed since the Article " + articleListOrphanCheckArticle + " in its articleList field has a non-nullable category field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(category);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Category> findCategoryEntities() {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult) {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Category.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Category findCategory(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Category> rt = cq.from(Category.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
