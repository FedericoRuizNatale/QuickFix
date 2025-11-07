package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Usuario;
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "HistorialServlet", urlPatterns = {"/cliente/HistorialServlet"})
public class HistorialServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession miSesion = request.getSession(false);

        // Seguridad
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        Usuario usuarioLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
        Cliente clienteLogueado = controlLogica.traerClienteCompleto(usuarioLogueado.getIdUsuario());

        if (clienteLogueado == null) {
            response.sendRedirect("../login.jsp?error=cliente_no_encontrado");
            return;
        }

        try {
            // Cargar el historial de solicitudes
            List<SolicitudServicio> historialSolicitudes = controlLogica.traerSolicitudesPorCliente(clienteLogueado);

            // Guardar en el request
            request.setAttribute("historialSolicitudes", historialSolicitudes);

            // Forward al JSP
            request.getRequestDispatcher("historialCliente.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeCliente.jsp?error=historial");
        }
    }
}
