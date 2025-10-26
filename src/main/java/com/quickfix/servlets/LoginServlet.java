package com.quickfix.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.quickfix.logic.ControladorLogica; 
import com.quickfix.entities.Usuario;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
	
	// Instancia de la Controladora Lógica
    ControladorLogica controlLogica = new ControladorLogica();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // --- 1. Obtener los datos del formulario ---
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // --- 2. Validar el usuario ---
        Usuario usuarioValidado = controlLogica.validarLogin(email, password);
        
        if (usuarioValidado != null) {
            // LOGIN EXITOSO
            
            // 3. Crear la Sesión
            HttpSession miSesion = request.getSession(true); 
            miSesion.setAttribute("usuarioLogueado", usuarioValidado);
            
            // 4. Guardar el ROL como String simple en la sesión (ej: "Administrador")
            String rol = usuarioValidado.getClass().getSimpleName();
            miSesion.setAttribute("rolUsuario", rol);
            
            // 5. Redirigir al Servlet que maneja la redirección de ROLES
            response.sendRedirect("HomeRedirectServlet"); 
            
        } else {
            // LOGIN FALLIDO
            // Redirigir de vuelta al login con mensaje de error
            response.sendRedirect("login.jsp?error=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirige al login si se accede directamente por GET
        response.sendRedirect("login.jsp");
    }

}
