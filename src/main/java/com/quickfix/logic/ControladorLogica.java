package com.quickfix.logic;

import java.util.List;

import com.quickfix.entities.*;
import com.quickfix.enums.EstadoConsulta;
import com.quickfix.enums.EstadoTurno;
import com.quickfix.persistencia.ControladorPersistencia;

public class ControladorLogica {
	ControladorPersistencia controlPersis = new ControladorPersistencia();

	public void crearAdministrador(Administrador admin) {
		controlPersis.adminDao.create(admin);
	}

	public Usuario validarLogin(String email, String password) {

		// 2. Llama al método específico del UsuarioDao a través de la
		// ControladoraPersistencia
		Usuario usuarioEncontrado = controlPersis.usuarioDao.findByEmailAndPassword(email, password);

		// 3. (Opcional pero recomendado) Añade una capa extra de lógica:
		// Verifica si el usuario existe Y si está activo (estado = true).
		if (usuarioEncontrado != null && usuarioEncontrado.isEstado()) {
			// Si ambas condiciones son verdaderas, devuelve el objeto Usuario completo
			return usuarioEncontrado;
		} else {
			// Si el DAO devolvió null (no encontrado) o si el usuario está inactivo (estado
			// = false),
			// devuelve null para indicar que el login falló.
			return null;
		}
	}

	public void crearCliente(Cliente cli) {
		controlPersis.clienteDao.create(cli);
	}
	
	public Cliente traerClienteCompleto(Integer idUsuario) {
		return controlPersis.clienteDao.find(idUsuario);
	}
	
	public Usuario obtenerUsuarioConEmail(String email) {
		 return controlPersis.usuarioDao.findByEmail(email);
	}
	
	
	
	//--------------------------------EquipoCliente-------------------------------------------------
	
	public void eliminarEquipo(Integer idEquipo) throws Exception { // Mantenemos el throws por si el DAO falla
	    // Simplemente llama al método delete del DAO, que ahora hace la validación.
	    controlPersis.equipoClienteDao.delete(idEquipo); 
	    // Si el DAO lanza la RuntimeException, esta se propagará al Servlet.
	}
	
	public void editarEquipo(EquipoCliente equipoCliente) {
		controlPersis.equipoClienteDao.update(equipoCliente);
	}
	
	public boolean clienteTieneEquipos(Cliente cliente) {
		List<EquipoCliente> equipos = controlPersis.equipoClienteDao.findByCliente(cliente);
		// Si la lista no es nula y tiene al menos un elemento, devuelve true.
		return (equipos != null && !equipos.isEmpty());
	}
	
	public EquipoCliente traerEquipo(Integer idEquipo) {
		return controlPersis.equipoClienteDao.find(idEquipo);
	}
	public List<EquipoCliente>traerEquiposPorCliente(Cliente cliente) {
	    // La lógica de negocio está aquí: solo llama al DAO
	    return controlPersis.equipoClienteDao.findByCliente(cliente);
	}
	public void crearEquipo(EquipoCliente equipo) {
		controlPersis.equipoClienteDao.create(equipo);
	}
	
	public Turno traerTurno(Integer idTurno) {
		return controlPersis.turnoDao.find(idTurno);
	}
	public List<Turno> traerTodosLosTurnos() {
		return controlPersis.turnoDao.findAll();
	}
	
	public void crearTurno(Turno turno) {
		controlPersis.turnoDao.create(turno);
	}
	public void setEstadoTurno(Integer idTurno, EstadoTurno nuevoEstado) {
        // En una aplicación real, pondrías validación de roles aquí (ej: solo el Admin puede hacerlo).
        controlPersis.turnoDao.setEstadoTurno(idTurno, nuevoEstado);
    }
	
	public List<Turno> traerTurnosDisponibles(){
		return controlPersis.turnoDao.findTurnosDispobibles();
	}
	
	
	public Servicio traerServicio(Integer idServicio) {
		return controlPersis.servicioDao.find(idServicio);
	}
	
	
	
	public List<Servicio> traerTodosLosServicios(){
		return controlPersis.servicioDao.findAll();
	}
	public void crearServicio(Servicio servicio) {
		controlPersis.servicioDao.create(servicio);
	}
	
	//---------------------------------ConsultaTecnica--------------------------------------------------------------
	
	public void crearConsultaTecnica(ConsultaTecnica consultaTecnica) {
		controlPersis.consultaTecnicaDao.create(consultaTecnica);
	}
	
	
	// --- En ControladorLogica.java ---

	/**
	 * Procesa la creación de una nueva solicitud y la actualización del turno
	 * en una única llamada a la persistencia.
	 */
	
	
	//--------------------------SolicitudServicio-------------------------------------------
	public List<SolicitudServicio> traerSolicitudesPorCliente(Cliente cliente){
		return controlPersis.solicitudServicioDao.findByCliente(cliente);
	}
	
	
	
	public void procesarNuevaSolicitud(SolicitudServicio solicitud, Turno turno) {
	    
	    // Aquí iría cualquier lógica de negocio final (ej: calcular puntos de lealtad)
	    
	    // Delega la responsabilidad transaccional a la Controladora de Persistencia.
	    controlPersis.procesarNuevaSolicitud(solicitud, turno);
	}
	
	
	//-------------------------Tecnico----------------------------
	public void crearTecnico (Tecnico tecnico) {
		controlPersis.tecnicoDao.create(tecnico);
	}

	public List<ConsultaTecnica> traerConsultasPendientes() {
		
		return controlPersis.consultaTecnicaDao.findConsultasPendientes();
	}

public void responderConsulta(Integer idConsulta, Tecnico tecnicoLogueado, String solucion) throws Exception {
        
        // 1. Buscar la consulta técnica en la BD
        ConsultaTecnica consulta = controlPersis.consultaTecnicaDao.find(idConsulta);

        // 2. Validaciones de negocio
        if (consulta == null) {
            throw new Exception("La consulta con ID " + idConsulta + " no fue encontrada.");
        }
        // Verifica si ya fue tomada por otro técnico o si no está pendiente
        if (consulta.getTecnico() != null || consulta.getEstado() != EstadoConsulta.PENDIENTE) {
             throw new Exception("Esta consulta ya no está pendiente o fue tomada por otro técnico.");
        }
        if (solucion == null || solucion.trim().isEmpty()) {
            throw new Exception("La solución no puede estar vacía.");
        }

        // 3. Actualizar los datos de la consulta
        consulta.setTecnico(tecnicoLogueado); // Asigna el técnico que respondió
        consulta.setSolucion(solucion);       // Guarda la respuesta
        consulta.setEstado(EstadoConsulta.RESUELTA); // Cambia el estado

        // 4. Llamar al DAO para guardar los cambios (actualizar)
        // El método update es heredado de GenericDao
        controlPersis.consultaTecnicaDao.update(consulta); 
        
        // (Opcional) Aquí podrías añadir lógica para notificar al cliente por email.
    }

public Tecnico traerTecnicoCompleto(Integer idUsuario) {
	
	return controlPersis.tecnicoDao.find(idUsuario);
}

public List<SolicitudServicio> traerSolicitudesActivasPorTecnico(Tecnico tecnicoLogueado) {
	
	return controlPersis.solicitudServicioDao.findSolicitudesActivasByTecnico(tecnicoLogueado);
}
	
}


