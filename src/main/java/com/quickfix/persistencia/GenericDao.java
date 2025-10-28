package com.quickfix.persistencia; // O el paquete que prefieras para esta clase

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory; // Importante
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery; // Usamos TypedQuery
import java.util.List;

/**
 * DAO Genérico para manejar todas las operaciones CRUD (Jakarta Persistence).
 * <T> es el tipo de la entidad (ej. Cliente)
 * <K> es el tipo de la Clave Primaria (ej. Integer)
 */
public class GenericDao<T, K > {

    // El EMF es "protected" para que las clases hijas puedan usarlo si lo necesitan
    protected EntityManagerFactory emf;
    
    // Almacena la clase (ej. Cliente.class) para usarla en las consultas
    protected Class<T> entityClass; 

    /**
     * Constructor que recibe el EntityManagerFactory desde la 
     * ControladoraPersistencia.
     */
    public GenericDao(Class<T> entityClass, EntityManagerFactory emf) {
        this.entityClass = entityClass;
        this.emf = emf; // Se lo guarda
    }

    /**
     * Método de utilidad para obtener un EntityManager (una conexión/sesión).
     */
    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // --- MÉTODOS CRUD ---

    /**
     * Guarda una nueva entidad en la base de datos.
     * @param entity El objeto a guardar.
     */
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

    /**
     * Busca una entidad por su Clave Primaria (ID).
     * @param id La Clave Primaria (ej. 1, 5, 100).
     * @return El objeto encontrado, o null si no existe.
     */
    public T find(K id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id); 
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Actualiza una entidad existente en la base de datos.
     * @param entity El objeto con los datos ya modificados.
     */
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

    /**
     * Elimina una entidad de la base de datos por su ID.
     * @param id La Clave Primaria de la entidad a eliminar.
     */
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

    /**
     * Devuelve una lista con todas las entidades de este tipo.
     * @return Una List<T> con todos los registros.
     */
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
