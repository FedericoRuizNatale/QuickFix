package com.quickfix.servlets;

import java.io.IOException;
import java.time.LocalDateTime;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.Usuario;
import com.quickfix.logic.ControladorLogica; // Importamos la lógica
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // --- 1. CAPTURA DE DATOS ---
        // Usamos request.getParameter() con los NOMBRES exactos del JSP
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String dni = request.getParameter("dni");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        
        // --- 2. LÓGICA DE NEGOCIO (Validación de Email) ---
        // Debes hacer un método en ControladoraLogica que verifique si el email existe
        Usuario usuario = controlLogica.obtenerUsuarioConEmail(email);
        if (usuario != null) {
            // Si el email ya está, redirigimos con un mensaje de error
            response.sendRedirect("register.jsp?error=email");
            return; // Detenemos la ejecución del servlet
        }

        // --- 3. CREACIÓN DEL OBJETO Y ASIGNACIÓN DE VALORES ---
        // Los datos de estado, fecha y puntosLealtad se asignan aquí o en el constructor
        
        Cliente nuevoCliente = new Cliente();
        
        // Datos de Usuario (Clase Padre)
        nuevoCliente.setNombre(nombre);
        nuevoCliente.setApellido(apellido);
        nuevoCliente.setEmail(email);
        nuevoCliente.setContraseña(password); // ¡Recuerda hashear esto en un proyecto real!
        nuevoCliente.setTelefono(telefono);
        nuevoCliente.setDireccion(direccion);
        nuevoCliente.setEstado(true); // Activo por defecto
        nuevoCliente.setFechaRegistro(LocalDateTime.now()); 
        
        // Datos de Cliente (Clase Hija)
        nuevoCliente.setDni(dni);
        nuevoCliente.setPuntosLealtad(0); // Puntos iniciales
        
        // --- 4. LLAMADA A LA LÓGICA PARA GUARDAR ---
        try {
             controlLogica.crearCliente(nuevoCliente);
             
             // --- 5. REDIRECCIÓN DE ÉXITO ---
             // Redirigimos a login.jsp con el parámetro de éxito para mostrar el mensaje
             response.sendRedirect("login.jsp?exito=true"); 
             
        } catch (Exception e) {
            // Manejo de errores de persistencia (ej: problema de conexión)
            System.err.println("Error al registrar cliente: " + e.getMessage());
            response.sendRedirect("register.jsp?error=interno"); 
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si alguien accede a /RegisterServlet directamente, lo mandamos al formulario
        response.sendRedirect("register.jsp");
    }
}
