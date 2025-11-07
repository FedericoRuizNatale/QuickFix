package com.quickfix.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import com.quickfix.entities.Servicio;
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para que el Administrador gestione el ABMC (CRUD) de Servicios.
 */
@WebServlet(name = "AdminServiciosServlet", urlPatterns = {"/admin/AdminServiciosServlet"})
public class AdminServiciosServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    /**
     * doGet: Carga la lista de todos los servicios y la muestra en el JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad: Verificar Sesión y Rol de Administrador
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        try {
            // 2. Cargar la lista de todos los servicios
            List<Servicio> listaServicios = controlLogica.traerTodosLosServicios();

            // 3. Guardar la lista en el request para el JSP
            request.setAttribute("listaServicios", listaServicios);

            // 4. Forward al JSP de gestión de servicios
            request.getRequestDispatcher("servicios_abmc.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar lista de servicios: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeAdmin.jsp?error=carga_servicios");
        }
    }

    /**
     * doPost: Procesa las acciones de Crear, Editar o Eliminar un Servicio.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad: Verificar Sesión y Rol de Administrador
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // 2. Obtener la acción (create, update, delete)
        String action = request.getParameter("action");
        String redirectURL = "AdminServiciosServlet"; // Redirección base

        try {
            switch (action) {
                case "create":
                    // --- LÓGICA PARA CREAR ---
                    String nombre = request.getParameter("nombre");
                    String descripcion = request.getParameter("descripcion");
                    Double costoBase = Double.parseDouble(request.getParameter("costoBase"));
                    Integer tiempoEstimado = Integer.parseInt(request.getParameter("tiempoEstimado"));

                    Servicio nuevoServicio = new Servicio();
                    nuevoServicio.setNombre(nombre);
                    nuevoServicio.setDescripcion(descripcion);
                    nuevoServicio.setCostoBase(costoBase);
                    nuevoServicio.setTiempoEstimado(tiempoEstimado);
                    
                    controlLogica.crearServicio(nuevoServicio);
                    redirectURL += "?exito=creado";
                    break;

                case "update":
                    // --- LÓGICA PARA EDITAR ---
                    Integer idServicio = Integer.parseInt(request.getParameter("idServicio"));
                    String nombreUpd = request.getParameter("nombre");
                    String descripcionUpd = request.getParameter("descripcion");
                    Double costoBaseUpd = Double.parseDouble(request.getParameter("costoBase"));
                    Integer tiempoEstimadoUpd = Integer.parseInt(request.getParameter("tiempoEstimado"));
                    
                    // Traemos el servicio existente para no perder relaciones (aunque en este caso no tiene muchas)
                    Servicio servicioEditado = controlLogica.traerServicio(idServicio);
                    if (servicioEditado != null) {
                        servicioEditado.setNombre(nombreUpd);
                        servicioEditado.setDescripcion(descripcionUpd);
                        servicioEditado.setCostoBase(costoBaseUpd);
                        servicioEditado.setTiempoEstimado(tiempoEstimadoUpd);
                        
                        controlLogica.editarServicio(servicioEditado); // Llama al update
                        redirectURL += "?exito=editado";
                    } else {
                        redirectURL += "?error=no_encontrado";
                    }
                    break;

                case "delete":
                    // --- LÓGICA PARA ELIMINAR ---
                    Integer idServicioDel = Integer.parseInt(request.getParameter("idServicio"));
                    try {
                        controlLogica.eliminarServicio(idServicioDel);
                        redirectURL += "?exito=eliminado";
                    } catch (Exception e) {
                        // Captura el error si el servicio está en uso (Foreign Key)
                        redirectURL += "?error=" + URLEncoder.encode(e.getMessage(), "UTF-8");
                    }
                    break;

                default:
                    redirectURL += "?error=accion_desconocida";
                    break;
            }
            
            response.sendRedirect(redirectURL);

        } catch (NumberFormatException e) {
            response.sendRedirect(redirectURL + "?error=formato_numero_invalido");
        } catch (Exception e) {
            System.err.println("Error en doPost de AdminServiciosServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(redirectURL + "?error=" + URLEncoder.encode("Error interno: " + e.getMessage(), "UTF-8"));
        }
    }
}
