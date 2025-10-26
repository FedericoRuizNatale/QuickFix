package com.quickfix.dao; // El paquete correcto para los DAOs

import com.quickfix.entities.Tecnico;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO Específico para la entidad Tecnico.
 * Hereda todos los métodos CRUD (create, find, update, delete, findAll)
 * de GenericDao.
 */
public class TecnicoDao extends GenericDao<Tecnico, Integer> {

    /**
     * Constructor que recibe el EntityManagerFactory desde la
     * ControladoraPersistencia y se lo pasa al padre (GenericDao).
     * @param emf El EntityManagerFactory único de la aplicación.
     */
    public TecnicoDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "Tecnico.class"
        // y le pasa el EMF para crear EntityManagers.
        super(Tecnico.class, emf);
    }
    
    // ¡Los métodos create, find, update, delete, findAll ya están heredados!
    
    
    // --- MÉTODOS ESPECIALES SOLO PARA TECNICO ---
    
    /**
     * Busca todos los técnicos que tengan una especialidad específica.
     * @param especialidad La especialidad a buscar (ej: "Hardware", "Redes").
     * @return Una lista de Técnicos que coincidan.
     */
    public List<Tecnico> findByEspecialidad(String especialidad) {
        
        // 1. Declara 'em' fuera del try
        EntityManager em = null; 
        
        try {
            // 2. Inicializa 'em' dentro del try
            em = getEntityManager(); 
            
            TypedQuery<Tecnico> query = em.createQuery(
                "SELECT t FROM Tecnico t WHERE t.especialidad LIKE :esp", 
                Tecnico.class
            );
            query.setParameter("esp", "%" + especialidad + "%");
            
            return query.getResultList();
            
        } finally {
            // 3. Comprueba si es null correctamente antes de cerrar
            if (em != null) { 
                 em.close();
            }
        }
    }
}
