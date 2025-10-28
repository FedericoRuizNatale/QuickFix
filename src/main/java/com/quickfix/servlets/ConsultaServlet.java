package com.quickfix.servlets;

import java.io.IOException;
import java.time.LocalDateTime;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.ConsultaTecnica;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Usuario;
import com.quickfix.enums.EstadoConsulta; // Asegúrate de importar tu Enum
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet que procesa el envío del formulario de Consulta Técnica.
 */
@WebServlet(name = "ConsultaServlet", urlPatterns = {"/cliente/ConsultaServlet"})
public class ConsultaServlet extends HttpServlet {

    ControladorLogica controlLogica = new ControladorLogica();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. VERIFICACIÓN DE SEGURIDAD
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // Obtenemos el cliente logueado desde la sesión
        Cliente clienteLogueado = (Cliente) miSesion.getAttribute("usuarioLogueado");

        // 2. CAPTURA DE DATOS DEL FORMULARIO
        String descripcionProblema = request.getParameter("descripcionProblema");
        String strIdEquipo = request.getParameter("idEquipo"); // Puede ser "" si no se seleccionó

        // Validación básica (descripción no puede estar vacía)
        if (descripcionProblema == null || descripcionProblema.trim().isEmpty()) {
            response.sendRedirect("consultaTecnica.jsp?error=descripcion_vacia");
            return;
        }

        try {
            // 3. BUSCAR EL EQUIPO (SI SE SELECCIONÓ - Aunque no se vincule directamente)
            EquipoCliente equipoSeleccionado = null;
            if (strIdEquipo != null && !strIdEquipo.isEmpty()) {
                Integer idEquipo = Integer.parseInt(strIdEquipo);
                equipoSeleccionado = controlLogica.traerEquipo(idEquipo);
                // Aquí podrías agregar lógica si necesitas usar la info del equipo,
                // por ejemplo, para añadirla a la descripción.
            }

            // 4. CREACIÓN DEL OBJETO ConsultaTecnica
            ConsultaTecnica nuevaConsulta = new ConsultaTecnica();
            nuevaConsulta.setDescripcionProblema(descripcionProblema);
            nuevaConsulta.setFechaHora(LocalDateTime.now());
            nuevaConsulta.setEstado(EstadoConsulta.PENDIENTE); // Estado inicial

            // VINCULACIÓN DE OBJETOS:
            nuevaConsulta.setCliente(clienteLogueado); // Vincular al cliente que hace la consulta

            // --- CORRECCIÓN: SE ELIMINÓ LA LÍNEA DE setEquipoCliente ---
            // La relación con el EquipoCliente es IMPLÍCITA a través del Cliente.

            // El técnico se asignará después

            // 5. LLAMADA A LA LÓGICA PARA GUARDAR
            controlLogica.crearConsultaTecnica(nuevaConsulta);

            // 6. REDIRECCIÓN DE ÉXITO
            response.sendRedirect("consultaTecnica.jsp?exito=true");

        } catch (NumberFormatException e) {
            // Error si el idEquipo no es un número válido
            response.sendRedirect("consultaTecnica.jsp?error=equipo_invalido");
        } catch (Exception e) {
            // Error genérico (ej: problema con la base de datos)
            System.err.println("Error al procesar Consulta Técnica: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("consultaTecnica.jsp?error=interno");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si acceden por GET, los mandamos al formulario de precarga (para cargar la lista de equipos)
        response.sendRedirect("PreConsultaServlet");
    }
}