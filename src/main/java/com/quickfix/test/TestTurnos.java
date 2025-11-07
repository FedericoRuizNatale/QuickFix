//package com.quickfix.test;
//
//import com.quickfix.entities.Turno;
//import com.quickfix.enums.EstadoTurno;
//import com.quickfix.logic.ControladorLogica;
//import java.time.DayOfWeek;
//import java.time.LocalDateTime;
//import java.time.temporal.TemporalAdjusters;
//
//public class TestTurnos {
//
//    public static void main(String[] args) {
//        
//        ControladorLogica controlLogica = new ControladorLogica();
//        
//        // --- 1. Definición del rango de turnos ---
//        
//        // Empieza en el próximo lunes a las 9:00 AM
//        LocalDateTime hoy = LocalDateTime.now();
//        LocalDateTime proximoLunes = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).toLocalDate().atTime(9, 0);
//
//        // Rango de trabajo
//        int horaInicio = 9;
//        int horaFin = 20; // 8 PM
//        int duracionTurno = 1; // Turnos de 2 horas
//
//        System.out.println("--- Generando Turnos de Prueba (DISPONIBLES) ---");
//        
//        // Generar turnos de Lunes a Viernes
//        for (int dia = 0; dia < 5; dia++) { 
//            
//            LocalDateTime fechaTurno = proximoLunes.plusDays(dia);
//            
//            // Generar franjas horarias
//            for (int hora = horaInicio; hora < horaFin; hora += duracionTurno) {
//                
//                LocalDateTime inicio = fechaTurno.withHour(hora).withMinute(0);
//                LocalDateTime fin = inicio.plusHours(duracionTurno);
//                
//                if (fin.getHour() <= horaFin) { 
//                    
//                    Turno nuevoTurno = new Turno();
//                    nuevoTurno.setFechaHoraInicio(inicio);
//                    nuevoTurno.setFechaHoraFin(fin);
//                    
//                    // CLAVE PARA LA PRUEBA: Se inserta como DISPONIBLE para que el cliente lo vea
//                    nuevoTurno.setEstado(EstadoTurno.DISPONIBLE); 
//                    
//                    try {
//                        controlLogica.crearTurno(nuevoTurno);
//                        System.out.println("Creado: " + inicio.getDayOfWeek() + " " + inicio.toLocalTime());
//                    } catch (Exception e) {
//                        System.err.println("Error al guardar turno: " + e.getMessage());
//                    }
//                }
//            }
//        }
//        
//        System.out.println("\n--- Proceso Finalizado ---");
//    }
//}
