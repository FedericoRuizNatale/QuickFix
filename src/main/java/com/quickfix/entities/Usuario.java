package com.quickfix.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // La estrategia de herencia
public abstract class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idUsuario;
	
	
	private String nombre;
	private String apellido;
	private String email;
	private String contraseña;
	private String telefono;
	private String direccion;
	private boolean estado;
	private LocalDateTime fechaRegistro;
	
	
	
	
	
	
	
	
	
	

	
	public Usuario(String nombre, String apellido, String email, String contraseña, String telefono, String direccion,
			boolean estado, LocalDateTime fechaRegistro) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.contraseña = contraseña;
		this.telefono = telefono;
		this.direccion = direccion;
		this.estado = estado;
		this.fechaRegistro = fechaRegistro;
	}


	public Usuario() {
		
	}


	
	
	
	
	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}


	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public boolean isEstado() {
		return estado;
	}


	public void setEstado(boolean estado) {
		this.estado = estado;
	}


	
	public Integer getIdUsuario() {
		return idUsuario;
	}


	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}


	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContraseña() {
		return contraseña;
	}
	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}


	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", nombre=" + nombre + ", apellido=" + apellido + ", email=" + email
				+ ", contraseña=" + contraseña + ", telefono=" + telefono + ", direccion=" + direccion + "]";
	}
	
	
	
	
	
}
