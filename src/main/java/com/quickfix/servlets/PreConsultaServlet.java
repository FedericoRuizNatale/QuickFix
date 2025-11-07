package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.logic.ControladorLogica;
import com.quickfix.entities.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PreConsultaServlet", urlPatterns = {"/cliente/PreConsultaServlet"})
public class PreConsultaServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession miSesion = request.getSession(false);

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
            // Cargar solo la lista de equipos del cliente
            List<EquipoCliente> listaEquipos = controlLogica.traerEquiposPorCliente(clienteLogueado);

            // Guardar la lista en el request
            request.setAttribute("listaEquipos", listaEquipos);

            // Forward al JSP
            request.getRequestDispatcher("consultaTecnica.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al precargar datos para Consulta Técnica: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeCliente.jsp?error=precarga_consulta");
        }
    }
}
