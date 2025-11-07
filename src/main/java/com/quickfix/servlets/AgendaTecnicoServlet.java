package com.quickfix.servlets;

import com.quickfix.dto.BloqueoDTO;
import com.quickfix.dto.TurnoDTO;
import com.quickfix.entities.AgendaConfiguracion;
import com.quickfix.entities.BloqueoTecnico;
import com.quickfix.entities.DiaNoLaboral; // ⬅️ IMPORTAR
import com.quickfix.entities.HorarioLaboral; // ⬅️ IMPORTAR
import com.quickfix.entities.Tecnico;
import com.quickfix.logic.ControladorLogica;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;
// ⬇️ IMPORTAR NUEVOS AYUDANTES
import com.quickfix.util.LocalDateAdapter;
import com.quickfix.util.LocalDateTimeAdapter; 
import com.quickfix.util.LocalTimeAdapter;
import java.time.LocalDate; // ⬅️ IMPORTAR
import java.time.LocalDateTime;
import java.time.LocalTime; // ⬅️ IMPORTAR
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "AgendaTecnicoServlet", urlPatterns = {"/tecnico/AgendaTecnicoServlet"})
public class AgendaTecnicoServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();
    // Pre-configuramos Gson con nuestros adaptadores de fecha/hora
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
        .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Tecnico tecnicoLogueado = (Tecnico) request.getSession().getAttribute("usuarioLogueado");
        if (tecnicoLogueado == null || !request.getSession().getAttribute("rolUsuario").equals("Tecnico")) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("fetchEvents".equals(action)) {
                // --- 1. Devolver EVENTOS (Feriados, Bloqueos) ---
                
                // Traemos las entidades JPA (con proxies)
                List<BloqueoTecnico> listaBloqueosJPA = controlLogica.traerBloqueosPorTecnico(tecnicoLogueado);
                List<DiaNoLaboral> listaFeriados = controlLogica.traerDiasNoLaborales();
                
                // ⬇️ --- ¡AQUÍ ESTÁ LA TRADUCCIÓN! --- ⬇️
                // Convertimos la lista de Entidades a una lista de DTOs limpios
                List<BloqueoDTO> listaBloqueosDTO = new ArrayList<>();
                for (BloqueoTecnico b : listaBloqueosJPA) {
                    listaBloqueosDTO.add(new BloqueoDTO(
                        b.getId(),
                        b.getFechaHoraInicio(),
                        b.getFechaHoraFin(),
                        b.getMotivo()
                    ));
                }
                // ⬆️ --- FIN DE LA TRADUCCIÓN --- ⬆️

                // (Aquí también traeríamos la Capa 4: Turnos Reservados)
                List<TurnoDTO> listaTurnosDTO = controlLogica.traerTurnosReservadosDTOs();

                Map<String, Object> eventos = Map.of(
                    "bloqueos", listaBloqueosDTO, // ⬅️ Usamos la lista de DTOs
                    "feriados", listaFeriados,
                    "turnos", listaTurnosDTO
                );

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(gson.toJson(eventos)); // ⬅️ GSON ahora sí está feliz
                
            } else if ("fetchRules".equals(action)) {
                // --- 2. Devolver REGLAS (Horarios, Config) ---
                List<HorarioLaboral> horarios = controlLogica.traerHorariosLaborales();
                AgendaConfiguracion config = controlLogica.traerConfiguracionAgenda();
                
                Map<String, Object> rules = Map.of(
                    "horarios", horarios,
                    "config", config
                );
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(gson.toJson(rules));

            } else {
                // --- 3. Cargar la PÁGINA ---
                request.getRequestDispatcher("/tecnico/agendaTecnico.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (action != null) { 
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/tecnico/AgendaTecnicoServlet?error=Error_al_cargar_agenda");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Tecnico tecnicoLogueado = (Tecnico) request.getSession().getAttribute("usuarioLogueado");
        if (tecnicoLogueado == null || !request.getSession().getAttribute("rolUsuario").equals("Tecnico")) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String jsonData = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> data = gson.fromJson(jsonData, Map.class); // Usamos el gson configurado
        
        String action = data.get("action");
        
        try {
            if ("crearBloqueo".equals(action)) {
                String inicio = data.get("inicio");
                String fin = data.get("fin");
                String motivo = data.get("motivo");
                controlLogica.crearBloqueoTecnico(tecnicoLogueado, inicio, fin, motivo);
            
            } else if ("borrarBloqueo".equals(action)) {
                int idBloqueo = Integer.parseInt(data.get("id"));
                controlLogica.borrarBloqueoTecnico(idBloqueo);
            }
            
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"success\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}