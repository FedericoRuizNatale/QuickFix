package com.quickfix.dao; // El paquete correcto para los DAOs

import com.quickfix.entities.Administrador;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManagerFactory;

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
    
    // ¡Los métodos create, find, update, delete, findAll ya están heredados!
    
    
    // --- MÉTODOS ESPECIALES SOLO PARA ADMIN ---
    
    // (Aquí podrías agregar métodos como findByNivelAcceso(Integer nivel), etc.)
    
}