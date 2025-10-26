package com.quickfix.entities;

import java.time.LocalDateTime;

import com.quickfix.enums.EstadoConsulta;

import jakarta.persistence.*;

@Entity
@Table(name = "consulta_tecnica")
public class ConsultaTecnica {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idConsulta;
	@Column(columnDefinition="TEXT")
	private String descripcionProblema;
	@Column(columnDefinition="TEXT")
	private String solucion;
	
	@Enumerated(EnumType.STRING) 
    private EstadoConsulta estado;
	
	private LocalDateTime fechaHora;
	
	@ManyToOne
	private Tecnico tecnico;
	
	@ManyToOne
	private Cliente cliente;
	
	
	@OneToOne(mappedBy = "consultaDeOrigen")
	private SolicitudServicio solicitudServicio;


	
    
	
	
	public ConsultaTecnica() {
		
	}

	

	public ConsultaTecnica(Integer idConsulta, String descripcionProblema, String solucion, EstadoConsulta estado,
			LocalDateTime fechaHora, Tecnico tecnico, Cliente cliente, SolicitudServicio solicitudServicio) {
		super();
		this.idConsulta = idConsulta;
		this.descripcionProblema = descripcionProblema;
		this.solucion = solucion;
		this.estado = estado;
		this.fechaHora = fechaHora;
		this.tecnico = tecnico;
		this.cliente = cliente;
		this.solicitudServicio = solicitudServicio;
	}



	public LocalDateTime getFechaHora() {
		return fechaHora;
	}



	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}



	public Integer getIdConsulta() {
		return idConsulta;
	}

	public void setIdConsulta(Integer idConsulta) {
		this.idConsulta = idConsulta;
	}

	public String getDescripcionProblema() {
		return descripcionProblema;
	}

	public void setDescripcionProblema(String descripcionProblema) {
		this.descripcionProblema = descripcionProblema;
	}

	public String getSolucion() {
		return solucion;
	}

	public void setSolucion(String solucion) {
		this.solucion = solucion;
	}

	public EstadoConsulta getEstado() {
		return estado;
	}

	public void setEstado(EstadoConsulta estado) {
		this.estado = estado;
	}

	public Tecnico getTecnico() {
		return tecnico;
	}

	public void setTecnico(Tecnico tecnico) {
		this.tecnico = tecnico;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public SolicitudServicio getSolicitudServicio() {
		return solicitudServicio;
	}

	public void setSolicitudServicio(SolicitudServicio solicitudServicio) {
		this.solicitudServicio = solicitudServicio;
	}
	
	
}
