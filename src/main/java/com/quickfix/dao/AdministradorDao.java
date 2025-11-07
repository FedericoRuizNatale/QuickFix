package com.quickfix.dao; // El paquete correcto para los DAOs

import com.quickfix.entities.Administrador;
import com.quickfix.persistencia.GenericDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

/**
 * DAO Específico para la entidad Administrador.
 * Hereda todos los métodos CRUD (create, find, update, delete, findAll)
 * de GenericDao.
 */
public class AdministradorDao extends GenericDao<Administrador, Integer> {

    /**
     * Constructor que recibe el EntityManagerFactory desde la
     * ControladoraPersistencia y se lo pasa al padre (GenericDao).
     * @param emf El EntityManagerFactory único de la aplicación.
     */
    public AdministradorDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "Administrador.class"
        // y le pasa el EMF para crear EntityManagers.
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