package com.quickfix.dao;
import com.quickfix.entities.Cliente; // Importa Cliente
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.persistencia.GenericDao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
public class SolicitudServicioDao extends GenericDao<SolicitudServicio, Integer>{

	public SolicitudServicioDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "Tecnico.class"
        // y le pasa el EMF para crear EntityManagers.
        super(SolicitudServicio.class, emf);
    }

public List<SolicitudServicio> findByCliente(Cliente cliente) {
    EntityManager em = getEntityManager();
    try {
        TypedQuery<SolicitudServicio> query = em.createQuery(
            // Busca Solicitudes donde el campo 'cliente' coincida
            "SELECT s FROM SolicitudServicio s WHERE s.cliente = :cliente ORDER BY s.fechaHoraCreacion DESC", // Ordena por fecha
            SolicitudServicio.class
        );
        query.setParameter("cliente", cliente);
        return query.getResultList();
    } finally {
        if (em != null) em.close();
    	}
	}
}
