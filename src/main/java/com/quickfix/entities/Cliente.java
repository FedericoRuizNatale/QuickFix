package com.quickfix.entities;

import java.time.LocalDateTime;
import java.util.ArrayList; // Importa ArrayList
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "idCliente")
public class Cliente extends Usuario{
	
	private Integer puntosLealtad;
	private String dni;
	
	@OneToMany(mappedBy = "cliente")
    private List<EquipoCliente> equipos;
	
	@OneToMany(mappedBy = "cliente")
	private List<ConsultaTecnica> consultasTecnicas;
	@OneToMany(mappedBy = "cliente")
	private List<SolicitudServicio> solicitudesServicios;
	
	/**
	 * Constructor completo (Recomendado).
	 * Nota que quité la lista de equipos de los argumentos.
	 * Se asume que un cliente se crea sin equipos y se le añaden después.
	 */
	public Cliente(String nombre, String apellido, String email, String contraseña, String telefono, String direccion,
			boolean estado, LocalDateTime fechaRegistro, Integer puntosLealtad, String dni) {
		
		// Llama al constructor padre
		super(nombre, apellido, email, contraseña, telefono, direccion, estado, fechaRegistro);
		
		// Asigna los campos propios de Cliente
		this.puntosLealtad = puntosLealtad;
		this.dni = dni;
		
		// ✅ INICIALIZA LA LISTA
		this.equipos = new ArrayList<>(); 
		this.consultasTecnicas = new ArrayList<>(); 
		this.solicitudesServicios = new ArrayList<>(); 
	}
	
	/**
	 * Constructor vacío (Obligatorio para JPA).
	 */
	public Cliente() {
		super();
		// ✅ INICIALIZA LA LISTA
		this.equipos = new ArrayList<>(); 
		this.consultasTecnicas = new ArrayList<>(); 
		this.solicitudesServicios = new ArrayList<>(); 
	}
	
	
	
	
	public List<ConsultaTecnica> getConsultasTecnicas() {
		return consultasTecnicas;
	}

	public void setConsultasTecnicas(List<ConsultaTecnica> consultasTecnicas) {
		this.consultasTecnicas = consultasTecnicas;
	}

	public List<EquipoCliente> getEquipos() {
		return equipos;
	}
	public void setEquipos(List<EquipoCliente> equipos) {
		this.equipos = equipos;
	}
	public Integer getPuntosLealtad() {
		return puntosLealtad;
	}
	public void setPuntosLealtad(Integer puntosLealtad) {
		this.puntosLealtad = puntosLealtad;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	
	// --- Métodos de utilidad (Opcional pero muy recomendado) ---
	
	/**
	 * Método para añadir un solo equipo a la lista
	 * y mantener la consistencia de la relación.
	 */
	public void addEquipo(EquipoCliente equipo) {
		this.equipos.add(equipo);
		equipo.setCliente(this); // Esto mantiene ambos lados de la relación sincronizados
	}
}
