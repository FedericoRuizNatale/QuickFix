package com.quickfix.logic;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.quickfix.dto.SlotDTO;
import com.quickfix.dto.TurnoDTO;
import com.quickfix.entities.*;
import com.quickfix.enums.EstadoConsulta;
import com.quickfix.enums.EstadoSolicitud;
import com.quickfix.enums.EstadoTurno;
import com.quickfix.persistencia.ControladorPersistencia;

public class ControladorLogica {
	
    private static ControladorLogica instance = null;

    
    ControladorPersistencia controlPersis;

    
    private ControladorLogica() {
        this.controlPersis = ControladorPersistencia.getInstance();
    }

    
    public static ControladorLogica getInstance() {
        if (instance == null) {
            instance = new ControladorLogica();
        }
        return instance;
    }

	public Usuario validarLogin(String email, String password) {

		
		Usuario usuarioEncontrado = controlPersis.usuarioDao.findByEmailAndPassword(email, password);

		
		if (usuarioEncontrado != null && usuarioEncontrado.isEstado()) {
			// Si ambas condiciones son verdaderas, devuelve el objeto Usuario completo
			return usuarioEncontrado;
		} else {
			//Devuelve null e indica q el login fallo
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
	
	public void eliminarEquipo(Integer idEquipo) throws Exception { 
	    controlPersis.equipoClienteDao.delete(idEquipo); 
	    
	}
	
	public void editarEquipo(EquipoCliente equipoCliente) {
		controlPersis.equipoClienteDao.update(equipoCliente);
	}
	
	public boolean clienteTieneEquipos(Cliente cliente) {
		List<EquipoCliente> equipos = controlPersis.equipoClienteDao.findByCliente(cliente);
		
		return (equipos != null && !equipos.isEmpty());
	}
	
	public EquipoCliente traerEquipo(Integer idEquipo) {
		return controlPersis.equipoClienteDao.find(idEquipo);
	}
	public List<EquipoCliente>traerEquiposPorCliente(Cliente cliente) {
	    
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
        
        
        ConsultaTecnica consulta = controlPersis.consultaTecnicaDao.find(idConsulta);

        
        if (consulta == null) {
            throw new Exception("La consulta con ID " + idConsulta + " no fue encontrada.");
        }
       
        if (consulta.getTecnico() != null || consulta.getEstado() != EstadoConsulta.PENDIENTE) {
             throw new Exception("Esta consulta ya no está pendiente o fue tomada por otro técnico.");
        }
        if (solucion == null || solucion.trim().isEmpty()) {
            throw new Exception("La solución no puede estar vacía.");
        }

        
        consulta.setTecnico(tecnicoLogueado); 
        consulta.setSolucion(solucion);       
        consulta.setEstado(EstadoConsulta.RESUELTA); 

        
        controlPersis.consultaTecnicaDao.update(consulta); 
        
        
    }

public Tecnico traerTecnicoCompleto(Integer idUsuario) {
	
	return controlPersis.tecnicoDao.find(idUsuario);
}

public List<SolicitudServicio> traerSolicitudesActivasPorTecnico(Tecnico tecnicoLogueado) {
	
	return controlPersis.solicitudServicioDao.findSolicitudesActivasByTecnico(tecnicoLogueado);
}

public List<Cliente> traerTodosLosClientes() {
	
	return controlPersis.clienteDao.findAll();
}

public List<Tecnico> traerTodosLosTecnicos() {
	
	return controlPersis.tecnicoDao.findAll();
}

public List<Administrador> traerTodosLosAdmins() {
	
	return controlPersis.adminDao.findAll();
}

public void editarCliente(Cliente cli) {
	controlPersis.clienteDao.update(cli);
	
}

public void editarTecnico(Tecnico tec) {
	controlPersis.tecnicoDao.update(tec);
	
}

public Administrador traerAdminCompleto(Integer idUsuario) {
	
	return controlPersis.adminDao.find(idUsuario);
}

public void editarAdmin(Administrador adm) {
	controlPersis.adminDao.update(adm);
	
}

public void crearAdmin(Administrador adm) {
	controlPersis.adminDao.create(adm);
	
}

public void eliminarCliente(Integer idUsuarioDel) {
	controlPersis.clienteDao.delete(idUsuarioDel);
	
}

public void eliminarTecnico(Integer idUsuarioDel) {
	controlPersis.tecnicoDao.delete(idUsuarioDel);
	
}

public void eliminarAdmin(Integer idUsuarioDel) {
	controlPersis.adminDao.delete(idUsuarioDel);
	
}

public long contarAdmins() {
	return controlPersis.adminDao.countAll();
}

public void editarServicio(Servicio servicioEditado) {
	controlPersis.servicioDao.update(servicioEditado);
	
}

public void eliminarServicio(Integer idServicioDel) {
	controlPersis.servicioDao.delete(idServicioDel);
	
}

public List<SolicitudServicio> traerSolicitudesSinAsignar() {
	
	return controlPersis.solicitudServicioDao.findSolicitudesSinAsignar();
}

public List<Tecnico> traerTecnicosActivos() {
	
	return controlPersis.tecnicoDao.findTecnicosActivos();
}

public Long obtenerCargaTecnico(Tecnico tec) {
	
	return controlPersis.solicitudServicioDao.countActiveSolicitudesByTecnico(tec);
}

public void asignarTecnicoASolicitud(Integer idSolicitud, Integer idTecnico) throws Exception {
    SolicitudServicio solicitud = controlPersis.solicitudServicioDao.find(idSolicitud);
    Tecnico tecnico = controlPersis.tecnicoDao.find(idTecnico);

    if (solicitud == null || tecnico == null) {
        throw new Exception("Solicitud o Técnico no encontrado.");
    }
    
    
    solicitud.setTecnico(tecnico);
    
    solicitud.setEstado(EstadoSolicitud.EN_DIAGNOSTICO); 

    
    controlPersis.solicitudServicioDao.update(solicitud);
}

public void actualizarEstadoSolicitud(Integer idSolicitud, EstadoSolicitud nuevoEstado, String diagnosticoTecnico) throws Exception {
    
    
    SolicitudServicio solicitud = controlPersis.solicitudServicioDao.find(idSolicitud);

    if (solicitud == null) {
        throw new Exception("La solicitud con ID " + idSolicitud + " no fue encontrada.");
    }

    
    solicitud.setEstado(nuevoEstado);
    
    
    if (diagnosticoTecnico != null && !diagnosticoTecnico.trim().isEmpty()) {
        
        
        solicitud.setDiagnosticoTecnico(diagnosticoTecnico); 
        
        
    }
    
    

    
    controlPersis.solicitudServicioDao.update(solicitud);
}
//--------------------GESTION TURNOS---------------------------------

public List<HorarioLaboral> traerHorariosLaborales() {
	
	return controlPersis.horarioLaboralDao.findAll();
}

public AgendaConfiguracion traerConfiguracionAgenda() {
	
	return controlPersis.agendaConfigDao.find(1);
}

//--- Capa 2: Feriados ---
public List<DiaNoLaboral> traerDiasNoLaborales() {
	
	return controlPersis.diaNoLaboralDao.findAll();
}



//--- MÉTODOS DE ESCRITURA PARA AGENDA ---

public void actualizarConfiguracionAgenda(int intervaloMinutos) {
// 1. Trae la configuración existente (ID=1)
	AgendaConfiguracion config = controlPersis.agendaConfigDao.find(1);

// 2. Modifica el valor
	config.setIntervaloMinutos(intervaloMinutos);

// 3. Guarda los cambios en la BD
	controlPersis.agendaConfigDao.update(config);
}

public void actualizarHorario(int diaSemana, String horaInicio, String horaFin) {
// 1. Busca el horario para ese día
// (Asumimos que los 7 días ya existen en la BD)
	HorarioLaboral horario = controlPersis.horarioLaboralDao.find(diaSemana);

// 2. Convierte los String a LocalTime.
//    Si el string está vacío (""), lo guarda como NULL (cerrado).
	horario.setHoraInicio(horaInicio.isEmpty() ? null : java.time.LocalTime.parse(horaInicio));
	horario.setHoraFin(horaFin.isEmpty() ? null : java.time.LocalTime.parse(horaFin));

// 3. Guarda los cambios en la BD
	controlPersis.horarioLaboralDao.update(horario);
}

public void agregarDiaNoLaboral(String fecha, String descripcion) {
// 1. Crea una nueva entidad
	DiaNoLaboral nuevoFeriado = new DiaNoLaboral();

// 2. Setea los valores convirtiendo el String a LocalDate
	nuevoFeriado.setFecha(java.time.LocalDate.parse(fecha));
	nuevoFeriado.setDescripcion(descripcion);

// 3. Lo crea en la BD
	controlPersis.diaNoLaboralDao.create(nuevoFeriado);
}

public void borrarDiaNoLaboral(int idFeriado) {
// El GenericDao ya nos da un método 'delete'
	controlPersis.diaNoLaboralDao.delete(idFeriado);
}
	

//------------------------------GESTION BLOQUEOS TECNICOS (HORARIOS)----------------------------------
public List<BloqueoTecnico> traerBloqueosPorTecnico(Tecnico tecnico) {
	return controlPersis.bloqueoTecnicoDao.findBloqueosByTecnico(tecnico);
}

public void crearBloqueoTecnico(Tecnico tecnico, String inicioISO, String finISO, String motivo) {
    BloqueoTecnico nuevoBloqueo = new BloqueoTecnico();
    nuevoBloqueo.setTecnico(tecnico);
    
    // ⬇️ --- LÍNEAS MODIFICADAS --- ⬇️
    // Convertimos el String ISO (que SÍ tiene zona horaria)
    // a un objeto LocalDateTime (que NO tiene zona horaria, es "local")
    nuevoBloqueo.setFechaHoraInicio(OffsetDateTime.parse(inicioISO).toLocalDateTime());
    nuevoBloqueo.setFechaHoraFin(OffsetDateTime.parse(finISO).toLocalDateTime());
    // ⬆️ --- FIN DE LÍNEAS MODIFICADAS --- ⬆️
    
    nuevoBloqueo.setMotivo(motivo);
    
    controlPersis.bloqueoTecnicoDao.create(nuevoBloqueo);
}

public void borrarBloqueoTecnico(int idBloqueo) {
	controlPersis.bloqueoTecnicoDao.delete(idBloqueo);
}

public List<TurnoDTO> traerTurnosReservadosDTOs() {
    // 1. Llama al nuevo método del DAO
    List<Turno> turnosJPA = controlPersis.turnoDao.findTurnosReservadosFuturos();
    
    // 2. Traduce la lista de Entidades a DTOs
    List<TurnoDTO> turnosDTO = new ArrayList<>();
    
    for (Turno t : turnosJPA) {
        String titulo = "Reservado"; // Título simple
        
        
        
        turnosDTO.add(new TurnoDTO(
            t.getIdTurno(),
            t.getFechaHoraInicio(),
            t.getFechaHoraFin(),
            titulo 
        ));
    }
    
    return turnosDTO;
}

//CEREBRO
public List<SlotDTO> traerSlotsDisponibles() {
    
    // --- 1. Obtener todas las Reglas y Datos ---
    
    
    AgendaConfiguracion config = controlPersis.agendaConfigDao.find(1);
    List<HorarioLaboral> horarios = controlPersis.horarioLaboralDao.findAll();
    int intervalo = config.getIntervaloMinutos();

    
    Set<LocalDate> diasFeriados = new HashSet<>();
    for (DiaNoLaboral dia : controlPersis.diaNoLaboralDao.findAll()) {
        diasFeriados.add(dia.getFecha());
    }

    
    List<BloqueoTecnico> bloqueos = controlPersis.bloqueoTecnicoDao.findAllBloqueosFuturos();
    List<Turno> turnosReservados = controlPersis.turnoDao.findTurnosReservadosFuturos();

    
    
    
    List<SlotDTO> slotsDisponibles = new ArrayList<>();
    LocalDate hoy = LocalDate.now();
    DateTimeFormatter formatoTitulo = DateTimeFormatter.ofPattern("HH:mm");

    // Generamos slots para los próximos 30 días
    for (int i = 0; i < 30; i++) {
        LocalDate diaActual = hoy.plusDays(i);
        DayOfWeek diaSemanaJava = diaActual.getDayOfWeek(); // Ej: MONDAY

        // --- Filtro Capa 2 (Feriados) ---
        if (diasFeriados.contains(diaActual)) {
            continue; // Es feriado, saltar este día
        }

        // --- Filtro Capa 1 (Horario Laboral) ---
        // Buscamos la regla del admin para este día de la semana
        HorarioLaboral reglaDia = null;
        for (HorarioLaboral h : horarios) {
            // (Java: 1=Lunes, 7=Domingo) (BD: 1=Lunes, 7=Domingo)
            if (h.getDiaSemana() == diaSemanaJava.getValue()) {
                reglaDia = h;
                break;
            }
        }

        // Si no hay regla o está cerrado (NULL), saltar este día
        if (reglaDia == null || reglaDia.getHoraInicio() == null || reglaDia.getHoraFin() == null) {
            continue;
        }

        // --- 3. Generar Slots para este día ---
        LocalTime slotInicio = reglaDia.getHoraInicio();
        
        while (slotInicio.isBefore(reglaDia.getHoraFin())) {
            LocalDateTime slotInicioFull = LocalDateTime.of(diaActual, slotInicio);
            LocalDateTime slotFinFull = slotInicioFull.plusMinutes(intervalo);
            
            // (No generar slots en el pasado)
            if (slotInicioFull.isBefore(LocalDateTime.now())) {
                slotInicio = slotInicio.plusMinutes(intervalo);
                continue;
            }

            // --- Filtro Capa 3 (Bloqueos Técnicos) ---
            boolean estaBloqueado = false;
            for (BloqueoTecnico b : bloqueos) {
                // Verificamos si nuestro slot [Inicio-Fin] se superpone con un bloqueo [b.Inicio-b.Fin]
                if (slotInicioFull.isBefore(b.getFechaHoraFin()) && slotFinFull.isAfter(b.getFechaHoraInicio())) {
                    estaBloqueado = true;
                    break;
                }
            }
            if (estaBloqueado) {
                slotInicio = slotInicio.plusMinutes(intervalo);
                continue; // El slot está bloqueado, saltar
            }

            // --- Filtro Capa 4 (Turnos Reservados) ---
            boolean estaReservado = false;
            for (Turno t : turnosReservados) {
                // Verificamos si nuestro slot se superpone con un turno ya reservado
                if (slotInicioFull.isBefore(t.getFechaHoraFin()) && slotFinFull.isAfter(t.getFechaHoraInicio())) {
                    estaReservado = true;
                    break;
                }
            }
            if (estaReservado) {
                slotInicio = slotInicio.plusMinutes(intervalo);
                continue; // El slot está reservado, saltar
            }

            // --- ¡SLOT DISPONIBLE! ---
            // Si pasó todos los filtros, lo añadimos a la lista
            slotsDisponibles.add(new SlotDTO(
                slotInicio.format(formatoTitulo), // Título: "09:00"
                slotInicioFull.toString(),       // Start: "2025-11-10T09:00:00"
                slotFinFull.toString()         // End: "2025-11-10T09:30:00"
            ));

            // Avanzamos al siguiente slot
            slotInicio = slotInicio.plusMinutes(intervalo);
        }
    }

    return slotsDisponibles;
}


}
	



