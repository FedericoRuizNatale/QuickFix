package com.quickfix.dao;

import java.util.List;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.persistencia.GenericDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

// --- En EquipoDao.java ---

// Hereda de GenericDao<EquipoCliente, Integer>
public class EquipoClienteDao extends GenericDao<EquipoCliente, Integer> {

    // ... (constructor) ...

	public EquipoClienteDao(EntityManagerFactory emf) { // Solo recibe EMF
        super(EquipoCliente.class, emf); // Llama al padre
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
    
    
    
    @Override
    public void delete(Integer id) { // K es Integer para EquipoCliente
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            // 1. Buscar la entidad DENTRO de la transacción
            EquipoCliente entity = em.find(entityClass, id); 
            
            if (entity == null) {
                throw new EntityNotFoundException("Equipo con ID " + id + " no encontrado.");
            }
            
            // --- ✅ VERIFICACIÓN DE SOLICITUDES (DENTRO DE LA SESIÓN) ---
            // Al acceder a getSolicitudes() aquí, Hibernate las carga (si es necesario)
            // porque la Sesión (em) todavía está abierta.
            if (!entity.getSolicitudes().isEmpty()) {
                 // Si tiene solicitudes, NO borrar. Revertir y lanzar error.
                 em.getTransaction().rollback(); // Importante: revertir ANTES de lanzar
                 throw new RuntimeException("No se puede eliminar el equipo porque tiene solicitudes de servicio asociadas.");
            }
            // --- FIN VERIFICACIÓN ---
            
            // 3. Si no tiene solicitudes, proceder a eliminar
            em.remove(entity); 
            em.getTransaction().commit();
            
        } catch (Exception e) { // Captura EntityNotFound, RuntimeException u otros errores
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Relanzar la excepción para que el Servlet la capture
            if (e instanceof EntityNotFoundException || e instanceof RuntimeException) {
                 throw e; // Relanza las excepciones específicas que ya manejamos
            } else {
                 throw new RuntimeException("Error al eliminar la entidad EquipoCliente", e); // Envuelve otras excepciones
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
