package com.quickfix.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "idAdmin")
public class Administrador extends Usuario{
	private Integer nivelAcceso;

	public Integer getNivelAcceso() {
		return nivelAcceso;
	}

	public void setNivelAcceso(Integer nivelAcceso) {
		this.nivelAcceso = nivelAcceso;
	}

	public Administrador(String nombre, String apellido, String email, String contraseña, String telefono,
			String direccion, boolean estado, LocalDateTime fechaRegistro, Integer nivelAcceso) {
		super(nombre, apellido, email, contraseña, telefono, direccion, estado, fechaRegistro);
		this.nivelAcceso = nivelAcceso;
	}
	
	
	public Administrador() {
		super();
	}
	
}
