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
import project.exceptions.PreexistingEntityException;

/**
 *
 * @author kostas
 */
public class SearchKeyJpaController implements Serializable {

    public SearchKeyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SearchKey searchKey) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(searchKey);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSearchKey(searchKey.getKeyword()) != null) {
                throw new PreexistingEntityException("SearchKey " + searchKey + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SearchKey searchKey) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            searchKey = em.merge(searchKey);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = searchKey.getKeyword();
                if (findSearchKey(id) == null) {
                    throw new NonexistentEntityException("The searchKey with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SearchKey searchKey;
            try {
                searchKey = em.getReference(SearchKey.class, id);
                searchKey.getKeyword();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The searchKey with id " + id + " no longer exists.", enfe);
            }
            em.remove(searchKey);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SearchKey> findSearchKeyEntities() {
        return findSearchKeyEntities(true, -1, -1);
    }

    public List<SearchKey> findSearchKeyEntities(int maxResults, int firstResult) {
        return findSearchKeyEntities(false, maxResults, firstResult);
    }

    private List<SearchKey> findSearchKeyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SearchKey.class));
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

    public SearchKey findSearchKey(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SearchKey.class, id);
        } finally {
            em.close();
        }
    }

    public int getSearchKeyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SearchKey> rt = cq.from(SearchKey.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
