package com.quickfix.persistencia;

import com.quickfix.dao.*; 
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
	
    //Unica instancia de EntityManageFactory
    private final EntityManagerFactory emf;
    
    
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
    
    public ControladorPersistencia() {
        // 3. Se crea el EMF una sola vez
        this.emf = Persistence.createEntityManagerFactory("quickfix");
        
        
        this.clienteDao = new ClienteDao(emf);
        this.tecnicoDao = new TecnicoDao(emf);
        this.adminDao = new AdministradorDao(emf);
        this.servicioDao = new ServicioDao(emf);
        this.usuarioDao = new UsuarioDao(emf); 
        this.equipoClienteDao = new EquipoClienteDao(emf);
        this.turnoDao = new TurnoDao(emf);
        this.consultaTecnicaDao = new ConsultaTecnicaDao(emf);
        this.solicitudServicioDao = new SolicitudServicioDao(emf);
        this.agendaConfigDao = new AgendaConfiguracionDao(emf);
        this.horarioLaboralDao = new HorarioLaboralDao(emf);
        this.diaNoLaboralDao = new DiaNoLaboralDao(emf);
        this.bloqueoTecnicoDao = new BloqueoTecnicoDao(emf);
    }
    
    
    
 
    public static ControladorPersistencia getInstance() {
        if (instance == null) {
            instance = new ControladorPersistencia();
        }
        return instance;
    }
    
    
    
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

        
         em.persist(turno);

        
         em.persist(solicitud);

         
         Servicio servicio = solicitud.getServicio();
         if (servicio != null && servicio.getSolicitudServicio() == null) { // Evitar sobrescribir si es M-1
             servicio.setSolicitudServicio(solicitud); 
             em.merge(servicio); 
         }

         
         em.getTransaction().commit();

     } catch (PersistenceException e) {
         if (em != null && em.getTransaction().isActive()) {
             em.getTransaction().rollback();
         }
         
         throw new RuntimeException("Error al procesar la Solicitud y Turno.", e);

     } finally {
         if (em != null) {
             em.close();
         }
     }
 }
}