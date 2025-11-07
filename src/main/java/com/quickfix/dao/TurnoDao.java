package com.quickfix.dao; // El paquete correcto para los DAOs

import java.time.LocalDateTime;
import java.util.List;

import com.quickfix.entities.Turno;
import com.quickfix.enums.EstadoTurno;
import com.quickfix.persistencia.GenericDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

/**
 * DAO Específico para la entidad Tecnico.
 * Hereda todos los métodos CRUD (create, find, update, delete, findAll)
 * de GenericDao.
 */
public class TurnoDao extends GenericDao<Turno, Integer> {

    /**
     * Constructor que recibe el EntityManagerFactory desde la
     * ControladoraPersistencia y se lo pasa al padre (GenericDao).
     * @param emf El EntityManagerFactory único de la aplicación.
     */
    public TurnoDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "Tecnico.class"
        // y le pasa el EMF para crear EntityManagers.
        super(Turno.class, emf);
    }
    
    public List<Turno> findTurnosDispobibles(){
    	EntityManager em = getEntityManager();
    	try {
			TypedQuery<Turno> query = em.createQuery(
					"SELECT t FROM Turno t WHERE t.estado = :estado",
					Turno.class
					);
			query.setParameter("estado", EstadoTurno.DISPONIBLE);
			return query.getResultList();
		} finally {
			if (em != null) em.close();
		}
    	
    }
    
   
    public void setEstadoTurno(Integer idTurno, EstadoTurno nuevoEstado) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Turno turno = em.find(Turno.class, idTurno);
            
            if (turno != null) {
                turno.setEstado(nuevoEstado);
                em.merge(turno);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar el estado del turno.", e);
        } finally {
            if (em != null) em.close();
        }
    }
    
    public List<Turno> findTurnosReservadosFuturos() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Turno> query = em.createQuery(
                // Buscamos turnos que NO estén disponibles y sean de hoy en adelante
                "SELECT t FROM Turno t " +
                "WHERE t.estado != :estadoDisponible " +
                "AND t.fechaHoraInicio >= :ahora " +
                "ORDER BY t.fechaHoraInicio",
                Turno.class
            );
            
            query.setParameter("estadoDisponible", EstadoTurno.DISPONIBLE); // ⬅️ O el estado que uses para "libre"
            query.setParameter("ahora", LocalDateTime.now().withHour(0).withMinute(0)); // Desde la medianoche de hoy
            
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    
}
