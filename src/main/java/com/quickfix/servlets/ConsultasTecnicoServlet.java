package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.ConsultaTecnica;
import com.quickfix.logic.ControladorLogica;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para que el Técnico vea y responda Consultas Técnicas pendientes.
 */
// URL dentro de la carpeta protegida del técnico
@WebServlet(name = "ConsultasTecnicoServlet", urlPatterns = {"/tecnico/ConsultasTecnicoServlet"})
public class ConsultasTecnicoServlet extends HttpServlet {

    ControladorLogica controlLogica = new ControladorLogica();

    /**
     * doGet: Carga la lista de consultas PENDIENTES y sin asignar.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad: Verificar Sesión y Rol de Técnico
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Tecnico".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        try {
            // 2. Cargar Consultas Pendientes (necesitas este método en la lógica)
            List<ConsultaTecnica> listaConsultasPendientes = controlLogica.traerConsultasPendientes();

            // 3. Guardar la lista en el request para el JSP
            request.setAttribute("listaConsultasPendientes", listaConsultasPendientes);

            // 4. Forward al JSP que mostrará la tabla
            request.getRequestDispatcher("consultasTecnico.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar consultas pendientes: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeTecnico.jsp?error=carga_consultas");
        }
    }

    /**
     * doPost: Procesa la acción de responder una consulta.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad: Verificar Sesión y Rol de Técnico
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Tecnico".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }
        
        // Obtener el técnico que está respondiendo
        com.quickfix.entities.Tecnico tecnicoLogueado = (com.quickfix.entities.Tecnico) miSesion.getAttribute("usuarioLogueado");

        try {
            // 2. Obtener datos del formulario de respuesta (del modal o página)
            Integer idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
            String solucion = request.getParameter("solucion");

            // Validación básica
            if (solucion == null || solucion.trim().isEmpty()) {
                 response.sendRedirect("ConsultasTecnicoServlet?error=solucion_vacia&idConsultaError=" + idConsulta);
                 return;
            }

            // 3. Llamar a la lógica para procesar la respuesta
            // Este método debe buscar la consulta, asignarle el técnico, 
            // poner la solución y cambiar el estado a RESUELTA.
            controlLogica.responderConsulta(idConsulta, tecnicoLogueado, solucion);

            // 4. Redirigir de vuelta al doGet para recargar la lista
            response.sendRedirect("ConsultasTecnicoServlet?exito=consulta_respondida");

        } catch (NumberFormatException e) {
            response.sendRedirect("ConsultasTecnicoServlet?error=id_invalido");
        } catch (Exception e) {
            System.err.println("Error al responder consulta: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("ConsultasTecnicoServlet?error=respuesta_fallida");
        }
    }
}
