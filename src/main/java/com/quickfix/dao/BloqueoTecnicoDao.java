package com.quickfix.dao;

import com.quickfix.entities.BloqueoTecnico;
import com.quickfix.entities.Tecnico; // ⬅️ IMPORTANTE
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManager; // ⬅️ IMPORTANTE
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery; // ⬅️ IMPORTANTE
import java.util.List; // ⬅️ IMPORTANTE

public class BloqueoTecnicoDao extends GenericDao<BloqueoTecnico, Integer> {

    public BloqueoTecnicoDao(EntityManagerFactory emf) {
        super(BloqueoTecnico.class, emf);
    }
    
    // ⬇️ --- MÉTODO NUEVO --- ⬇️
    /**
     * Busca todos los bloqueos registrados para un técnico específico.
     */
    public List<BloqueoTecnico> findBloqueosByTecnico(Tecnico tecnico) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<BloqueoTecnico> query = em.createQuery(
                "SELECT b FROM BloqueoTecnico b WHERE b.tecnico = :tecnico", 
                BloqueoTecnico.class
            );
            query.setParameter("tecnico", tecnico);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}