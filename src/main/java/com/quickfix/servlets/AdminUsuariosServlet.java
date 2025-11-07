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

/**
 * Servlet para que el Administrador gestione el ABMC (CRUD) de todos los usuarios.
 */
// URL dentro de la carpeta protegida del admin
@WebServlet(name = "AdminUsuariosServlet", urlPatterns = {"/admin/AdminUsuariosServlet"})
public class AdminUsuariosServlet extends HttpServlet {

	private final ControladorLogica controlLogica = ControladorLogica.getInstance();

    /**
     * doGet: Carga la lista de usuarios según el rol solicitado y muestra el JSP correspondiente.
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

        // 2. Determinar qué rol se quiere listar (por defecto, Clientes)
        String rolAMostrar = request.getParameter("rol");
        if (rolAMostrar == null || rolAMostrar.isEmpty()) {
            rolAMostrar = "cliente"; // Vista por defecto
        }

        String jspTarget = ""; // El JSP al que haremos forward
        
        try {
            // 3. Cargar la lista correspondiente según el rol
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

    /**
     * doPost: Procesa las acciones de Crear, Editar o Eliminar un usuario.
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

        // 2. Obtener la acción (create, update, delete) y el rol
        String action = request.getParameter("action");
        String rol = request.getParameter("rol"); // Cliente, Tecnico, Administrador

        // Redirección base en caso de éxito o error (vuelve a la tabla del rol correspondiente)
        String redirectURL = "AdminUsuariosServlet?rol=" + rol;

        try {
            switch (action) {
                case "create":
                    // --- LÓGICA PARA CREAR ---
                    // Extraer datos comunes de Usuario
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
                    // Extraer datos comunes actualizados
                    String nombreUpd = request.getParameter("nombre");
                    String apellidoUpd = request.getParameter("apellido");
                    String emailUpd = request.getParameter("email");
                    // OJO: Manejo de contraseña (no se suele pasar en editar, o se maneja aparte)
                    String telefonoUpd = request.getParameter("telefono");
                    String direccionUpd = request.getParameter("direccion");
                    boolean estadoUpd = "on".equals(request.getParameter("estado")); // Checkbox

                    // Lógica específica para cada rol
                     if ("cliente".equalsIgnoreCase(rol)) {
                        Cliente cli = controlLogica.traerClienteCompleto(idUsuario);
                        if(cli != null) {
                            cli.setNombre(nombreUpd); cli.setApellido(apellidoUpd); cli.setEmail(emailUpd);
                            cli.setTelefono(telefonoUpd); cli.setDireccion(direccionUpd); cli.setEstado(estadoUpd);
                            cli.setDni(request.getParameter("dni")); // DNI específico
                            controlLogica.editarCliente(cli); // Necesitas este método
                        }
                    } else if ("tecnico".equalsIgnoreCase(rol)) {
                         Tecnico tec = controlLogica.traerTecnicoCompleto(idUsuario);
                         if(tec != null) {
                            tec.setNombre(nombreUpd); tec.setApellido(apellidoUpd); tec.setEmail(emailUpd);
                            tec.setTelefono(telefonoUpd); tec.setDireccion(direccionUpd); tec.setEstado(estadoUpd);
                            tec.setEspecialidad(request.getParameter("especialidad")); // Específico
                            tec.setDisponibilidad(request.getParameter("disponibilidad")); // Específico
                            controlLogica.editarTecnico(tec); // Necesitas este método
                         }
                    } else if ("administrador".equalsIgnoreCase(rol)) {
                         Administrador adm = controlLogica.traerAdminCompleto(idUsuario); // Necesitas este método
                         if(adm != null) {
                            adm.setNombre(nombreUpd); adm.setApellido(apellidoUpd); adm.setEmail(emailUpd);
                            adm.setTelefono(telefonoUpd); adm.setDireccion(direccionUpd); adm.setEstado(estadoUpd);
                            adm.setNivelAcceso(Integer.parseInt(request.getParameter("nivelAcceso"))); // Específico
                            controlLogica.editarAdmin(adm); // Necesitas este método
                         }
                    }
                    redirectURL += "&exito=editado";
                    break;

                case "delete":
                    // --- LÓGICA PARA ELIMINAR ---
                    Integer idUsuarioDel = Integer.parseInt(request.getParameter("idUsuario"));
                    
                    // Necesitas métodos de eliminación específicos por rol en la lógica
                    // Estos métodos deberían llamar a los delete() de los DAOs correspondientes
                     if ("cliente".equalsIgnoreCase(rol)) {
                        controlLogica.eliminarCliente(idUsuarioDel); // Necesitas este método
                    } else if ("tecnico".equalsIgnoreCase(rol)) {
                        controlLogica.eliminarTecnico(idUsuarioDel); // Necesitas este método
                    } else if ("administrador".equalsIgnoreCase(rol)) {
                         // Añadir validación: No permitir auto-eliminación si es el único admin
                         Usuario adminLogueado = (Usuario) miSesion.getAttribute("usuarioLogueado");
                         if(adminLogueado.getIdUsuario().equals(idUsuarioDel) && controlLogica.contarAdmins() <= 1) {
                              redirectURL += "&error=" + URLEncoder.encode("No puedes eliminar al último administrador.", "UTF-8");
                         } else {
                              controlLogica.eliminarAdmin(idUsuarioDel); // Necesitas este método
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
