<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.quickfix.logic.ControladorLogica" %>
<%@ page import="com.quickfix.entities.Cliente" %>
<%@ page import="com.quickfix.entities.EquipoCliente" %>
<%@ page import="java.util.List" %>

<%
    // ====================================================================
    // LÓGICA DE SEGURIDAD
    // ====================================================================
    if (session.getAttribute("usuarioLogueado") == null || 
        !"Cliente".equals(session.getAttribute("rolUsuario"))) {
        response.sendRedirect("../login.jsp");
        return; // Detiene la ejecución si no es cliente
    }
    
    // ====================================================================
    // LÓGICA DE CARGA DE DATOS PARA LA TABLA
    // ====================================================================
    ControladorLogica controlLogica = new ControladorLogica();
    Cliente clienteLogueado = (Cliente) session.getAttribute("usuarioLogueado");
    
    List<EquipoCliente> listaEquipos = null;
    if (clienteLogueado != null) {
         listaEquipos = controlLogica.traerEquiposPorCliente(clienteLogueado); 
    }
    
    request.setAttribute("listaEquipos", listaEquipos);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mis Equipos - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <style> body { padding-top: 56px; } </style>
</head>
<body>

    <%@ include file="../includes/navbarCliente.jsp" %>

    <div class="container mt-4">
        
        <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-4">
            <h1><i class="fas fa-desktop me-2"></i> Mis Equipos Registrados</h1>
            <a class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalRegistroEquipo">
                <i class="fas fa-plus me-2"></i> Registrar Nuevo Equipo
            </a>
        </div>
        
        <%-- Mensajes de Éxito/Error --%>
        <c:if test="${param.exito == 'creado'}"> <div class="alert alert-success">¡Equipo registrado!</div> </c:if>
        <c:if test="${param.exito == 'editado'}"> <div class="alert alert-success">¡Equipo actualizado!</div> </c:if>
        <c:if test="${param.exito == 'eliminado'}"> <div class="alert alert-success">¡Equipo eliminado!</div> </c:if>
        <c:if test="${not empty param.error}"> <div class="alert alert-danger">Error: ${param.error}</div> </c:if>

        <%-- Tabla de Equipos --%>
        <c:choose>
            <c:when test="${empty listaEquipos}">
                <div class="alert alert-info text-center" role="alert">
                    Aún no tienes ningún equipo registrado. Por favor, utiliza el botón superior para agregar uno.
                </div>
            </c:when>
            <c:otherwise>
                <table class="table table-striped table-hover shadow-sm align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th> <th>Tipo</th> <th>Marca y Modelo</th> <th>Problemas Comunes</th> <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="equipo" items="${listaEquipos}">
                            <tr>
                                <td>${equipo.idEquipo}</td>
                                <td>${equipo.tipo}</td>
                                <td>${equipo.marca} - ${equipo.modelo}</td>
                                <td>${equipo.problemasFrecuentes}</td>
                                <td class="text-center">
                                    <%-- BOTÓN EDITAR (Llama a JavaScript) --%>
                                    <button type="button" class="btn btn-sm btn-warning me-1" 
                                            onclick="abrirModalEdicion(${equipo.idEquipo}, '${equipo.tipo}', '${equipo.marca}', '${equipo.modelo}', '${equipo.problemasFrecuentes}')">
                                        <i class="fas fa-edit"></i> Editar
                                    </button>

                                    <%-- BOTÓN ELIMINAR (Formulario POST) --%>
                                    <form action="EquipoServlet" method="POST" style="display:inline;" onsubmit="return confirm('¿Estás seguro de que quieres eliminar este equipo?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="idEquipo" value="${equipo.idEquipo}">
                                        <button type="submit" class="btn btn-sm btn-danger">
                                            <i class="fas fa-trash"></i> Eliminar
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- MODAL (Sirve para Crear y Editar) --%>
    <div class="modal fade" id="modalRegistroEquipo" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="modalLabel">Registrar Nuevo Equipo</h5> 
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="formEquipo" action="EquipoServlet" method="POST"> 
             <div class="modal-body">
                <%-- Campos ocultos para Editar --%>
                <input type="hidden" name="action" id="formAction" value="create"> <%-- Valor por defecto al crear --%>
                <input type="hidden" name="idEquipo" id="formIdEquipo" value="0"> 
             
                <%-- Campos Visibles --%>
                <div class="mb-3">
                    <label for="tipoInput" class="form-label">Tipo de Equipo</label>
                    <input type="text" class="form-control" id="tipoInput" name="tipo" required>
                </div>
                <div class="mb-3">
                    <label for="marcaInput" class="form-label">Marca</label>
                    <input type="text" class="form-control" id="marcaInput" name="marca" required>
                </div>
                <div class="mb-3">
                    <label for="modeloInput" class="form-label">Modelo</label>
                    <input type="text" class="form-control" id="modeloInput" name="modelo" required>
                </div>
                <div class="mb-3">
                    <label for="problemasInput" class="form-label">Problemas Frecuentes</label>
                    <textarea class="form-control" id="problemasInput" name="problemasFrecuentes" rows="3"></textarea>
                </div>
             </div>
             <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-primary" id="btnGuardar">Guardar Equipo</button> 
             </div>
          </form>
        </div>
      </div>
    </div>
    
    <%-- Incluye el JavaScript de Bootstrap (SIEMPRE al final) --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <%-- ========================================================= --%>
    <%-- BLOQUE JAVASCRIPT PARA EL MODAL (Justo antes de </body>) --%>
    <%-- ========================================================= --%>
    <script>
        // Obtener la instancia del modal y sus elementos internos
        var modalEquipo = new bootstrap.Modal(document.getElementById('modalRegistroEquipo'));
        var modalLabel = document.getElementById('modalLabel');
        var btnGuardar = document.getElementById('btnGuardar');
        var formEquipo = document.getElementById('formEquipo'); // Referencia al formulario
        var formAction = document.getElementById('formAction');
        var formIdEquipo = document.getElementById('formIdEquipo');
        var tipoInput = document.getElementById('tipoInput');
        var marcaInput = document.getElementById('marcaInput');
        var modeloInput = document.getElementById('modeloInput');
        var problemasInput = document.getElementById('problemasInput');

        // Función que se ejecuta al hacer clic en el botón "Editar" de la tabla
        function abrirModalEdicion(id, tipo, marca, modelo, problemas) {
            // 1. Cambiar el título y el texto/color del botón Guardar
            modalLabel.textContent = 'Editar Equipo (ID: ' + id + ')';
            btnGuardar.textContent = 'Guardar Cambios';
            btnGuardar.classList.remove('btn-primary'); // Quita el azul
            btnGuardar.classList.add('btn-warning'); // Pone el amarillo de edición

            // 2. Establecer los valores ocultos para que el Servlet sepa que es UPDATE
            formAction.value = 'update';
            formIdEquipo.value = id;
            
            // 3. Rellenar los campos del formulario con los datos del equipo
            tipoInput.value = tipo;
            marcaInput.value = marca;
            modeloInput.value = modelo;
            problemasInput.value = problemas; 
            
            // 4. Abrir el modal
            modalEquipo.show();
        }

        // Evento que se dispara CUANDO el modal se cierra (para resetearlo)
        var modalElement = document.getElementById('modalRegistroEquipo');
        modalElement.addEventListener('hidden.bs.modal', function (event) {
            // 1. Resetear título y botón a los valores de "Crear"
            modalLabel.textContent = 'Registrar Nuevo Equipo';
            btnGuardar.textContent = 'Guardar Equipo';
            btnGuardar.classList.remove('btn-warning'); // Quita el amarillo
            btnGuardar.classList.add('btn-primary'); // Pone el azul de nuevo
            
            // 2. Resetear los campos ocultos a los valores de "Crear"
            formAction.value = 'create'; // Acción por defecto
            formIdEquipo.value = '0'; // ID 0 o vacío indica nuevo
            
            // 3. Limpiar todos los campos visibles del formulario
            formEquipo.reset(); 
        });
    </script>
</body>
</html>