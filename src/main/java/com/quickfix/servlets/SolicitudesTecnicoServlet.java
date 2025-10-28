package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Tecnico; // Importa la entidad Tecnico
import com.quickfix.entities.Usuario;
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para que el Técnico vea las Solicitudes de Servicio que le fueron asignadas.
 */
// URL dentro de la carpeta protegida del técnico
@WebServlet(name = "SolicitudesTecnicoServlet", urlPatterns = {"/tecnico/SolicitudesTecnicoServlet"})
public class SolicitudesTecnicoServlet extends HttpServlet {

    ControladorLogica controlLogica = new ControladorLogica();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad: Verificar Sesión y Rol de Técnico
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Tecnico".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // 2. Obtener el Técnico logueado (necesitamos el objeto Tecnico completo)
        Usuario usuarioLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
        // Asume que tienes un método para traer el Tecnico completo
        Tecnico tecnicoLogueado = controlLogica.traerTecnicoCompleto(usuarioLogueado.getIdUsuario()); 

        if (tecnicoLogueado == null) {
            response.sendRedirect("../login.jsp?error=tecnico_no_encontrado");
            return;
        }

        try {
            // 3. Cargar Solicitudes Asignadas y Activas (necesitas este método en la lógica/DAO)
            List<SolicitudServicio> listaSolicitudesAsignadas = controlLogica.traerSolicitudesActivasPorTecnico(tecnicoLogueado);

            // 4. Guardar la lista en el request para el JSP
            request.setAttribute("listaSolicitudesAsignadas", listaSolicitudesAsignadas);

            // 5. Forward al JSP que mostrará la tabla
            request.getRequestDispatcher("solicitudesTecnico.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar solicitudes asignadas: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeTecnico.jsp?error=carga_solicitudes");
        }
    }

    // El doPost podría usarse más adelante para actualizar el estado de una solicitud
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Por ahora, redirigimos al GET si se accede por POST
        doGet(request, response);
    }
}
