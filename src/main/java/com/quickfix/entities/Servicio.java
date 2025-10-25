package com.quickfix.entities;

import jakarta.persistence.*;

@Entity
public class Servicio {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idServicio;
	
	private Integer tiempoEstimado;
	private String nombre;
	private String descripcion;
	private Double costoBase;
	
	@OneToOne (mappedBy = "servicio")
	private SolicitudServicio solicitudServicio;

	
	
	
	
	public Servicio() {
		
	}

	public Servicio(Integer tiempoEstimado, String nombre, String descripcion, Double costoBase,
			SolicitudServicio solicitudServicio) {
		super();
		this.tiempoEstimado = tiempoEstimado;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.costoBase = costoBase;
		this.solicitudServicio = solicitudServicio;
	}

	public Integer getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(Integer idServicio) {
		this.idServicio = idServicio;
	}

	public Integer getTiempoEstimado() {
		return tiempoEstimado;
	}

	public void setTiempoEstimado(Integer tiempoEstimado) {
		this.tiempoEstimado = tiempoEstimado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getCostoBase() {
		return costoBase;
	}

	public void setCostoBase(Double costoBase) {
		this.costoBase = costoBase;
	}

	public SolicitudServicio getSolicitudServicio() {
		return solicitudServicio;
	}

	public void setSolicitudServicio(SolicitudServicio solicitudServicio) {
		this.solicitudServicio = solicitudServicio;
	}
	
	
	
	
	
	
	
}
