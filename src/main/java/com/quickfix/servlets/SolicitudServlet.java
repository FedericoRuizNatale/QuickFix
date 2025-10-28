package com.quickfix.servlets;

import java.io.IOException;
import java.time.LocalDateTime;

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Servicio;
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Turno;
import com.quickfix.entities.Usuario; // Needed for session object
import com.quickfix.enums.EstadoSolicitud;
import com.quickfix.enums.EstadoTurno;
import com.quickfix.enums.Prioridad;
import com.quickfix.logic.ControladorLogica;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "SolicitudServlet", urlPatterns = {"/cliente/SolicitudServlet"})
public class SolicitudServlet extends HttpServlet {

    ControladorLogica controlLogica = new ControladorLogica();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- 1. VERIFICACIÓN DE SEGURIDAD Y OBTENCIÓN DEL CLIENTE ---
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // Obtener el Usuario de la sesión
        Usuario usuarioLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
        
        // Obtener el objeto Cliente completo desde la lógica
        Cliente clienteLogueado = controlLogica.traerClienteCompleto(usuarioLogueado.getIdUsuario());

        // Si por alguna razón el cliente no se encuentra (inesperado)
        if (clienteLogueado == null) {
            response.sendRedirect("../login.jsp?error=cliente_no_encontrado");
            return;
        }

        // --- 2. CAPTURA DE DATOS DEL FORMULARIO ---
        String strIdEquipo = request.getParameter("idEquipo");
        String strIdServicio = request.getParameter("idServicio");
        String strIdTurno = request.getParameter("idTurno");
        String diagnostico = request.getParameter("diagnostico");
        String strPrioridad = request.getParameter("prioridad");

        // Validación básica (IDs y diagnóstico no pueden ser null/vacíos)
        if (strIdEquipo == null || strIdEquipo.isEmpty() ||
            strIdServicio == null || strIdServicio.isEmpty() ||
            strIdTurno == null || strIdTurno.isEmpty() ||
            diagnostico == null || diagnostico.trim().isEmpty()) {
                
            response.sendRedirect("PreSolicitudServlet?error=datos_faltantes"); // Redirigir via PreServlet para recargar listas
            return;
        }

        try {
            // --- 3. CONVERTIR Y BUSCAR OBJETOS ---
            Integer idEquipo = Integer.parseInt(strIdEquipo);
            Integer idServicio = Integer.parseInt(strIdServicio);
            Integer idTurno = Integer.parseInt(strIdTurno);

            EquipoCliente equipo = controlLogica.traerEquipo(idEquipo);
            Servicio servicio = controlLogica.traerServicio(idServicio);
            Turno turno = controlLogica.traerTurno(idTurno);

            // --- 4. VERIFICACIÓN DE LÓGICA DE NEGOCIO ---
            
            // Re-verificar que el turno exista y esté disponible
            if (turno == null || turno.getEstado() != EstadoTurno.DISPONIBLE) {
                response.sendRedirect("PreSolicitudServlet?error=turno_no_disponible"); // Redirigir via PreServlet
                return;
            }
            
            // Validar que el equipo pertenezca al cliente logueado (importante por seguridad)
            if (equipo == null || equipo.getCliente() == null || !equipo.getCliente().getIdUsuario().equals(clienteLogueado.getIdUsuario())) {
                 response.sendRedirect("PreSolicitudServlet?error=equipo_invalido");
                 return;
            }

            // --- 5. CREACIÓN Y VINCULACIÓN DEL OBJETO SOLICITUD ---
            SolicitudServicio nuevaSolicitud = new SolicitudServicio();
            nuevaSolicitud.setFechaHoraCreacion(LocalDateTime.now());
            nuevaSolicitud.setDiagnostico(diagnostico);
            
            Prioridad prioridad = Prioridad.valueOf(strPrioridad); // Convertir String a Enum
            nuevaSolicitud.setPrioridad(prioridad);
            
            nuevaSolicitud.setEstado(EstadoSolicitud.RECIBIDA); // Estado inicial

            // Vincular objetos
            nuevaSolicitud.setCliente(clienteLogueado);
            nuevaSolicitud.setEquipoCliente(equipo); // Usar el campo correcto
            nuevaSolicitud.setServicio(servicio);
            nuevaSolicitud.setTurno(turno);
            // El técnico se asignará después
            
            // --- 6. ACTUALIZAR EL ESTADO DEL TURNO ---
            turno.setEstado(EstadoTurno.RESERVADO);

            // --- 7. LLAMADA A LA LÓGICA TRANSACCIONAL ---
            controlLogica.procesarNuevaSolicitud(nuevaSolicitud, turno);

            // --- 8. REDIRECCIÓN DE ÉXITO ---
            // Se redirige al home del cliente con el parámetro de éxito
            response.sendRedirect("homeCliente.jsp?exito=solicitud_ok"); 
            
        } catch (NumberFormatException e) {
            // Error si los IDs no son números válidos
            response.sendRedirect("PreSolicitudServlet?error=formato_invalido");
        } catch (IllegalArgumentException e) {
            // Error si el valor de Prioridad no es válido
            response.sendRedirect("PreSolicitudServlet?error=prioridad_invalida");
        } catch (Exception e) {
            // Error genérico (ej: BD)
            System.err.println("Error al procesar solicitud: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("PreSolicitudServlet?error=interno");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirige al PreServlet si se accede por GET
        response.sendRedirect("PreSolicitudServlet");
    }
}
