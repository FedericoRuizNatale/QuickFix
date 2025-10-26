package com.quickfix.test;

import java.time.LocalDateTime;

import com.quickfix.entities.Administrador;
import com.quickfix.logic.ControladorLogica;

public class TestAdmin {

	public static void main(String[] args) {
		ControladorLogica controlLogica = new ControladorLogica();
		
		Administrador admin = new Administrador();
        admin.setNombre("Federico");
        admin.setApellido("Ruiz Natale");
        admin.setEmail("federico@ruiznatale21.com");
        admin.setContraseña("12345"); // ¡Recuerda hashear esto en el futuro!
        admin.setTelefono("3411234567");
        admin.setDireccion("Calle Falsa 123");
        admin.setEstado(true); // Activo
        admin.setFechaRegistro(LocalDateTime.now()); // Fecha actual
        admin.setNivelAcceso(1); // Nivel de Admin
        
        try {
			controlLogica.crearAdministrador(admin);
			System.out.println("Admin Creado Exitosamente!");
			
			if (admin.getIdUsuario() != null) {
				System.out.println("El id asignado es: " + admin.getIdUsuario());
			}
			
		} catch (Exception e) {
			System.out.println("Error Admin no fue creado");
			e.printStackTrace();
		}

	}

}
