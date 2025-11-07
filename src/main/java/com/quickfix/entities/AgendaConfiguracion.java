package com.quickfix.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agenda_configuracion")
public class AgendaConfiguracion {

    @Id
    private Integer id;

    @Column(name = "intervalo_minutos", nullable = false)
    private int intervaloMinutos;

    // Getters y Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public int getIntervaloMinutos() {
        return intervaloMinutos;
    }
    public void setIntervaloMinutos(int intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }
}
