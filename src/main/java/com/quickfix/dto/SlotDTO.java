package com.quickfix.dto;

/**
 * DTO para enviar un slot de turno disponible al FullCalendar del cliente.
 */
public class SlotDTO {
    
    private String title; // Ej: "09:00 - 09:30"
    private String start; // Ej: "2025-11-10T09:00:00"
    private String end;   // Ej: "2025-11-10T09:30:00"

    public SlotDTO(String title, String start, String end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    // Getters (necesarios para GSON)
    public String getTitle() { return title; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
}
