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



 // --- En ControladorPersistencia.java ---

 // (Asegúrate de tener "import jakarta.persistence.EntityManager;" al principio)

 public void procesarNuevaSolicitud(SolicitudServicio solicitud, Turno turno) {

     EntityManager em = null; 
     try {
         em = emf.createEntityManager(); 
         em.getTransaction().begin();

         // --- ¡ORDEN CORREGIDO! ---

         // 1. GUARDAR EL TURNO PRIMERO
         //    Como SolicitudServicio "depende" de Turno,
         //    el Turno debe existir primero en la base de datos.
         //    Usamos 'persist' porque es un objeto NUEVO.
         em.persist(turno);

         // 2. GUARDAR LA SOLICITUD (que ahora apunta a un Turno que SÍ existe)
         //    Usamos 'persist' porque también es nueva.
         em.persist(solicitud);

         // 3. (Opcional, si Servicio también dependía de Solicitud)
         //    Asegurar la relación con Servicio (si aplica)
         Servicio servicio = solicitud.getServicio();
         if (servicio != null && servicio.getSolicitudServicio() == null) { // Evitar sobrescribir si es M-1
             servicio.setSolicitudServicio(solicitud); // Asumiendo One-to-One
             em.merge(servicio); 
         }

         // 4. Si todo fue bien, confirmar los cambios
         em.getTransaction().commit();

     } catch (PersistenceException e) {
         if (em != null && em.getTransaction().isActive()) {
             em.getTransaction().rollback();
         }
         // Lanzar la excepción para que el Servlet la atrape
         throw new RuntimeException("Error al procesar la Solicitud y Turno.", e);

     } finally {
         if (em != null) {
             em.close();
         }
     }
 }
}