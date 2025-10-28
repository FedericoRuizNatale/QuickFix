package com.quickfix.persistencia;

import com.quickfix.dao.*; // Importa todos tus DAOs
import com.quickfix.entities.ConsultaTecnica;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Turno;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class ControladorPersistencia {

    // 1. La ÚNICA instancia del EntityManagerFactory
    private final EntityManagerFactory emf;
    
    // 2. Instancias de todos los DAOs que el sistema necesita
    public final ClienteDao clienteDao;
    public final TecnicoDao tecnicoDao;
    public final AdministradorDao adminDao;
    public final ServicioDao servicioDao;
    public final UsuarioDao usuarioDao; // El del login
    public final EquipoClienteDao equipoClienteDao;
    public final TurnoDao turnoDao;
    public final ConsultaTecnicaDao consultaTecnicaDao;
    public final SolicitudServicioDao solicitudServicioDao;
    // ... etc.
    public ControladorPersistencia() {
        // 3. Se crea el EMF una sola vez
        this.emf = Persistence.createEntityManagerFactory("quickfix");
        
        // 4. Se crean los DAOs y se les "inyecta" el EMF
        this.clienteDao = new ClienteDao(emf);
        this.tecnicoDao = new TecnicoDao(emf);
        this.adminDao = new AdministradorDao(emf);
        this.servicioDao = new ServicioDao(emf);
        this.usuarioDao = new UsuarioDao(emf); // (El UsuarioDao también debería recibir el EMF)
        this.equipoClienteDao = new EquipoClienteDao(emf);
        this.turnoDao = new TurnoDao(emf);
        this.consultaTecnicaDao = new ConsultaTecnicaDao(emf);
        this.solicitudServicioDao = new SolicitudServicioDao(emf);
    }
    
    
    
    // Método para cerrar la conexión principal al final de la aplicación
    public void closeEmf() {
        if (emf != null) {
            emf.close();
        }
    }



    public void procesarNuevaSolicitud(SolicitudServicio solicitud, Turno turno) {
        
        // El EntityManager lo crea el GenericDao, pero lo necesitamos aquí.
        EntityManager em = null; 

        try {
            // 1. Obtener un EntityManager de la EMF única.
            //    (Lo obtienes directamente de la EMF que tienes guardada)
            em = emf.createEntityManager(); 
            
            // 2. Iniciar la transacción
            em.getTransaction().begin();
            
            // 3. OPERACIÓN 1: Guardar la nueva Solicitud (CREATE)
            em.persist(solicitud);
            
            // 4. OPERACIÓN 2: Actualizar el Turno a Reservado (UPDATE)
            //    Usamos merge porque el objeto 'turno' viene de la lógica y necesita
            //    ser re-adjuntado al contexto de JPA.
            em.merge(turno); 
            
            // 5. Si todo fue bien, confirmar los cambios
            em.getTransaction().commit();

        } catch (PersistenceException e) {
            // 6. Si algo falla (ej: error de BD), revertir la transacción
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Lanzar la excepción para que la capa superior la maneje
            throw new RuntimeException("Error al procesar la Solicitud y Turno.", e);
            
        } finally {
            // 7. Cerrar el EntityManager
            if (em != null) {
                em.close();
            }
        }
    }
}