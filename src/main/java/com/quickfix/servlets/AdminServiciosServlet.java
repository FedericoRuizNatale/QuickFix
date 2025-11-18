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


@WebServlet(name = "AdminServiciosServlet", urlPatterns = {"/admin/AdminServiciosServlet"})
public class AdminServiciosServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        try {
            
            List<Servicio> listaServicios = controlLogica.traerTodosLosServicios();

            
            request.setAttribute("listaServicios", listaServicios);

           
            request.getRequestDispatcher("servicios_abmc.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar lista de servicios: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeAdmin.jsp?error=carga_servicios");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        
        String action = request.getParameter("action");
        String redirectURL = "AdminServiciosServlet"; 

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
