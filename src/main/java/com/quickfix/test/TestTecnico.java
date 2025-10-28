package com.quickfix.test;

import java.time.LocalDateTime;

import com.quickfix.entities.Tecnico;
import com.quickfix.logic.ControladorLogica;

public class TestTecnico {

	public static void main(String[] args) {
		ControladorLogica controlLogica = new ControladorLogica();
		
		Tecnico tecnico = new Tecnico();
        tecnico.setNombre("Federico");
        tecnico.setApellido("Ruiz");
        tecnico.setEmail("fedeTecnico@gmail.com");
        tecnico.setContraseña("12345"); 
        tecnico.setTelefono("3411234564");
        tecnico.setDireccion("Calle Falsa 123");
        tecnico.setEstado(true); 
        tecnico.setFechaRegistro(LocalDateTime.now()); 
        
        // Atributos específicos de Tecnico
        tecnico.setDisponibilidad("L-V 9-18hs"); // Un horario de ejemplo
        tecnico.setEspecialidad("Hardware y Redes"); // ✅ Añadir la especialidad

        try {
            // Asume que este método existe en ControladoraLogica
			controlLogica.crearTecnico(tecnico); 
			System.out.println("Tecnico Creado Exitosamente!");
			
			if (tecnico.getIdUsuario() != null) {
				System.out.println("El id asignado es: " + tecnico.getIdUsuario());
			}
			
		} catch (Exception e) {
			System.out.println("Error Tecnico no fue creado");
			e.printStackTrace();
		}
	}
}
