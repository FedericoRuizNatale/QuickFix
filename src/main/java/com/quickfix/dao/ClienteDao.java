package com.quickfix.dao; // O el paquete donde tengas tus DAOs

import com.quickfix.entities.Cliente;
import com.quickfix.persistencia.GenericDao; // Importa tu DAO genérico
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException; // Para el catch
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * DAO Específico para la entidad Cliente.
 * Hereda todos los métodos CRUD (create, find, update, delete, findAll)
 * de GenericDao.
 */
public class ClienteDao extends GenericDao<Cliente, Integer> {

    /**
     * Constructor que recibe el EntityManagerFactory desde la
     * ControladoraPersistencia y se lo pasa al padre (GenericDao).
     * @param emf El EntityManagerFactory único de la aplicación.
     */
    public ClienteDao(EntityManagerFactory emf) {
        // Le dice al padre que esta clase maneja "Cliente.class"
        // y le pasa el EMF para que pueda crear EntityManagers.
        super(Cliente.class, emf);
    }
    
    // --- ¡YA NO NECESITAS ESCRIBIR create, find, update, delete, findAll! ---
    // Todos esos métodos fueron heredados automáticamente de GenericDao.
    
    
    // --- MÉTODOS ESPECIALES SOLO PARA CLIENTE ---
    
    /**
     * Busca un cliente por su número de DNI.
     * @param dni El DNI a buscar.
     * @return El Cliente encontrado, o null si no existe.
     */
    public Cliente findByDni(String dni) {
        EntityManager em = getEntityManager(); // Usa el método heredado
        try {
            TypedQuery<Cliente> query = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.dni = :dni", 
                Cliente.class
            );
            query.setParameter("dni", dni);
            
            return query.getSingleResult();
            
        } catch (NoResultException e) {
            return null; // No se encontró
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Busca clientes por apellido (ejemplo de otro método específico).
     * @param apellido El apellido a buscar.
     * @return Una lista de clientes que coincidan.
     */
    public List<Cliente> findByApellido(String apellido) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                // Usamos LIKE para búsquedas parciales (ej: "Perez")
                "SELECT c FROM Cliente c WHERE c.apellido LIKE :apellido", 
                Cliente.class
            );
            query.setParameter("apellido", "%" + apellido + "%");
            
            return query.getResultList();
            
        } finally {
            if (em != null) em.close();
        }
    }
}