package com.quickfix.dao; 

import com.quickfix.entities.Administrador;
import com.quickfix.persistencia.GenericDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;


public class AdministradorDao extends GenericDao<Administrador, Integer> {

    
    public AdministradorDao(EntityManagerFactory emf) {
        
        super(Administrador.class, emf);
    }

	public long countAll() {
    EntityManager em = getEntityManager();
    try {
        // La base de datos cuenta directamente
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(a) FROM Administrador a", Long.class);
        return query.getSingleResult(); // Devuelve solo un número (long)
    } finally {
        if (em != null) em.close();
    }
}
    
    // ¡Los métodos create, find, update, delete, findAll ya están heredados!
    
    
    // --- MÉTODOS ESPECIALES SOLO PARA ADMIN ---
    
    // (Aquí podrías agregar métodos como findByNivelAcceso(Integer nivel), etc.)
    
}