<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.quickfix.logic.ControladorLogica" %>
<%@ page import="com.quickfix.entities.Cliente" %>
<%@ page import="com.quickfix.entities.EquipoCliente" %>
<%@ page import="java.util.List" %>

<%
    // ====================================================================
    // LÓGICA DE SEGURIDAD (Obligatoria en todas las páginas privadas)
    // ====================================================================
    if (session.getAttribute("usuarioLogueado") == null || 
        !"Cliente".equals(session.getAttribute("rolUsuario"))) {
        response.sendRedirect("../login.jsp");
        return;
    }
    
    // ====================================================================
    // LÓGICA DE CARGA DE DATOS PARA LA TABLA
    // ====================================================================
    
    ControladorLogica controlLogica = new ControladorLogica();
    Cliente clienteLogueado = (Cliente) session.getAttribute("usuarioLogueado");
    
    // Creamos una variable para guardar la lista de equipos
    // (Asegúrate de que este método exista y el DAO se inicialice correctamente)
    List<EquipoCliente>listaEquipos = controlLogica.traerEquiposPorCliente(clienteLogueado); 
    
    // Guardamos la lista en el Request Scope para usarla con JSTL
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
        
        <c:choose>
            <c:when test="${empty listaEquipos}">
                <div class="alert alert-info text-center" role="alert">
                    Aún no tienes ningún equipo registrado. Por favor, utiliza el botón superior para agregar uno.
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${param.exito == 'true'}">
                    <div class="alert alert-success">¡Equipo registrado con éxito!</div>
                </c:if>

                <table class="table table-striped table-hover shadow-sm">
                    <thead>
                        <tr class="bg-light">
                            <th>ID</th>
                            <th>Tipo</th>
                            <th>Marca y Modelo</th>
                            <th>Problemas Comunes</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="equipo" items="${listaEquipos}">
                            <tr>
                                <td>${equipo.idEquipo}</td>
                                <td>${equipo.tipo}</td>
                                <td>${equipo.marca} - ${equipo.modelo}</td>
                                <td>${equipo.problemasFrecuentes}</td>
                                <td>
                                    <a href="#" class="btn btn-sm btn-warning">Editar</a>
                                    <a href="#" class="btn btn-sm btn-danger">Eliminar</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="modal fade" id="modalRegistroEquipo" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="modalLabel">Registrar Nuevo Equipo</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form action="EquipoServlet" method="POST"> 
             <div class="modal-body">
                <div class="mb-3">
                    <label for="tipoInput" class="form-label">Tipo de Equipo</label>
                    <input type="text" class="form-control" name="tipo" required placeholder="Ej: PC de Escritorio, Laptop">
                </div>
                <div class="mb-3">
                    <label for="marcaInput" class="form-label">Marca</label>
                    <input type="text" class="form-control" name="marca" required placeholder="Ej: Dell, HP, Armada">
                </div>
                <div class="mb-3">
                    <label for="modeloInput" class="form-label">Modelo</label>
                    <input type="text" class="form-control" name="modelo" required>
                </div>
                <div class="mb-3">
                    <label for="problemasInput" class="form-label">Problemas Frecuentes</label>
                    <textarea class="form-control" name="problemasFrecuentes" rows="3" placeholder="Ej: Falla en el arranque, Pantalla azul"></textarea>
                </div>
             </div>
             <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar Equipo</button>
             </div>
          </form>
        </div>
      </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
</body>
</html>