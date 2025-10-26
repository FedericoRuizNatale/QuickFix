package com.quickfix.servlets;

import java.io.IOException;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.logic.ControladorLogica; // Importamos la lógica
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EquipoServlet", urlPatterns = {"/cliente/EquipoServlet"})
public class EquipoClienteServlet extends HttpServlet {

    ControladorLogica controlLogica = new ControladorLogica();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Verificar la sesión del cliente (Seguridad)
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || miSesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // El usuario logueado es el dueño del equipo
        Cliente clienteLogueado = (Cliente) miSesion.getAttribute("usuarioLogueado");

        // --- 2. Captura de Datos del formulario miEquipo.jsp (Modal) ---
        String tipo = request.getParameter("tipo");
        String marca = request.getParameter("marca");
        String modelo = request.getParameter("modelo");
        String problemasFrecuentes = request.getParameter("problemasFrecuentes");
        
        // --- 3. Creación del Objeto EquipoCliente ---
        EquipoCliente nuevoEquipo = new EquipoCliente();
        nuevoEquipo.setTipo(tipo);
        nuevoEquipo.setMarca(marca);
        nuevoEquipo.setModelo(modelo);
        nuevoEquipo.setProblemasFrecuentes(problemasFrecuentes);
        
        // VINCULACIÓN: Asignar el cliente logueado al equipo
        nuevoEquipo.setCliente(clienteLogueado);

        // --- 4. Llama a la Lógica para Guardar ---
        try {
             controlLogica.crearEquipo(nuevoEquipo);
             
             // --- 5. ACTUALIZAR LA SESIÓN Y REDIRIGIR ---
             
             // Redirigir de vuelta a la página de equipos con un mensaje de éxito
             response.sendRedirect("miEquipo.jsp?exito=true"); 
             
        } catch (Exception e) {
            System.err.println("Error al registrar equipo: " + e.getMessage());
            response.sendRedirect("miEquipo.jsp?error=interno"); 
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirige a la página principal de gestión de equipos
        response.sendRedirect("miEquipo.jsp");
    }
}
