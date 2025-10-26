package com.quickfix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "equipo_cliente")
public class EquipoCliente {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idEquipo;
	private String tipo;
	private String marca;
	private String modelo;
	@Column(columnDefinition="TEXT")
	private String problemasFrecuentes;
	@ManyToOne
	private Cliente cliente;
	
	
	
	
	public Integer getIdEquipo() {
		return idEquipo;
	}
	public void setIdEquipo(Integer idEquipo) {
		this.idEquipo = idEquipo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public String getProblemasFrecuentes() {
		return problemasFrecuentes;
	}
	public void setProblemasFrecuentes(String problemasFrecuentes) {
		this.problemasFrecuentes = problemasFrecuentes;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public EquipoCliente(String tipo, String marca, String modelo, String problemasFrecuentes, Cliente cliente) {
		super();
		this.tipo = tipo;
		this.marca = marca;
		this.modelo = modelo;
		this.problemasFrecuentes = problemasFrecuentes;
		this.cliente = cliente;
	}
	
	public EquipoCliente() {
		
	}
	
	
	
	
	
	
	
	
}
