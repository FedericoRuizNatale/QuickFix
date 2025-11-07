package com.quickfix.servlets;

import java.io.IOException;

import java.time.LocalDateTime;



import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Servicio;
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Turno;
import com.quickfix.entities.Usuario; // Needed for session object

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map; // ⬅️ Importante para el doGet

// Imports de GSON (para la API del calendario)
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quickfix.dto.SlotDTO;
import com.quickfix.util.LocalDateAdapter;
import com.quickfix.util.LocalDateTimeAdapter;
import com.quickfix.util.LocalTimeAdapter;

// Imports de Entidades y Lógica
import com.quickfix.entities.AgendaConfiguracion; // ⬅️ Necesario para el intervalo
import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.entities.Servicio;
import com.quickfix.entities.SolicitudServicio;
import com.quickfix.entities.Turno;
import com.quickfix.entities.Usuario;
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

    private final ControladorLogica controlLogica = ControladorLogica.getInstance();
    
    // Objeto GSON para manejar las fechas/horas en JSON
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
        .create();

    /**
     * doGet AHORA SÓLO maneja la llamada AJAX del calendario.
     * La carga de la página (con dropdowns) la hace PreSolicitudServlet.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad (revisa la sesión)
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 No Autorizado
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("fetchSlotsDisponibles".equals(action)) {
                // --- CASO: EL CALENDARIO PIDE SLOTS (API) ---
                
                // 1. Llamar al "cerebro" que calcula las 4 capas
                List<SlotDTO> slotsDisponibles = controlLogica.traerSlotsDisponibles();
                
                // 2. Devolver la lista como JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(gson.toJson(slotsDisponibles));
            }
            // (No hay 'else')
            
        } catch (Exception e) {
            e.printStackTrace();
            // Error al devolver JSON
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * doPost AHORA procesa el formulario y CREA el Turno y la Solicitud.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- 1. VERIFICACIÓN DE SEGURIDAD Y OBTENCIÓN DEL CLIENTE ---
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }
        Cliente clienteLogueado = (Cliente) miSesion.getAttribute("usuarioLogueado");
        if (clienteLogueado == null) {
            // Recarga el cliente completo por si acaso
            clienteLogueado = controlLogica.traerClienteCompleto(((Usuario)miSesion.getAttribute("usuarioLogueado")).getIdUsuario());
        }

        // --- 2. CAPTURA DE DATOS DEL FORMULARIO ---
        String strIdEquipo = request.getParameter("idEquipo");
        String strIdServicio = request.getParameter("idServicio");
        // ⬇️ --- CAMBIO CLAVE: Ya no es 'idTurno', es la fecha --- ⬇️
        String strFechaHoraTurno = request.getParameter("fechaHoraTurno");
        String diagnostico = request.getParameter("diagnostico");
        String strPrioridad = request.getParameter("prioridad");

        // Validación básica
        if (strIdEquipo == null || strIdEquipo.isEmpty() ||
            strIdServicio == null || strIdServicio.isEmpty() ||
            strFechaHoraTurno == null || strFechaHoraTurno.isEmpty() || // ⬅️ CAMBIADO
            diagnostico == null || diagnostico.trim().isEmpty()) {
                
            response.sendRedirect("PreSolicitudServlet?error=datos_faltantes");
            return;
        }

        try {
            // --- 3. CONVERTIR Y BUSCAR OBJETOS ---
            Integer idEquipo = Integer.parseInt(strIdEquipo);
            Integer idServicio = Integer.parseInt(strIdServicio);
            
            EquipoCliente equipo = controlLogica.traerEquipo(idEquipo);
            Servicio servicio = controlLogica.traerServicio(idServicio);
            
            // Validar que el equipo pertenezca al cliente (seguridad)
            if (equipo == null || equipo.getCliente() == null || !equipo.getCliente().getIdUsuario().equals(clienteLogueado.getIdUsuario())) {
                 response.sendRedirect("PreSolicitudServlet?error=equipo_invalido");
                 return;
            }

            // ⬇️ --- 4. ¡NUEVA LÓGICA! CREACIÓN DEL TURNO --- ⬇️
            
            // 4a. Convertir el String (ej: "2025-11-10T09:30:00") a LocalDateTime
            LocalDateTime fechaInicioTurno = LocalDateTime.parse(strFechaHoraTurno);

            // 4b. Traer la configuración para saber la duración del turno
            AgendaConfiguracion config = controlLogica.traerConfiguracionAgenda();
            int duracionTurno = config.getIntervaloMinutos();

            // 4c. Crear el NUEVO objeto Turno
            Turno nuevoTurno = new Turno();
            nuevoTurno.setFechaHoraInicio(fechaInicioTurno);
            nuevoTurno.setFechaHoraFin(fechaInicioTurno.plusMinutes(duracionTurno));
            nuevoTurno.setEstado(EstadoTurno.RESERVADO); 
            // El 'tecnico' se asignará después por el Admin
            
            // ⬆️ --- FIN DE LA NUEVA LÓGICA --- ⬆️

            // --- 5. CREACIÓN Y VINCULACIÓN DEL OBJETO SOLICITUD ---
            SolicitudServicio nuevaSolicitud = new SolicitudServicio();
            nuevaSolicitud.setFechaHoraCreacion(LocalDateTime.now());
            nuevaSolicitud.setDiagnostico(diagnostico);
            
            Prioridad prioridad = Prioridad.valueOf(strPrioridad);
            nuevaSolicitud.setPrioridad(prioridad);
            
            nuevaSolicitud.setEstado(EstadoSolicitud.RECIBIDA); // Estado inicial

            // Vincular objetos
            nuevaSolicitud.setCliente(clienteLogueado);
            nuevaSolicitud.setEquipoCliente(equipo);
            nuevaSolicitud.setServicio(servicio);
            nuevaSolicitud.setTurno(nuevoTurno); // ⬅️ Le pasamos el Turno NUEVO

            // --- 6. LLAMADA A LA LÓGICA TRANSACCIONAL ---
            // El método 'procesarNuevaSolicitud' (que ya teníamos)
            // guardará la 'solicitud' y el 'turno' juntos en una transacción.
            controlLogica.procesarNuevaSolicitud(nuevaSolicitud, nuevoTurno);

            // --- 7. REDIRECCIÓN DE ÉXITO ---
            response.sendRedirect("homeCliente.jsp?exito=solicitud_ok"); 
            
        } catch (NumberFormatException e) {
            response.sendRedirect("PreSolicitudServlet?error=formato_invalido");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("PreSolicitudServlet?error=prioridad_invalida");
        } catch (Exception e) {
            System.err.println("Error al procesar solicitud: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("PreSolicitudServlet?error=interno");
        }
    }
}
