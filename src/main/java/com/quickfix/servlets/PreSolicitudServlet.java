package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Servicio;
// import com.quickfix.entities.Turno; // ⬅️ YA NO LO USAMOS
import com.quickfix.logic.ControladorLogica;
import com.quickfix.entities.Usuario;
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

        Usuario usuarioLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
        
        Cliente clienteLogueado = null; 
        if (usuarioLogueado != null) {
            clienteLogueado = controlLogica.traerClienteCompleto(usuarioLogueado.getIdUsuario());
        }

        if (clienteLogueado == null) {
            response.sendRedirect("../login.jsp?error=cliente_no_encontrado");
            return;
        }

        try {
            // A. Traer Equipos del cliente
            List<EquipoCliente> listaEquipos = controlLogica.traerEquiposPorCliente(clienteLogueado);
            
            // B. Traer Catálogo de Servicios
            List<Servicio> listaServicios = controlLogica.traerTodosLosServicios();
            
            // C. (¡BORRADO!) Ya no traemos los turnos. El calendario lo hará por AJAX.

            // 3. ATRIBUTOS DE REQUEST
            request.setAttribute("listaEquipos", listaEquipos);
            request.setAttribute("listaServicios", listaServicios);
            // (Ya no pasamos listaTurnosDisponibles)

            // 4. FORWARD AL JSP (OJO AL NOMBRE DEL JSP)
            // Asegúrate de que el JSP se llame "solicitarServicio.jsp"
            request.getRequestDispatcher("solicitarServicio.jsp").forward(request, response); 

        } catch (Exception e) {
            System.err.println("Error GRAVE al precargar datos para Solicitud de Servicio: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeCliente.jsp?error=precarga_bd");
        }
    }
}
