package com.quickfix.test;

import com.quickfix.entities.Servicio;
import com.quickfix.logic.ControladorLogica;

public class TestServicios {

    public static void main(String[] args) {
        
        ControladorLogica controlLogica = new ControladorLogica();
        
        System.out.println("--- Creando Servicios Base (Catálogo) ---");

        try {
            // Servicio 1: Limpieza
            Servicio s1 = new Servicio();
            s1.setNombre("Limpieza y Mantenimiento Preventivo");
            s1.setDescripcion("Limpieza interna del equipo, cambio de pasta térmica y optimización de software.");
            s1.setCostoBase(5000.00);
            s1.setTiempoEstimado(3); // 3 horas
            controlLogica.crearServicio(s1);
            System.out.println("Servicio Creado: " + s1.getNombre());

            // Servicio 2: Instalación de S.O.
            Servicio s2 = new Servicio();
            s2.setNombre("Instalación y Formateo de Sistema Operativo");
            s2.setDescripcion("Instalación limpia de Windows/Linux, drivers y software esencial.");
            s2.setCostoBase(8500.00);
            s2.setTiempoEstimado(4); // 4 horas
            controlLogica.crearServicio(s2);
            System.out.println("Servicio Creado: " + s2.getNombre());

            // Servicio 3: Diagnóstico Avanzado
            Servicio s3 = new Servicio();
            s3.setNombre("Diagnóstico Avanzado de Hardware/Software");
            s3.setDescripcion("Revisión exhaustiva de componentes para problemas intermitentes o fallas mayores.");
            s3.setCostoBase(2000.00);
            s3.setTiempoEstimado(1); // 1 hora
            controlLogica.crearServicio(s3);
            System.out.println("Servicio Creado: " + s3.getNombre());

            System.out.println("\n--- 3 Servicios Insertados con Éxito ---");
            
        } catch (Exception e) {
            System.err.println("Error al insertar servicios. ¿La base de datos está corriendo?");
            e.printStackTrace();
        }
    }
}
