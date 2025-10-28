package com.quickfix.dao;

import java.util.List;

import com.quickfix.entities.ConsultaTecnica;
import com.quickfix.enums.EstadoConsulta; // Importa el Enum de estado
import com.quickfix.persistencia.GenericDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

/**
 * DAO Específico para la entidad ConsultaTecnica.
 * Hereda todos los métodos CRUD (create, find, update, delete, findAll)
 * de GenericDao.
 */
public class ConsultaTecnicaDao extends GenericDao<ConsultaTecnica, Integer> {

    /**
     * Constructor que recibe el EntityManagerFactory desde la
     * ControladoraPersistencia y se lo pasa al padre (GenericDao).
     * @param emf El EntityManagerFactory único de la aplicación.
     */
    public ConsultaTecnicaDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "ConsultaTecnica.class"
        super(ConsultaTecnica.class, emf);
    }

    // ¡Los métodos create, find, update, delete, findAll ya están heredados!


    // --- MÉTODOS ESPECIALES SOLO PARA CONSULTA TÉCNICA ---

    /**
     * Busca todas las consultas técnicas que están en estado PENDIENTE
     * y que aún no tienen un técnico asignado.
     * @return Una lista de Consultas Técnicas pendientes.
     */
    public List<ConsultaTecnica> findConsultasPendientes() {
        EntityManager em = getEntityManager(); // Usa el método heredado
        try {
            // JPQL: Busca consultas donde el estado sea PENDIENTE y el campo 'tecnico' sea NULL
            TypedQuery<ConsultaTecnica> query = em.createQuery(
                "SELECT c FROM ConsultaTecnica c WHERE c.tecnico IS NULL AND c.estado = :estadoPendiente",
                ConsultaTecnica.class
            );
            query.setParameter("estadoPendiente", EstadoConsulta.PENDIENTE); // Filtra por el estado

            return query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Busca todas las consultas asignadas a un técnico específico (para su panel).
     * @param tecnico El objeto Tecnico logueado.
     * @return Una lista de Consultas Técnicas asignadas a ese técnico.
     */
    public List<ConsultaTecnica> findConsultasByTecnico(com.quickfix.entities.Tecnico tecnico) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ConsultaTecnica> query = em.createQuery(
                "SELECT c FROM ConsultaTecnica c WHERE c.tecnico = :tecnico ORDER BY c.fechaHora DESC", // Ordenadas por fecha
                ConsultaTecnica.class
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