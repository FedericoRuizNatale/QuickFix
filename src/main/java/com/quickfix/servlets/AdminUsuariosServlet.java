package com.quickfix.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

import com.quickfix.entities.*; // Importa todas tus entidades
import com.quickfix.logic.ControladorLogica;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet(name = "AdminUsuariosServlet", urlPatterns = {"/admin/AdminUsuariosServlet"})
public class AdminUsuariosServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Seguridad: Verificar Sesión y Rol de Administrador
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        
        String rolAMostrar = request.getParameter("rol");
        if (rolAMostrar == null || rolAMostrar.isEmpty()) {
            rolAMostrar = "cliente"; // Vista por defecto
        }

        String jspTarget = ""; // El JSP al que haremos forward
        
        try {
            // Cargar la lista correspondiente según el rol
            switch (rolAMostrar.toLowerCase()) {
                case "cliente":
                    List<Cliente> listaClientes = controlLogica.traerTodosLosClientes();
                    request.setAttribute("listaUsuarios", listaClientes);
                    jspTarget = "clientes_abmc.jsp";
                    break;
                case "tecnico":
                    List<Tecnico> listaTecnicos = controlLogica.traerTodosLosTecnicos();
                    request.setAttribute("listaUsuarios", listaTecnicos);
                    jspTarget = "tecnicos_abmc.jsp";
                    break;
                case "administrador":
                    List<Administrador> listaAdmins = controlLogica.traerTodosLosAdmins();
                    request.setAttribute("listaUsuarios", listaAdmins);
                    jspTarget = "admins_abmc.jsp";
                    break;
                default:
                    // Rol no válido, redirigir al home del admin con error
                    response.sendRedirect("homeAdmin.jsp?error=rol_invalido");
                    return;
            }

            // 4. Guardar el rol actual en el request para que el JSP sepa qué mostrar
            request.setAttribute("rolActual", rolAMostrar);

            // 5. Forward al JSP correspondiente
            request.getRequestDispatcher(jspTarget).forward(request, response);

        } catch (Exception e) {
            System.err.println("Error al cargar lista de usuarios (" + rolAMostrar + "): " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("homeAdmin.jsp?error=carga_usuarios");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Seguridad: Verificar Sesión y Rol de Administrador
        HttpSession miSesion = request.getSession(false);
        if (miSesion == null || !"Administrador".equals(miSesion.getAttribute("rolUsuario"))) {
            response.sendRedirect("../login.jsp");
            return;
        }

        // 2. Obtener la acción (create, update, delete) y el rol
        String action = request.getParameter("action");
        String rol = request.getParameter("rol"); // Cliente, Tecnico, Administrador

        // Redirección base en caso de éxito o error (vuelve a la tabla del rol correspondiente)
        String redirectURL = "AdminUsuariosServlet?rol=" + rol;

        try {
            switch (action) {
                case "create":
                    // --- LÓGICA PARA CREAR ---
                    
                    String nombre = request.getParameter("nombre");
                    String apellido = request.getParameter("apellido");
                    String email = request.getParameter("email");
                    String password = request.getParameter("password");
                    String telefono = request.getParameter("telefono");
                    String direccion = request.getParameter("direccion");

                    // Validar email único ANTES de crear
                    if (controlLogica.obtenerUsuarioConEmail(email) != null) {
                         response.sendRedirect(redirectURL + "&error=" + URLEncoder.encode("El email ya está registrado.", "UTF-8"));
                         return;
                    }

                    // Crear el objeto específico según el rol
                    if ("cliente".equalsIgnoreCase(rol)) {
                        String dni = request.getParameter("dni");
                        Cliente cli = new Cliente(nombre, apellido, email, password, telefono, direccion, true, LocalDateTime.now(), 0, dni);
                        controlLogica.crearCliente(cli);
                    } else if ("tecnico".equalsIgnoreCase(rol)) {
                        String especialidad = request.getParameter("especialidad");
                        String disponibilidad = request.getParameter("disponibilidad");
                        Tecnico tec = new Tecnico(nombre, apellido, email, password, telefono, direccion, true, LocalDateTime.now(), disponibilidad, especialidad);
                        controlLogica.crearTecnico(tec); // Necesitas este método
                    } else if ("administrador".equalsIgnoreCase(rol)) {
                        Integer nivelAcceso = Integer.parseInt(request.getParameter("nivelAcceso"));
                        Administrador adm = new Administrador(nombre, apellido, email, password, telefono, direccion, true, LocalDateTime.now(), nivelAcceso);
                        controlLogica.crearAdmin(adm); // Ya tienes este método
                    }
                    redirectURL += "&exito=creado";
                    break;

                case "update":
                    // --- LÓGICA PARA EDITAR ---
                    Integer idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
                    
                    String nombreUpd = request.getParameter("nombre");
                    String apellidoUpd = request.getParameter("apellido");
                    String emailUpd = request.getParameter("email");
                    
                    String telefonoUpd = request.getParameter("telefono");
                    String direccionUpd = request.getParameter("direccion");
                    boolean estadoUpd = "on".equals(request.getParameter("estado")); // Checkbox

                    
                     if ("cliente".equalsIgnoreCase(rol)) {
                        Cliente cli = controlLogica.traerClienteCompleto(idUsuario);
                        if(cli != null) {
                            cli.setNombre(nombreUpd); cli.setApellido(apellidoUpd); cli.setEmail(emailUpd);
                            cli.setTelefono(telefonoUpd); cli.setDireccion(direccionUpd); cli.setEstado(estadoUpd);
                            cli.setDni(request.getParameter("dni")); 
                            controlLogica.editarCliente(cli); 
                        }
                    } else if ("tecnico".equalsIgnoreCase(rol)) {
                         Tecnico tec = controlLogica.traerTecnicoCompleto(idUsuario);
                         if(tec != null) {
                            tec.setNombre(nombreUpd); tec.setApellido(apellidoUpd); tec.setEmail(emailUpd);
                            tec.setTelefono(telefonoUpd); tec.setDireccion(direccionUpd); tec.setEstado(estadoUpd);
                            tec.setEspecialidad(request.getParameter("especialidad")); 
                            tec.setDisponibilidad(request.getParameter("disponibilidad")); 
                            controlLogica.editarTecnico(tec); 
                         }
                    } else if ("administrador".equalsIgnoreCase(rol)) {
                         Administrador adm = controlLogica.traerAdminCompleto(idUsuario); // Necesitas este método
                         if(adm != null) {
                            adm.setNombre(nombreUpd); adm.setApellido(apellidoUpd); adm.setEmail(emailUpd);
                            adm.setTelefono(telefonoUpd); adm.setDireccion(direccionUpd); adm.setEstado(estadoUpd);
                            adm.setNivelAcceso(Integer.parseInt(request.getParameter("nivelAcceso"))); // Específico
                            controlLogica.editarAdmin(adm); 
                         }
                    }
                    redirectURL += "&exito=editado";
                    break;

                case "delete":
                    // --- LÓGICA PARA ELIMINAR ---
                    Integer idUsuarioDel = Integer.parseInt(request.getParameter("idUsuario"));
                    
                    
                     if ("cliente".equalsIgnoreCase(rol)) {
                        controlLogica.eliminarCliente(idUsuarioDel);
                    } else if ("tecnico".equalsIgnoreCase(rol)) {
                        controlLogica.eliminarTecnico(idUsuarioDel); 
                    } else if ("administrador".equalsIgnoreCase(rol)) {
                         
                         Usuario adminLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
                         if(adminLogueado.getIdUsuario().equals(idUsuarioDel) && controlLogica.contarAdmins() <= 1) {
                              redirectURL += "&error=" + URLEncoder.encode("No puedes eliminar al último administrador.", "UTF-8");
                         } else {
                              controlLogica.eliminarAdmin(idUsuarioDel);
                              redirectURL += "&exito=eliminado";
                         }
                    }
                    break;

                default:
                    redirectURL += "&error=accion_desconocida";
                    break;
            }

            response.sendRedirect(redirectURL);

        } catch (NumberFormatException e) {
            response.sendRedirect(redirectURL + "&error=id_invalido");
        } catch (Exception e) {
            System.err.println("Error en doPost de AdminUsuariosServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(redirectURL + "&error=" + URLEncoder.encode("Error interno: " + e.getMessage(), "UTF-8"));
        }
    }
}
