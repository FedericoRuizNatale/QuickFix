package com.quickfix.servlets;

import java.io.IOException;
import java.net.URLEncoder; // Para mensajes de error
import java.util.List;

import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Tecnico;
import com.quickfix.entities.Usuario;
import com.quickfix.enums.EstadoSolicitud; // Importar Enum
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para que el Técnico vea y gestione las Solicitudes de Servicio asignadas.
 */
@WebServlet(name = "SolicitudesTecnicoServlet", urlPatterns = {"/tecnico/SolicitudesTecnicoServlet"})
public class SolicitudesTecnicoServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    /**
     * doGet: Carga la lista de solicitudes asignadas y activas.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Tecnico".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // 2. Obtener el Técnico logueado
        Usuario usuarioLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
        Tecnico tecnicoLogueado = controlLogica.traerTecnicoCompleto(usuarioLogueado.getIdUsuario()); 

        if (tecnicoLogueado == null) {
            response.sendRedirect("../login.jsp?error=tecnico_no_encontrado");
            return;
        }

        try {
            // 3. Cargar Solicitudes Asignadas y Activas
            List<SolicitudServicio> listaSolicitudesAsignadas = controlLogica.traerSolicitudesActivasPorTecnico(tecnicoLogueado);

            // 4. Guardar en el request
            request.setAttribute("listaSolicitudesAsignadas", listaSolicitudesAsignadas);

            // 5. Forward al JSP
            request.getRequestDispatcher("solicitudesTecnico.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar solicitudes asignadas: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeTecnico.jsp?error=carga_solicitudes");
        }
    }

    /**
     * doPost: Procesa la actualización de estado de una solicitud.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Tecnico".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }
        
        String redirectURL = "SolicitudesTecnicoServlet"; // Volver a esta misma página (al doGet)

        try {
            // 2. Obtener datos del formulario del modal "Gestionar"
            Integer idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
            String nuevoEstadoStr = request.getParameter("nuevoEstado");
            
            // (Opcional) Guardar el diagnóstico del técnico
            String diagnosticoTecnico = request.getParameter("diagnosticoTecnico"); 

            // 3. Convertir String a Enum
            EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(nuevoEstadoStr);

            // 4. Llamar a la lógica para actualizar
            // (Necesitarás crear este método en ControladoraLogica)
            controlLogica.actualizarEstadoSolicitud(idSolicitud, nuevoEstado, diagnosticoTecnico);

            // 5. Redirigir con mensaje de éxito
            response.sendRedirect(redirectURL + "?exito=¡Estado actualizado con éxito!");

        } catch (IllegalArgumentException e) {
            // Error si el 'nuevoEstado' no es válido
            response.sendRedirect(redirectURL + "?error=" + URLEncoder.encode("El estado seleccionado no es válido.", "UTF-8"));
        } catch (Exception e) {
            System.err.println("Error al actualizar estado de solicitud: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(redirectURL + "?error=" + URLEncoder.encode("Error interno al actualizar.", "UTF-8"));
        }
    }
}
