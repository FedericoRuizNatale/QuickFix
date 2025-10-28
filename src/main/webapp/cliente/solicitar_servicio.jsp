<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%-- Necesario para formatear el Costo Base --%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %> <%-- Necesario para fn:split --%>

<%-- Lógica de seguridad para evitar acceso directo --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Cliente'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Solicitar Servicio - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .card-header { background-color: #0d6efd; color: white; }
    </style>
</head>
<body>

    <%@ include file="../includes/navbarCliente.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-primary"><i class="fas fa-tools me-3"></i> Formulario de Solicitud de Servicio</h1>
        <hr>

        <%-- Mensajes de Error --%>
        <c:if test="${param.error == 'datos_faltantes'}">
            <div class="alert alert-danger mt-3">❌ Error: Debes seleccionar todos los campos obligatorios.</div>
        </c:if>
        <c:if test="${param.error == 'turno_no_disponible'}">
            <div class="alert alert-danger mt-3">❌ Error: El turno que intentas reservar ya fue tomado. Intenta de nuevo.</div>
        </c:if>

        <div class="row">
            <div class="col-lg-8">

                <div class="card shadow-lg p-4 mb-4">
                    <h5 class="card-title text-center mb-4 border-bottom pb-2">Datos para Agendar Cita</h5>

                    <form action="SolicitudServlet" method="POST">

                        <%-- Sección Equipo --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-desktop me-2"></i> 1. Equipo a Reparar:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                            <label for="equipoSelect" class="form-label">Selecciona tu Equipo *</label>
                            <select class="form-select" id="equipoSelect" name="idEquipo" required <c:if test="${empty listaEquipos}">disabled</c:if>>
                                <c:if test="${empty listaEquipos}">
                                    <option value="" disabled selected>— REGISTRA UN EQUIPO PRIMERO —</option>
                                </c:if>
                                <c:forEach var="equipo" items="${listaEquipos}">
                                    <option value="${equipo.idEquipo}">
                                        [${equipo.tipo}] ${equipo.marca} ${equipo.modelo}
                                    </option>
                                </c:forEach>
                            </select>
                             <c:if test="${empty listaEquipos}">
                                <div class="alert alert-info py-2 small mt-2">
                                    No tienes equipos. <a href="miEquipo.jsp" class="alert-link">Haz clic para registrarlos primero</a>.
                                </div>
                            </c:if>
                        </div>

                        <%-- Sección Servicio --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-cogs me-2"></i> 2. Tipo de Servicio:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                            <label for="servicioSelect" class="form-label">Servicio Requerido *</label>
                            <select class="form-select" id="servicioSelect" name="idServicio" required>
                                <option value="" disabled selected>Selecciona una opción</option>
                                <c:forEach var="servicio" items="${listaServicios}">
                                    <option value="${servicio.idServicio}">
                                        ${servicio.nombre} (Costo Base: <fmt:formatNumber value="${servicio.costoBase}" type="currency" currencySymbol="$" maxFractionDigits="0"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <%-- Sección Turno --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-calendar-alt me-2"></i> 3. Turno de Asistencia:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                            <label for="turnoSelect" class="form-label">Turno Disponible *</label>
                            <select class="form-select" id="turnoSelect" name="idTurno" required>
                                <c:choose>
                                     <c:when test="${empty listaTurnosDisponibles}">
                                        <option value="" disabled selected>— NO HAY TURNOS DISPONIBLES —</option>
                                    </c:when>
                                    <c:otherwise>
                                         <option value="" disabled selected>Selecciona un horario</option>
                                        <c:forEach var="turno" items="${listaTurnosDisponibles}">
                                            <%-- ✅ CORRECCIÓN: Mostrar LocalDateTime sin fmt:formatDate --%>
                                            <option value="${turno.idTurno}">
                                                ${String.format('%02d', turno.fechaHoraInicio.dayOfMonth)}/${String.format('%02d', turno.fechaHoraInicio.monthValue)}/${turno.fechaHoraInicio.year}
                                                ${String.format('%02d', turno.fechaHoraInicio.hour)}:${String.format('%02d', turno.fechaHoraInicio.minute)}
                                                -
                                                ${String.format('%02d', turno.fechaHoraFin.hour)}:${String.format('%02d', turno.fechaHoraFin.minute)}
                                            </option>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                            <div class="form-text">Solo se muestran horarios con técnicos ya asignados.</div>
                        </div>

                        <%-- Sección Detalle y Prioridad --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-file-alt me-2"></i> 4. Detalle y Prioridad:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                            <label for="diagnosticoTextarea" class="form-label">Describe el Problema *</label>
                            <textarea class="form-control" id="diagnosticoTextarea" name="diagnostico" rows="3" required placeholder="Describe lo que sucede con el equipo, cuándo comenzó la falla, etc."></textarea>

                            <label for="prioridadSelect" class="form-label mt-3">Prioridad de Atención *</label>
                            <select class="form-select" id="prioridadSelect" name="prioridad" required>
                                <c:forEach var="p" items="${fn:split('BAJA,MEDIA,ALTA,URGENTE', ',')}">
                                    <option value="${p}">${p}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-success btn-lg">
                                <i class="fas fa-check-circle me-2"></i> Confirmar Solicitud y Agendar Turno
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="col-lg-4">
                 <div class="card shadow-sm">
                     <div class="card-header bg-secondary text-white">
                         <i class="fas fa-info-circle me-2"></i> Importante
                     </div>
                     <div class="card-body">
                         <p class="card-text small">
                             Al agendar, el turno queda **reservado**. Recibirás una confirmación por email con los detalles de tu cita.
                         </p>
                         <p class="card-text small">
                            Asegúrate de que la descripción del problema sea lo más detallada posible para acelerar el diagnóstico.
                         </p>
                     </div>
                 </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>