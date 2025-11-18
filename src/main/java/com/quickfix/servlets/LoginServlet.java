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
	
	
	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        
        Usuario usuarioValidado = controlLogica.validarLogin(email, password);
        
        if (usuarioValidado != null) {
            
            
            
            HttpSession miSesion = request.getSession(true); 
            miSesion.setAttribute("usuarioLogueado", usuarioValidado);
            
            
            String rol = usuarioValidado.getClass().getSimpleName();
            miSesion.setAttribute("rolUsuario", rol);
            
            
            response.sendRedirect("HomeRedirectServlet"); 
            
        } else {
            
            response.sendRedirect("login.jsp?error=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        response.sendRedirect("login.jsp");
    }

}
