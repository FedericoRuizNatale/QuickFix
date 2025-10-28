package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.Turno;
import com.quickfix.entities.Usuario;
import com.quickfix.enums.EstadoTurno; // Importa el Enum
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para que el Técnico gestione la disponibilidad de los turnos.
 */
// La URL debe estar dentro de la carpeta protegida del técnico
@WebServlet(name = "GestionTurnosServlet", urlPatterns = {"/tecnico/GestionTurnosServlet"})
public class GestionTurnosServlet extends HttpServlet {

    ControladorLogica controlLogica = new ControladorLogica();

    /**
     * doGet: Carga la lista COMPLETA de turnos y la muestra en el JSP.
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
            // 2. Cargar TODOS los turnos (usando el método que ya tienes en la lógica)
            List<Turno> listaTodosTurnos = controlLogica.traerTodosLosTurnos();

            // 3. Guardar la lista en el request para el JSP
            request.setAttribute("listaTodosTurnos", listaTodosTurnos);

            // 4. Forward al JSP que mostrará la tabla de gestión
            request.getRequestDispatcher("turnosTecnico.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar la lista de turnos para gestión: " + e.getMessage());
            e.printStackTrace();
            // Redirigir al home del técnico con un mensaje de error
            response.sendRedirect("homeTecnico.jsp?error=carga_turnos");
        }
    }

    /**
     * doPost: Procesa la acción de cambiar el estado de un turno.
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

        try {
            // 2. Obtener los parámetros del formulario (enviados desde turnosTecnico.jsp)
            Integer idTurno = Integer.parseInt(request.getParameter("idTurno"));
            String strNuevoEstado = request.getParameter("nuevoEstado"); // Ej: "DISPONIBLE" o "NO_DISPONIBLE"

            // 3. Convertir el String del estado al tipo Enum
            EstadoTurno nuevoEstado = EstadoTurno.valueOf(strNuevoEstado);

            // 4. Llamar a la lógica para actualizar el estado del turno
            controlLogica.setEstadoTurno(idTurno, nuevoEstado);

            // 5. Redirigir de vuelta al doGet de este mismo servlet para recargar la lista
            //    Añadimos un parámetro de éxito para mostrar un mensaje.
            response.sendRedirect("GestionTurnosServlet?exito=estado_actualizado");

        } catch (NumberFormatException e) {
            // Error si el idTurno no es un número válido
            response.sendRedirect("GestionTurnosServlet?error=id_invalido");
        } catch (IllegalArgumentException e) {
            // Error si el 'nuevoEstado' no coincide con ningún valor del Enum
             response.sendRedirect("GestionTurnosServlet?error=estado_invalido");
        } catch (Exception e) {
            // Otro error (ej: BD)
            System.err.println("Error al actualizar estado del turno: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("GestionTurnosServlet?error=actualizacion_fallida");
        }
    }
}
