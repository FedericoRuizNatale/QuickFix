package com.quickfix.dao; // El paquete de los DAOs

import com.quickfix.entities.Servicio;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManagerFactory;
// Puedes agregar imports para EntityManager, TypedQuery, List si añades métodos específicos luego

/**
 * DAO Específico para la entidad Servicio.
 * Hereda todos los métodos CRUD (create, find, update, delete, findAll)
 * de GenericDao.
 */
public class ServicioDao extends GenericDao<Servicio, Integer> {

    /**
     * Constructor que recibe el EntityManagerFactory desde la
     * ControladoraPersistencia y se lo pasa al padre (GenericDao).
     * @param emf El EntityManagerFactory único de la aplicación.
     */
    public ServicioDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "Servicio.class"
        // y le pasa el EMF para crear EntityManagers.
        super(Servicio.class, emf);
    }
    
    // ¡Los métodos create, find, update, delete, findAll ya están heredados!
    
    
    // --- MÉTODOS ESPECIALES SOLO PARA SERVICIO ---
    
    // (Aquí podrías agregar métodos como findByNombre(String nombre), 
    // findByCostoBetween(Double min, Double max), etc.)
    
}