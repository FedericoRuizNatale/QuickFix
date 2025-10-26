package com.quickfix.dao;

import java.util.List;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.persistencia.GenericDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

// --- En EquipoDao.java ---

// Hereda de GenericDao<EquipoCliente, Integer>
public class EquipoClienteDao extends GenericDao<EquipoCliente, Integer> {

    // ... (constructor) ...

    public EquipoClienteDao(Class<EquipoCliente> entityClass, EntityManagerFactory emf) {
		super(entityClass, emf);
		// TODO Auto-generated constructor stub
	}

	/**
     * Busca todos los equipos asociados a un Cliente específico.
     */
    public List<EquipoCliente> findByCliente(Cliente cliente) {
        EntityManager em = getEntityManager();
        try {
            // JPQL: Busca equipos donde el campo 'cliente' sea el objeto Cliente dado
            TypedQuery<EquipoCliente> query = em.createQuery(
                "SELECT e FROM EquipoCliente e WHERE e.cliente = :cliente", 
                EquipoCliente.class
            );
            query.setParameter("cliente", cliente);
            
            return query.getResultList();
            
        } finally {
            if (em != null) em.close();
        }
    }
}