package com.quickfix.logic;

import java.util.List;

import com.quickfix.entities.*;
import com.quickfix.persistencia.ControladorPersistencia;

public class ControladorLogica {
	ControladorPersistencia controlPersis = new ControladorPersistencia();

	public void crearAdministrador(Administrador admin) {
		controlPersis.adminDao.create(admin);
	}

	public Usuario validarLogin(String email, String password) {

		// 2. Llama al método específico del UsuarioDao a través de la
		// ControladoraPersistencia
		Usuario usuarioEncontrado = controlPersis.usuarioDao.findByEmailAndPassword(email, password);

		// 3. (Opcional pero recomendado) Añade una capa extra de lógica:
		// Verifica si el usuario existe Y si está activo (estado = true).
		if (usuarioEncontrado != null && usuarioEncontrado.isEstado()) {
			// Si ambas condiciones son verdaderas, devuelve el objeto Usuario completo
			return usuarioEncontrado;
		} else {
			// Si el DAO devolvió null (no encontrado) o si el usuario está inactivo (estado
			// = false),
			// devuelve null para indicar que el login falló.
			return null;
		}
	}

	public void crearCliente(Cliente cli) {
		controlPersis.clienteDao.create(cli);
	}
	
	public Usuario obtenerUsuarioConEmail(String email) {
		 return controlPersis.usuarioDao.findByEmail(email);
	}
	
	public boolean clienteTieneEquipos(Cliente cliente) {
		List<EquipoCliente> equipos = controlPersis.equipoClienteDao.findByCliente(cliente);
		// Si la lista no es nula y tiene al menos un elemento, devuelve true.
		return (equipos != null && !equipos.isEmpty());
	}
	
	public List<EquipoCliente>traerEquiposPorCliente(Cliente cliente) {
	    // La lógica de negocio está aquí: solo llama al DAO
	    return controlPersis.equipoClienteDao.findByCliente(cliente);
	}
	public void crearEquipo(EquipoCliente equipo) {
		controlPersis.equipoClienteDao.create(equipo);
	}
	// ...

}
