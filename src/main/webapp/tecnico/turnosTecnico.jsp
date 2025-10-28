<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%-- Para formatear fechas si las necesitas --%>

<%-- Seguridad: Verifica sesión y rol de Técnico --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Tecnico'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestionar Disponibilidad - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .table thead th { background-color: #ffc107; color: #343a40; } /* Amarillo para cabecera */
    </style>
</head>
<body>

    <%-- Incluir la Barra de Navegación del Técnico (Asume que existe) --%>
    <%-- <%@ include file="../includes/navbarTecnico.jsp" %> --%>
    <%-- O usa la barra de navegación que tengas --%>
     <%@ include file="../includes/navbarTecnico.jsp" %>


    <div class="container mt-4">

        <h1 class="mb-4 text-dark"><i class="fas fa-calendar-alt me-3 text-warning"></i> Gestionar Disponibilidad de Turnos</h1>
        <p class="text-muted">Publica u oculta los turnos para indicar cuándo estás disponible para nuevas solicitudes de servicio.</p>
        <hr>

        <%-- Mensajes de Éxito o Error (desde el Servlet) --%>
        <c:if test="${param.exito == 'estado_actualizado'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ¡Estado del turno actualizado correctamente!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Error al actualizar el turno: ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <%-- Tabla de Turnos --%>
        <div class="card shadow-sm">
            <div class="card-body">
                <table class="table table-hover table-bordered align-middle">
                    <thead>
                        <tr>
                            <th>Fecha y Hora Inicio</th>
                            <th>Fecha y Hora Fin</th>
                            <th>Estado Actual</th>
                            <th class="text-center">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="turno" items="${listaTodosTurnos}">
                            <tr>
                                <td>${String.format('%02d', turno.fechaHoraInicio.dayOfMonth)}/${String.format('%02d', turno.fechaHoraInicio.monthValue)}/${turno.fechaHoraInicio.year} ${String.format('%02d', turno.fechaHoraInicio.hour)}:${String.format('%02d', turno.fechaHoraInicio.minute)}</td>
                                <td>${String.format('%02d', turno.fechaHoraFin.dayOfMonth)}/${String.format('%02d', turno.fechaHoraFin.monthValue)}/${turno.fechaHoraFin.year} ${String.format('%02d', turno.fechaHoraFin.hour)}:${String.format('%02d', turno.fechaHoraFin.minute)}</td>
                                <td>
                                    <%-- Badge de color según el estado --%>
                                    <span class="badge ${turno.estado == 'DISPONIBLE' ? 'bg-success' : turno.estado == 'RESERVADO' ? 'bg-primary' : turno.estado == 'COMPLETADO' ? 'bg-info' : 'bg-secondary'}">
                                        ${turno.estado}
                                    </span>
                                </td>
                                <td class="text-center">
                                    <%-- Formulario para cambiar el estado --%>
                                    <form action="GestionTurnosServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="idTurno" value="${turno.idTurno}">
                                        
                                        <c:choose>
                                            <c:when test="${turno.estado == 'NO_DISPONIBLE'}">
                                                <%-- Botón para PUBLICAR (cambiar a DISPONIBLE) --%>
                                                <input type="hidden" name="nuevoEstado" value="DISPONIBLE">
                                                <button type="submit" class="btn btn-success btn-sm">
                                                    <i class="fas fa-eye me-1"></i> Publicar
                                                </button>
                                            </c:when>
                                            <c:when test="${turno.estado == 'DISPONIBLE'}">
                                                <%-- Botón para OCULTAR (cambiar a NO_DISPONIBLE) --%>
                                                <input type="hidden" name="nuevoEstado" value="NO_DISPONIBLE">
                                                <button type="submit" class="btn btn-warning btn-sm">
                                                    <i class="fas fa-eye-slash me-1"></i> Ocultar
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <%-- Si está RESERVADO, COMPLETADO, etc., no se puede cambiar desde aquí --%>
                                                <button type="button" class="btn btn-secondary btn-sm" disabled>
                                                    <i class="fas fa-lock me-1"></i> No Editable
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div> <%-- Fin container --%>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>