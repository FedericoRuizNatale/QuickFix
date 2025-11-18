package com.quickfix.dao;

import com.quickfix.entities.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException; // Importante para el catch
import jakarta.persistence.TypedQuery;

public class UsuarioDao { 

    protected EntityManagerFactory emf;

    
    public UsuarioDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    
    public Usuario findByEmailAndPassword(String email, String password) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                
                "SELECT u FROM Usuario u WHERE u.email = :email AND u.contraseña = :pass", 
                Usuario.class
            );
            query.setParameter("email", email);
            query.setParameter("pass", password);
            
            
            return query.getSingleResult();
            
        } catch (NoResultException e) {
            return null; 
        } finally {
            if (em != null) em.close();
        }
    }
    public Usuario findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                
                "SELECT u FROM Usuario u WHERE u.email = :email", 
                Usuario.class
            );
            query.setParameter("email", email);
            
            
            return query.getSingleResult();
            
        } catch (NoResultException e) {
            return null; // Login fallido (no se encontró)
        } finally {
            if (em != null) em.close();
        }
    }
    
    
}
