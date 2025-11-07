<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- Seguridad --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Administrador'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Asignar Solicitudes - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style> body { padding-top: 56px; background-color: #f8f9fa; } </style>
</head>
<body>

    <%@ include file="../includes/navbarAdmin.jsp" %>

    <div class="container mt-4">
        
        <h1 class="mb-4 text-dark"><i class="fas fa-clipboard-list me-3 text-primary"></i> Asignación de Solicitudes Pendientes</h1>
        <hr>

        <%-- Mensajes --%>
        <c:if test="${param.exito == 'asignacion_ok'}"> <div class="alert alert-success">¡Solicitud asignada al técnico correctamente!</div> </c:if>
        <c:if test="${not empty param.error}"> <div class="alert alert-danger">${param.error}</div> </c:if>

        <div class="card shadow-sm">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty listaSolicitudesNuevas}">
                        <div class="alert alert-info text-center">No hay solicitudes nuevas pendientes de asignación.</div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-hover align-middle">
                            <thead class="table-dark">
                                <tr>
                                    <th>ID</th>
                                    <th>Cliente</th>
                                    <th>Servicio Requerido</th>
                                    <th>Equipo</th>
                                    <th>Asignar Técnico</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="solicitud" items="${listaSolicitudesNuevas}">
                                    <tr>
                                        <td>${solicitud.idSolicitud}</td>
                                        <td>${solicitud.cliente.nombre} ${solicitud.cliente.apellido}</td>
                                        <td><span class="badge bg-success">${solicitud.servicio.nombre}</span></td>
                                        <td>${solicitud.equipoCliente.marca} ${solicitud.equipoCliente.modelo}</td>
                                        <td>
                                            <%-- Formulario para asignar --%>
                                            <form action="AsignacionServlet" method="POST" class="d-flex">
                                                <input type="hidden" name="idSolicitud" value="${solicitud.idSolicitud}">
                                                
                                                <select name="idTecnico" class="form-select form-select-sm" required>
                                                    <option value="" disabled selected>-- Seleccionar Técnico --</option>
                                                    <c:forEach var="tec" items="${listaTecnicosActivos}">
                                                        <%-- ✅ MUESTRA ESPECIALIDAD Y CARGA --%>
                                                        <option value="${tec.idUsuario}">
                                                            ${tec.nombre} ${tec.apellido} (${tec.especialidad}) 
                                                            - Carga: ${cargaPorTecnico[tec.idUsuario]}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                
                                                <button typed="submit" class="btn btn-primary btn-sm ms-2">Asignar</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div> 

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>