package com.quickfix.dto;

import java.time.LocalDateTime;

/**
 * Un DTO (Data Transfer Object) simple para enviar los datos
 * del bloqueo al calendario, evitando el HibernateProxy.
 */
public class BloqueoDTO {
    
    private Integer id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String motivo;

    // Constructor para la "traducción"
    public BloqueoDTO(Integer id, LocalDateTime inicio, LocalDateTime fin, String motivo) {
        this.id = id;
        this.fechaHoraInicio = inicio;
        this.fechaHoraFin = fin;
        this.motivo = motivo;
    }

    // Getters (GSON los necesita para leer los valores)
    public Integer getId() {
        return id;
    }
    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }
    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }
    public String getMotivo() {
        return motivo;
    }
}