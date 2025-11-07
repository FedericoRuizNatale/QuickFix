package com.quickfix.entities;

import java.time.LocalTime; // Importante: usar java.time
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "horario_laboral")
public class HorarioLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dia_semana", nullable = false, unique = true)
    private int diaSemana; // 1=Lunes, 7=Domingo

    @Column(name = "hora_inicio")
    private LocalTime horaInicio; // Se mapea con TIME

    @Column(name = "hora_fin")
    private LocalTime horaFin; // Se mapea con TIME

    // Getters y Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public int getDiaSemana() {
        return diaSemana;
    }
    public void setDiaSemana(int diaSemana) {
        this.diaSemana = diaSemana;
    }
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    public LocalTime getHoraFin() {
        return horaFin;
    }
    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}