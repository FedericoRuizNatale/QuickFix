package com.quickfix.dao;
import com.quickfix.entities.Cliente; // Importa Cliente
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Tecnico;
import com.quickfix.enums.EstadoSolicitud;
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

public List<SolicitudServicio> findSolicitudesActivasByTecnico(Tecnico tecnico) {
    EntityManager em = null; // Declarar fuera del try
    try {
        em = getEntityManager(); // Inicializar dentro del try
        
        // JPQL: Busca solicitudes asignadas al técnico y excluye los estados finales
        TypedQuery<SolicitudServicio> query = em.createQuery(
            "SELECT s FROM SolicitudServicio s WHERE s.tecnico = :tecnico " +
            "AND s.estado NOT IN (:estadosFinalizados) ORDER BY s.fechaHoraCreacion DESC", // Ordenadas por fecha más reciente
            SolicitudServicio.class
        );
        
        query.setParameter("tecnico", tecnico);
        
        // Define qué estados consideras "finalizados" o "inactivos"
        query.setParameter("estadosFinalizados", 
            List.of(EstadoSolicitud.FINALIZADA, EstadoSolicitud.CANCELADA)); 
            // Podrías añadir LISTA_PARA_RETIRO si tampoco quieres verlas aquí
        
        return query.getResultList();
        
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
}
