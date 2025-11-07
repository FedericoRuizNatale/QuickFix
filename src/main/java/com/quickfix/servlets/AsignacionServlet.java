package com.quickfix.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Tecnico;
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AsignacionServlet", urlPatterns = {"/admin/AsignacionServlet"})
public class AsignacionServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Seguridad
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        try {
            // 1. Cargar Solicitudes sin Asignar
            List<SolicitudServicio> solicitudesNuevas = controlLogica.traerSolicitudesSinAsignar();
            
            // 2. Cargar Técnicos Activos
            List<Tecnico> tecnicosActivos = controlLogica.traerTecnicosActivos();
            
            // 3. Calcular la Carga de Trabajo de CADA técnico
            Map<Integer, Long> cargaPorTecnico = new HashMap<>();
            for (Tecnico tec : tecnicosActivos) {
                cargaPorTecnico.put(tec.getIdUsuario(), controlLogica.obtenerCargaTecnico(tec));
            }

            // 4. Enviar todo al JSP
            request.setAttribute("listaSolicitudesNuevas", solicitudesNuevas);
            request.setAttribute("listaTecnicosActivos", tecnicosActivos);
            request.setAttribute("cargaPorTecnico", cargaPorTecnico); // Mapa de carga

            request.getRequestDispatcher("asignacion.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("homeAdmin.jsp?error=carga_asignacion");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Seguridad
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }
        
        try {
            // Recibir los IDs del formulario del JSP
            Integer idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
            Integer idTecnico = Integer.parseInt(request.getParameter("idTecnico"));
            
            // Llamar a la lógica para hacer la asignación
            controlLogica.asignarTecnicoASolicitud(idSolicitud, idTecnico);
            
            // Redirigir de vuelta al GET para recargar la lista
            response.sendRedirect("AsignacionServlet?exito=asignacion_ok");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AsignacionServlet?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}