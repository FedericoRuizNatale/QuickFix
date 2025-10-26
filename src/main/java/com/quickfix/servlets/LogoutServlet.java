package com.quickfix.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet encargado de cerrar la sesión del usuario.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener la sesión actual (si existe)
        HttpSession miSesion = request.getSession(false); // Usamos 'false' para NO crear una sesión si no existe
        
        if (miSesion != null) {
            // 2. Invalidar la sesión: Esto borra todos los atributos (usuarioLogueado, rolUsuario)
            //    y destruye la sesión en el servidor.
            miSesion.invalidate();
        }
        
        // 3. Redirigir al usuario al index.jsp público
        // La ruta es a la raíz de la aplicación para ir al index.
        response.sendRedirect("index.jsp"); 
    }
    
    // Lo manejamos solo con GET porque los botones de "Salir" usan un simple enlace <a>
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}