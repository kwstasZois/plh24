/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import project.exceptions.NonexistentEntityException;

/**
 *
 * @author kostas
 */
public class ArticleJpaController implements Serializable {
    
    
    public ArticleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Article article) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Category category = article.getCategory();
            if (category != null) {
                category = em.getReference(category.getClass(), category.getId());
                article.setCategory(category);
            }
            em.persist(article);
            if (category != null) {
                category.getArticleList().add(article);
                category = em.merge(category);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Article article) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Article persistentArticle = em.find(Article.class, article.getPageid());
            Category categoryOld = persistentArticle.getCategory();
            Category categoryNew = article.getCategory();
            if (categoryNew != null) {
                categoryNew = em.getReference(categoryNew.getClass(), categoryNew.getId());
                article.setCategory(categoryNew);
            }
            article = em.merge(article);
            if (categoryOld != null && !categoryOld.equals(categoryNew)) {
                categoryOld.getArticleList().remove(article);
                categoryOld = em.merge(categoryOld);
            }
            if (categoryNew != null && !categoryNew.equals(categoryOld)) {
                categoryNew.getArticleList().add(article);
                categoryNew = em.merge(categoryNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = article.getPageid();
                if (findArticle(id) == null) {
                    throw new NonexistentEntityException("The article with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Article article;
            try {
                article = em.getReference(Article.class, id);
                article.getPageid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The article with id " + id + " no longer exists.", enfe);
            }
            Category category = article.getCategory();
            if (category != null) {
                category.getArticleList().remove(article);
                category = em.merge(category);
            }
            em.remove(article);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Article> findArticleEntities() {
        return findArticleEntities(true, -1, -1);
    }

    public List<Article> findArticleEntities(int maxResults, int firstResult) {
        return findArticleEntities(false, maxResults, firstResult);
    }

    private List<Article> findArticleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Article.class));
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

    public Article findArticle(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Article.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Article> rt = cq.from(Article.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
