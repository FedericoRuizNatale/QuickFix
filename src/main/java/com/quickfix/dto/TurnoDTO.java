package com.quickfix.dto;

import java.time.LocalDateTime;

/**
 * DTO para enviar Turnos Reservados al calendario.
 */
public class TurnoDTO {
    
    private Integer id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String titulo; // Ej: "Reservado"

    public TurnoDTO(Integer id, LocalDateTime inicio, LocalDateTime fin, String titulo) {
        this.id = id;
        this.fechaHoraInicio = inicio;
        this.fechaHoraFin = fin;
        this.titulo = titulo;
    }

    // Getters
    public Integer getId() { return id; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public String getTitulo() { return titulo; }
}