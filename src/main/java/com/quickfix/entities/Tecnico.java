package com.quickfix.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
@Entity
@PrimaryKeyJoinColumn(name = "idTecnico")
public class Tecnico extends Usuario {
	private String disponibilidad;
	private String especialidad;
	
	@OneToMany(mappedBy = "tecnico")
	private List<ConsultaTecnica> consultasTecnicas;
	
	@OneToMany(mappedBy = "tecnico")
	private List<SolicitudServicio>solicitudesServicios;
	
	
	
	
	
	
	
	
	public Tecnico() {
		super();
		this.consultasTecnicas = new ArrayList<>();
		this.solicitudesServicios = new ArrayList<>(); 
	}
	

	public Tecnico(String nombre, String apellido, String email, String contraseña, String telefono, String direccion,
			boolean estado, LocalDateTime fechaRegistro, String disponibilidad, String especialidad) {
		super(nombre, apellido, email, contraseña, telefono, direccion, estado, fechaRegistro);
		this.disponibilidad = disponibilidad;
		this.especialidad = especialidad;
		
		this.consultasTecnicas = new ArrayList<>(); 
		this.solicitudesServicios = new ArrayList<>(); 
	}
	public String getDisponibilidad() {
		return disponibilidad;
	}
	public void setDisponibilidad(String disponibilidad) {
		this.disponibilidad = disponibilidad;
	}
	public String getEspecialidad() {
		return especialidad;
	}
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}


	public List<ConsultaTecnica> getConsultasTecnicas() {
		return consultasTecnicas;
	}


	public void setConsultasTecnicas(List<ConsultaTecnica> consultasTecnicas) {
		this.consultasTecnicas = consultasTecnicas;
	}


	public List<SolicitudServicio> getSolicitudesServicios() {
		return solicitudesServicios;
	}


	public void setSolicitudesServicios(List<SolicitudServicio> solicitudesServicios) {
		this.solicitudesServicios = solicitudesServicios;
	}
	
	
	
}
