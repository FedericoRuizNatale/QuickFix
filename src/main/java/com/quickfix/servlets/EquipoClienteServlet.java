package com.quickfix.servlets;

import java.io.IOException;
import java.net.URLEncoder; // Importa URLEncoder para los mensajes de error

import com.quickfix.entities.Cliente;
import com.quickfix.entities.EquipoCliente;
import com.quickfix.logic.ControladorLogica; // Importamos la lógica
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EquipoServlet", urlPatterns = {"/cliente/EquipoServlet"})
// ✅ CORRECCIÓN: El nombre de la clase debe ser EquipoServlet (sin Cliente)
public class EquipoClienteServlet extends HttpServlet { 

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    // doGet: Se usa para MOSTRAR el formulario de edición
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession miSesion = request.getSession(false);

        
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        if ("edit".equals(action)) {
            
            try {
                Integer idEquipo = Integer.parseInt(request.getParameter("id"));
                EquipoCliente equipo = controlLogica.traerEquipo(idEquipo);
                if (equipo != null) {
                    request.setAttribute("equipoParaEditar", equipo);
                    request.getRequestDispatcher("editarEquipo.jsp").forward(request, response); 
                } else {
                    response.sendRedirect("miEquipo.jsp?error=no_encontrado");
                }
            } catch (NumberFormatException e) {
                 response.sendRedirect("miEquipo.jsp?error=id_invalido");
            } catch (Exception e) {
                 System.err.println("Error en doGet de EquipoServlet: " + e.getMessage());
                 response.sendRedirect("miEquipo.jsp?error=interno_edit");
            }
        } else {
             response.sendRedirect("miEquipo.jsp");
        }
    }


    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action"); 
        HttpSession miSesion = request.getSession(false);

        
        if (miSesion == null || !"Cliente".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }
        
        Cliente clienteLogueado = (Cliente) miSesion.getAttribute("usuarioLogueado");

        try {
            if ("delete".equals(action)) {
                
                Integer idEquipo = Integer.parseInt(request.getParameter("idEquipo"));
                
                
                try {
                    controlLogica.eliminarEquipo(idEquipo); // La lógica ahora puede lanzar Exception
                    response.sendRedirect("miEquipo.jsp?exito=eliminado"); 
                } catch (Exception e) {
                    // Si la lógica lanzó la excepción (ej: "tiene solicitudes"), la mostramos
                    String mensajeError = URLEncoder.encode(e.getMessage(), "UTF-8");
                    response.sendRedirect("miEquipo.jsp?error=" + mensajeError);
                }

            } else if ("update".equals(action)) {
                // --- ACCIÓN: ACTUALIZAR ---
                Integer idEquipo = Integer.parseInt(request.getParameter("idEquipo")); 
                String tipo = request.getParameter("tipo");
                String marca = request.getParameter("marca");
                String modelo = request.getParameter("modelo");
                String problemas = request.getParameter("problemasFrecuentes");

                EquipoCliente equipoEditado = new EquipoCliente();
                equipoEditado.setIdEquipo(idEquipo); 
                equipoEditado.setTipo(tipo);
                equipoEditado.setMarca(marca);
                equipoEditado.setModelo(modelo);
                equipoEditado.setProblemasFrecuentes(problemas);
                equipoEditado.setCliente(clienteLogueado);

                controlLogica.editarEquipo(equipoEditado); 
                response.sendRedirect("miEquipo.jsp?exito=editado"); 

            } else {
                // --- ACCIÓN POR DEFECTO: CREAR ---
                String tipo = request.getParameter("tipo");
                String marca = request.getParameter("marca");
                String modelo = request.getParameter("modelo");
                String problemasFrecuentes = request.getParameter("problemasFrecuentes");

                EquipoCliente nuevoEquipo = new EquipoCliente();
                nuevoEquipo.setTipo(tipo);
                nuevoEquipo.setMarca(marca);
                nuevoEquipo.setModelo(modelo);
                nuevoEquipo.setProblemasFrecuentes(problemasFrecuentes);
                nuevoEquipo.setCliente(clienteLogueado);

                controlLogica.crearEquipo(nuevoEquipo); 
                response.sendRedirect("miEquipo.jsp?exito=creado");
            }
            
        } catch (NumberFormatException e) {
             response.sendRedirect("miEquipo.jsp?error=id_invalido");
        } catch (Exception e) { 
            System.err.println("Error en doPost de EquipoServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("miEquipo.jsp?error=interno");
        }
    }
}
