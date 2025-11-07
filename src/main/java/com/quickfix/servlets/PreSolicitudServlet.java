package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Servicio;
import com.quickfix.entities.Turno;
import com.quickfix.logic.ControladorLogica;
import com.quickfix.entities.Usuario; // CLASE PADRE
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PreSolicitudServlet", urlPatterns = {"/cliente/PreSolicitudServlet"})
public class PreSolicitudServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession miSesion = request.getSession(false);
        
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // 1. OBTENER EL USUARIO DE LA SESIÓN (Objeto de la superclase)
        Usuario usuarioLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
        
        // 2. BUSCAR EL OBJETO CLIENTE COMPLETO EN LA BD
        Cliente clienteLogueado = null; // Inicializar a null por seguridad
        if (usuarioLogueado != null) {
            clienteLogueado = controlLogica.traerClienteCompleto(usuarioLogueado.getIdUsuario());
        }

        if (clienteLogueado == null) {
            // Si el cliente no se encuentra (raro si el login funcionó), redirigir
            response.sendRedirect("../login.jsp?error=cliente_no_encontrado");
            return;
        }

        try {
            // A. Traer Equipos del cliente
            List<EquipoCliente> listaEquipos = controlLogica.traerEquiposPorCliente(clienteLogueado);
            
            // B. Traer Catálogo de Servicios
            List<Servicio> listaServicios = controlLogica.traerTodosLosServicios();
            
            // C. Traer Turnos Disponibles
            List<Turno> listaTurnosDisponibles = controlLogica.traerTurnosDisponibles();
            
            // --- ✅ LÍNEAS DE DEPURACIÓN AGREGADAS ---
            System.out.println("--- DEBUG PreSolicitudServlet ---");
            System.out.println("Cliente ID Obtenido: " + clienteLogueado.getIdUsuario()); 
            System.out.println("Equipos encontrados para este cliente: " + (listaEquipos != null ? listaEquipos.size() : "null"));
            System.out.println("Servicios totales encontrados: " + (listaServicios != null ? listaServicios.size() : "null"));
            System.out.println("Turnos Disponibles encontrados: " + (listaTurnosDisponibles != null ? listaTurnosDisponibles.size() : "null"));
            System.out.println("---------------------------------");
            // --- FIN DEBUG ---

            // 3. ATRIBUTOS DE REQUEST
            request.setAttribute("listaEquipos", listaEquipos);
            request.setAttribute("listaServicios", listaServicios);
            request.setAttribute("listaTurnosDisponibles", listaTurnosDisponibles);

            // 4. FORWARD AL JSP
            request.getRequestDispatcher("solicitar_servicio.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error GRAVE al precargar datos para Solicitud de Servicio: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeCliente.jsp?error=precarga_bd");
        }
    }
}
