package com.quickfix.entities;

import java.time.LocalDateTime;

import com.quickfix.enums.*;

import jakarta.persistence.*;
@Entity
public class SolicitudServicio {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idSolicitud;
	
	private LocalDateTime fechaHoraCreacion;
	@Column(columnDefinition="TEXT")
	private String diagnostico;
	@Enumerated(EnumType.STRING)
	private EstadoSolicitud estado;
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;
	
	@ManyToOne
	private Cliente cliente;
	
	@ManyToOne
	private Tecnico tecnico;
	
	@OneToOne
	@JoinColumn(name = "servicio_id")
	private Servicio servicio;
	
	@OneToOne
	@JoinColumn(name = "turno_id")
	private Turno turno;
	
	@OneToOne
    @JoinColumn(name = "consulta_origen_id")
    private ConsultaTecnica consultaDeOrigen;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public SolicitudServicio() {
		
	}

	public SolicitudServicio(LocalDateTime fechaHoraCreacion, String diagnostico, EstadoSolicitud estado,
			Prioridad prioridad, Cliente cliente, Tecnico tecnico, Servicio servicio, Turno turno,
			ConsultaTecnica consultaDeOrigen) {
		super();
		this.fechaHoraCreacion = fechaHoraCreacion;
		this.diagnostico = diagnostico;
		this.estado = estado;
		this.prioridad = prioridad;
		this.cliente = cliente;
		this.tecnico = tecnico;
		this.servicio = servicio;
		this.turno = turno;
		this.consultaDeOrigen = consultaDeOrigen;
	}

	public Integer getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Integer idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public LocalDateTime getFechaHoraCreacion() {
		return fechaHoraCreacion;
	}

	public void setFechaHoraCreacion(LocalDateTime fechaHoraCreacion) {
		this.fechaHoraCreacion = fechaHoraCreacion;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}

	public Prioridad getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Tecnico getTecnico() {
		return tecnico;
	}

	public void setTecnico(Tecnico tecnico) {
		this.tecnico = tecnico;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public Turno getTurno() {
		return turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	public ConsultaTecnica getConsultaDeOrigen() {
		return consultaDeOrigen;
	}

	public void setConsultaDeOrigen(ConsultaTecnica consultaDeOrigen) {
		this.consultaDeOrigen = consultaDeOrigen;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
