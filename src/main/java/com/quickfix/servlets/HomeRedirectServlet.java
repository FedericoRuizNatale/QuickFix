package com.quickfix.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet encargado únicamente de leer el rol del usuario desde la sesión 
 * y redirigir al Home específico (cliente, admin, tecnico).
 * Se accede después de un login exitoso.
 */
@WebServlet(name = "HomeRedirectServlet", urlPatterns = {"/HomeRedirectServlet"})
public class HomeRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener la sesión actual SIN crear una nueva (false)
        HttpSession miSesion = request.getSession(false);
        
        // 2. Verificación de Seguridad: ¿Existe la sesión y el rol?
        if (miSesion == null || miSesion.getAttribute("rolUsuario") == null) {
            // Si la sesión es inválida o no tiene el rol, volvemos al login
            response.sendRedirect("login.jsp");
            return;
        }

        // 3. Obtener el rol (String) guardado en el LoginServlet
        String rol = (String) miSesion.getAttribute("rolUsuario");
        
        // 4. Redirección basada en el String del rol
        
        switch (rol) {
            case "Administrador":
                // Asume que tu home de admin está en /admin/
                response.sendRedirect("admin/homeAdmin.jsp");
                break;
            case "Cliente":
                // Asume que tu home de cliente está en /cliente/
                response.sendRedirect("cliente/homeCliente.jsp");
                break;
            case "Tecnico":
                // Asume que tu home de técnico está en /tecnico/
                response.sendRedirect("tecnico/homeTecnico.jsp");
                break;
            default:
                // Rol desconocido o inesperado.
                response.sendRedirect("login.jsp?error=rol_desconocido");
                break;
        }
    }
    
    // Si alguien intenta acceder por POST, lo tratamos como GET
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
