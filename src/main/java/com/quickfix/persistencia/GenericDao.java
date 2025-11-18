package com.quickfix.persistencia; // O el paquete que prefieras para esta clase

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory; // Importante
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery; // Usamos TypedQuery
import java.util.List;


public class GenericDao<T, K > {

    
    protected EntityManagerFactory emf;
    
    
    protected Class<T> entityClass; 

   
    public GenericDao(Class<T> entityClass, EntityManagerFactory emf) {
        this.entityClass = entityClass;
        this.emf = emf; // Se lo guarda
    }

    
    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // --- MÉTODOS CRUD ---

    public void create(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            // Relanzamos la excepción para que la capa de lógica la maneje
            throw new RuntimeException("Error al crear la entidad", e);
        } finally {
            if (em != null) em.close();
        }
    }

    
    public T find(K id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id); 
        } finally {
            if (em != null) em.close();
        }
    }

    
    public void update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity); // Merge es el "update" en JPA
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar la entidad", e);
        } finally {
            if (em != null) em.close();
        }
    }

    
    public void delete(K id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Usamos find para traer la entidad al contexto de persistencia
            T entity = em.find(entityClass, id);
            
            if (entity == null) {
                throw new EntityNotFoundException("Entidad con ID " + id + " no encontrada.");
            }
            
            em.remove(entity); // Ahora sí podemos borrarla
            em.getTransaction().commit();
        } catch (Exception e) { // Captura EntityNotFound u otros errores
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al eliminar la entidad", e);
        } finally {
            if (em != null) em.close();
        }
    }

    
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            // JPQL genérico usando el nombre simple de la clase
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            
            // Usamos TypedQuery para obtener una lista del tipo correcto
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            
            return query.getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
}
