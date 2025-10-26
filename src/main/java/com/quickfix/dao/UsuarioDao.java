package com.quickfix.dao;

import com.quickfix.entities.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException; // Importante para el catch
import jakarta.persistence.TypedQuery;

public class UsuarioDao { // ¡No hereda de GenericDao!

    protected EntityManagerFactory emf;

    // Recibe el EMF de la ControladoraPersistencia
    public UsuarioDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    /**
     * Busca un usuario por su email y contraseña.
     * Devuelve el Usuario (que puede ser Cliente, Tecnico o Admin)
     * o null si no se encuentra o la contraseña es incorrecta.
     */
    public Usuario findByEmailAndPassword(String email, String password) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                // JPQL consulta la entidad ABSTRACTA Usuario
                "SELECT u FROM Usuario u WHERE u.email = :email AND u.contraseña = :pass", 
                Usuario.class
            );
            query.setParameter("email", email);
            query.setParameter("pass", password);
            
            // Usamos getSingleResult para que devuelva un solo objeto
            return query.getSingleResult();
            
        } catch (NoResultException e) {
            return null; // Login fallido (no se encontró)
        } finally {
            if (em != null) em.close();
        }
    }
    public Usuario findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                // JPQL consulta la entidad ABSTRACTA Usuario
                "SELECT u FROM Usuario u WHERE u.email = :email", 
                Usuario.class
            );
            query.setParameter("email", email);
            
            // Usamos getSingleResult para que devuelva un solo objeto
            return query.getSingleResult();
            
        } catch (NoResultException e) {
            return null; // Login fallido (no se encontró)
        } finally {
            if (em != null) em.close();
        }
    }
    
    // Aquí podrías agregar otros métodos genéricos de Usuario, como:
    // public Usuario findByEmail(String email) { ... }
}
