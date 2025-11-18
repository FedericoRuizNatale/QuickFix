package com.quickfix.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//Servlet para redireccionar segun corresponda
@WebServlet(name = "HomeRedirectServlet", urlPatterns = {"/HomeRedirectServlet"})
public class HomeRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        HttpSession miSesion = request.getSession(false);
        
        
        if (miSesion == null || miSesion.getAttribute("rolUsuario") == null) {
            
            response.sendRedirect("login.jsp");
            return;
        }

        
        String rol = (String) miSesion.getAttribute("rolUsuario");
        
        
        
        switch (rol) {
            case "Administrador":
                
                response.sendRedirect("admin/homeAdmin.jsp");
                break;
            case "Cliente":
                
                response.sendRedirect("cliente/homeCliente.jsp");
                break;
            case "Tecnico":
                
                response.sendRedirect("tecnico/homeTecnico.jsp");
                break;
            default:
                //Rol desconocido
                response.sendRedirect("login.jsp?error=rol_desconocido");
                break;
        }
    }
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
