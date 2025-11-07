package com.quickfix.persistencia;

import com.quickfix.dao.*; // Importa todos tus DAOs
import com.quickfix.entities.ConsultaTecnica;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Servicio;
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Turno;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class ControladorPersistencia {

	
	private static ControladorPersistencia instance = null;
	
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
    public final AgendaConfiguracionDao agendaConfigDao;
    public final HorarioLaboralDao horarioLaboralDao;
    public final DiaNoLaboralDao diaNoLaboralDao;
    public final BloqueoTecnicoDao bloqueoTecnicoDao;
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
        this.agendaConfigDao = new AgendaConfiguracionDao(emf);
        this.horarioLaboralDao = new HorarioLaboralDao(emf);
        this.diaNoLaboralDao = new DiaNoLaboralDao(emf);
        this.bloqueoTecnicoDao = new BloqueoTecnicoDao(emf);
    }
    
    
    
 // 5. El "getter" PÚBLICO para la instancia
    public static ControladorPersistencia getInstance() {
        if (instance == null) {
            instance = new ControladorPersistencia();
        }
        return instance;
    }
    
    
    // Método para cerrar la conexión principal al final de la aplicación
    public void closeEmf() {
        if (emf != null) {
            emf.close();
        }
    }



    public void procesarNuevaSolicitud(SolicitudServicio solicitud, Turno turno) {
        
        EntityManager em = null; 

        try {
            em = emf.createEntityManager(); 
            em.getTransaction().begin();
            
            // Obtenemos el servicio ANTES de hacer nada
            Servicio servicio = solicitud.getServicio();

            // VALIDACIÓN: Si no hay servicio, no podemos continuar.
            if (servicio == null) {
                throw new PersistenceException("Intento de procesar una solicitud sin servicio asociado.");
            }
            
            // --- ORDEN CORREGIDO ---
            
            // 1. (ANTES 4) Guardar la Solicitud (INVERSO) PRIMERO.
            //    Esto genera el ID de la solicitud.
            em.persist(solicitud);
                
            // 2. (ANTES 3) Ahora que 'solicitud' tiene un ID, 
            //    establecemos la relación en el "dueño" y lo guardamos.
            servicio.setSolicitudServicio(solicitud);
            em.merge(servicio); // Ahora JPA puede tomar el ID de 'solicitud' y guardarlo en el 'servicio'
            
            // 3. (ANTES 5) Guardar los cambios del Turno.
            em.merge(turno); 
                
            // 4. Confirmar todo
            em.getTransaction().commit();

        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Lanzar la excepción para que la capa superior la maneje
            throw new RuntimeException("Error al procesar la Solicitud y Turno.", e);
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}