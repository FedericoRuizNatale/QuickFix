package com.quickfix.entities;

import java.time.LocalDateTime;

import com.quickfix.enums.EstadoTurno;

import jakarta.persistence.*;

@Entity
public class Turno {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idTurno;
	
	private LocalDateTime fechaHoraInicio;
	private LocalDateTime fechaHoraFin;
	
	@Enumerated(EnumType.STRING)
	private EstadoTurno estado;
	
	@OneToOne(mappedBy = "turno")
	private SolicitudServicio solicitudServicio;

	
	
	
	
	public Turno() {}
	
	
	public Turno(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, EstadoTurno estado,
			SolicitudServicio solicitudServicio) {
		super();
		this.fechaHoraInicio = fechaHoraInicio;
		this.fechaHoraFin = fechaHoraFin;
		this.estado = estado;
		this.solicitudServicio = solicitudServicio;
	}

	public Integer getIdTurno() {
		return idTurno;
	}

	public void setIdTurno(Integer idTurno) {
		this.idTurno = idTurno;
	}

	public LocalDateTime getFechaHoraInicio() {
		return fechaHoraInicio;
	}

	public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
		this.fechaHoraInicio = fechaHoraInicio;
	}

	public LocalDateTime getFechaHoraFin() {
		return fechaHoraFin;
	}

	public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
		this.fechaHoraFin = fechaHoraFin;
	}

	public EstadoTurno getEstado() {
		return estado;
	}

	public void setEstado(EstadoTurno estado) {
		this.estado = estado;
	}

	public SolicitudServicio getSolicitudServicio() {
		return solicitudServicio;
	}

	public void setSolicitudServicio(SolicitudServicio solicitudServicio) {
		this.solicitudServicio = solicitudServicio;
	}
	
	
	
	
	
	
	
	
	
}
