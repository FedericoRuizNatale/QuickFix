<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%-- Para formatear moneda/números si los necesitas --%>

<%-- Seguridad: Verifica sesión y rol de Técnico --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Tecnico'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mis Solicitudes Asignadas - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .table thead th { background-color: #0d6efd; color: white; } /* Azul para solicitudes */
    </style>
</head>
<body>

    <%-- Incluir la Barra de Navegación del Técnico --%>
    <%@ include file="../includes/navbarTecnico.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-dark"><i class="fas fa-tasks me-3 text-primary"></i> Mis Solicitudes de Servicio Asignadas</h1>
        <p class="text-muted">Aquí puedes ver las solicitudes de servicio activas que te han sido asignadas.</p>
        <hr>

        <%-- Mensaje si hay error al cargar --%>
        <c:if test="${param.error == 'carga_solicitudes'}">
            <div class="alert alert-danger">Error al cargar las solicitudes. Intenta más tarde.</div>
        </c:if>
         <%-- Mensaje de éxito genérico (si se actualiza estado en el futuro) --%>
        <c:if test="${not empty param.exito}">
             <div class="alert alert-success">${param.exito}</div>
        </c:if>


        <%-- Tabla de Solicitudes Asignadas --%>
        <div class="card shadow-sm">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty listaSolicitudesAsignadas}">
                        <div class="alert alert-info text-center" role="alert">
                            <i class="fas fa-info-circle me-2"></i> No tienes solicitudes de servicio activas asignadas en este momento.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-hover table-bordered align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Fecha Creación</th>
                                    <th>Cliente</th>
                                    <th>Equipo</th>
                                    <th>Servicio</th>
                                    <th>Prioridad</th>
                                    <th>Estado</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="solicitud" items="${listaSolicitudesAsignadas}">
                                    <tr>
                                        <td>${solicitud.idSolicitud}</td>
                                        <td>
                                            <%-- Formato de fecha para LocalDateTime --%>
                                            ${String.format('%02d', solicitud.fechaHoraCreacion.dayOfMonth)}/${String.format('%02d', solicitud.fechaHoraCreacion.monthValue)}/${solicitud.fechaHoraCreacion.year} ${String.format('%02d', solicitud.fechaHoraCreacion.hour)}:${String.format('%02d', solicitud.fechaHoraCreacion.minute)}
                                        </td>
                                        <td>${solicitud.cliente.nombre} ${solicitud.cliente.apellido}</td>
                                        <td>
                                            <c:if test="${not empty solicitud.equipoCliente}">
                                                ${solicitud.equipoCliente.marca} ${solicitud.equipoCliente.modelo}
                                            </c:if>
                                            <c:if test="${empty solicitud.equipoCliente}">N/A</c:if>
                                        </td>
                                        <td>${solicitud.servicio.nombre}</td>
                                        <td>
                                            <span class="badge ${solicitud.prioridad == 'URGENTE' ? 'bg-danger' : solicitud.prioridad == 'ALTA' ? 'bg-warning text-dark' : 'bg-secondary'}">
                                                ${solicitud.prioridad}
                                            </span>
                                        </td>
                                        <td>
                                            <%-- Badge de color según el estado --%>
                                            <span class="badge ${solicitud.estado == 'RECIBIDA' || solicitud.estado == 'EN_DIAGNOSTICO' ? 'bg-warning text-dark' : solicitud.estado == 'APROBADA' || solicitud.estado == 'EN_REPARACION' ? 'bg-info text-dark' : solicitud.estado == 'LISTA_PARA_RETIRO' ? 'bg-primary' : 'bg-secondary'}">
                                                ${solicitud.estado}
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <%-- Botón para Ver Detalles / Actualizar Estado --%>
                                            <%-- Este enlace debería llevar a un Servlet/JSP para gestionar esta solicitud específica --%>
                                            <a href="GestionDetalleSolicitudServlet?id=${solicitud.idSolicitud}" class="btn btn-primary btn-sm">
                                                <i class="fas fa-edit me-1"></i> Gestionar
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div> <%-- Fin container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>