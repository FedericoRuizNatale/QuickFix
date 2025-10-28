package com.quickfix.enums;

public enum EstadoTurno {
    // ESTADOS NUEVOS (Se crean con este estado)
    NO_DISPONIBLE,     // El turno existe pero no ha sido publicado/habilitado por el Admin/Tecnico
    
    // ESTADOS DE USO NORMAL
    DISPONIBLE,        // El turno está publicado y listo para ser reservado por un cliente
    RESERVADO,         // El cliente lo ha tomado y está en espera de la cita
    
    // ESTADOS FINALES
    COMPLETADO,        // La cita se llevó a cabo y el trabajo está en manos del técnico
    CANCELADO,
    AUSENTE
}