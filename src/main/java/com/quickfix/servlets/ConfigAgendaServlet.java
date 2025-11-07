package com.quickfix.servlets;

import java.io.IOException;
import java.util.List;

import com.quickfix.entities.AgendaConfiguracion;
import com.quickfix.entities.DiaNoLaboral;
import com.quickfix.entities.HorarioLaboral;
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ConfigAgendaServlet", urlPatterns = {"/admin/ConfigAgendaServlet"})
public class ConfigAgendaServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    /**
     * doGet ahora hace DOS cosas:
     * 1. Muestra la página (como antes).
     * 2. Maneja la acción de BORRAR Feriado (que viene por GET).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        try {
            // 🗑️ Lógica de BORRADO
            if ("borrarFeriado".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                controlLogica.borrarDiaNoLaboral(id);
                // Redirigimos con un mensaje de éxito
                response.sendRedirect(request.getContextPath() + "/admin/ConfigAgendaServlet?exito=borrado_ok");
                return; // Importante: termina la ejecución aquí
            }
        
            // ----------------------------------------------------
            // 📖 Lógica de LECTURA (si no hay 'action')
            // ----------------------------------------------------
            
            // 1. Traemos todos los datos de la agenda
            List<HorarioLaboral> listaHorarios = controlLogica.traerHorariosLaborales();
            List<DiaNoLaboral> listaFeriados = controlLogica.traerDiasNoLaborales();
            AgendaConfiguracion config = controlLogica.traerConfiguracionAgenda();
            
            // 2. Los ponemos en el request para que el JSP los pueda leer
            request.setAttribute("listaHorarios", listaHorarios);
            request.setAttribute("listaFeriados", listaFeriados);
            request.setAttribute("config", config);
            
            // 3. Redirigimos la petición al JSP
            request.getRequestDispatcher("/admin/configAgenda.jsp").forward(request, response);

        } catch (Exception e) {
            // ✅ CAMBIO AQUÍ: Mensaje de error simple, no el e.getMessage()
            response.sendRedirect(request.getContextPath() + "/ConfigAgendaServlet?error=Error_al_procesar_solicitud_get");
        }
    }

    /**
     * doPost maneja las acciones de GUARDAR Horario y AGREGAR Feriado.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String exitoMsg = null; // Mensaje de éxito
        String errorMsg = null; // Mensaje de error

        try {
            switch (action) {
                // 💾 Caso: Guardar Horario Semanal
                case "guardarHorario":
                    // 1. Guardar el intervalo
                    int intervalo = Integer.parseInt(request.getParameter("intervalo"));
                    controlLogica.actualizarConfiguracionAgenda(intervalo);
                    
                    // 2. Guardar los 7 días de la semana
                    for (int i = 1; i <= 7; i++) {
                        String inicio = request.getParameter("inicio_" + i);
                        String fin = request.getParameter("fin_" + i);
                        controlLogica.actualizarHorario(i, inicio, fin);
                    }
                    exitoMsg = "horario_ok";
                    break;
                
                // ➕ Caso: Agregar Feriado
                case "agregarFeriado":
                    String fecha = request.getParameter("fechaFeriado");
                    String desc = request.getParameter("descFeriado");
                    controlLogica.agregarDiaNoLaboral(fecha, desc);
                    exitoMsg = "feriado_ok";
                    break;
            }

        } catch (Exception e) {
            // ✅ CAMBIO AQUÍ: Mensaje de error simple, no el e.getMessage()
            errorMsg = "Error_al_guardar_datos";
            // (Opcional: imprime el error real en la consola para ti)
            e.printStackTrace(); 
        }

        // --- Redirección Final ---
        // Al final de todo, redirigimos de vuelta al servlet (por GET)
        // para que recargue la página, mostrando los datos actualizados
        // y el mensaje de éxito o error.
        
        String redirectURL = request.getContextPath() + "/admin/ConfigAgendaServlet";
        
        if (exitoMsg != null) {
            redirectURL += "?exito=" + exitoMsg;
        } else if (errorMsg != null) {
            redirectURL += "?error=" + errorMsg;
        }
        
        response.sendRedirect(redirectURL);
    }
}