<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- Seguridad (Asegúrate de que el admin esté logueado) --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Administrador'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Configuración de Agenda</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style> body { padding-top: 56px; background-color: #f8f9fa; } </style>
</head>
<body>

    <%@ include file="../includes/navbarAdmin.jsp" %>

    <div class="container mt-4">
        
        <h1 class="mb-4 text-dark"><i class="fas fa-calendar-alt me-3 text-primary"></i> Configuración de Turnos Base</h1>
        <hr>

        <%-- Mensajes de Éxito/Error --%>
		<c:if test="${param.exito == 'horario_ok'}">
			<div class="alert alert-success">¡Horario laboral guardado
				correctamente!</div>
		</c:if>
		<c:if test="${param.exito == 'feriado_ok'}">
			<div class="alert alert-success">¡Día no laboral agregado!</div>
		</c:if>
		<c:if test="${param.exito == 'borrado_ok'}">
			<div class="alert alert-warning">Día no laboral eliminado.</div>
		</c:if>
		<c:if test="${not empty param.error}">
			<div class="alert alert-danger">
				<strong>Error:</strong> ${param.error}
			</div>
		</c:if>


		<div class="card shadow-sm mb-4">
            <div class="card-header fs-5">
                <i class="fas fa-clock me-2"></i> Plantilla de Horario Laboral
            </div>
            <div class="card-body">
                <form action="ConfigAgendaServlet" method="POST">
                    <input type="hidden" name="action" value="guardarHorario">
                    
                    <div class="mb-4">
                        <label for="intervalo" class="form-label fw-bold">Duración de cada Turno (en minutos):</label>
                        <input type="number" class="form-control" id="intervalo" name="intervalo" value="${config.intervaloMinutos}" min="15" step="15" style="max-width: 200px;">
                    </div>

                    <h5 class="mb-3">Horario Semanal</h5>
                    <table class="table table-bordered align-middle" style="max-width: 700px;">
                        <thead class="table-light">
                            <tr>
                                <th>Día</th>
                                <th>Hora de Inicio</th>
                                <th>Hora de Fin</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%-- Definimos los nombres de los días --%>
								<c:set var="dias" value="Lunes,Martes,Miércoles,Jueves,Viernes,Sábado,Domingo" />
								<c:set var="diasArray" value="${dias.split(',')}" />
                            
                            <%-- Iteramos sobre la lista de 7 horarios que trajo el Servlet --%>
                            <c:forEach var="horario" items="${listaHorarios}" varStatus="loop">
                                <tr>
                                   	<td class="fw-bold">${diasArray[loop.index]}</td>
                                    <td>
                                        <input type="time" class="form-control" name="inicio_${horario.diaSemana}" value="${horario.horaInicio}">
                                    </td>
                                    <td>
                                        <input type="time" class="form-control" name="fin_${horario.diaSemana}" value="${horario.horaFin}">
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save me-2"></i> Guardar Horario</button>
                </form>
            </div>
        </div>


        <div class="card shadow-sm">
            <div class="card-header fs-5">
                <i class="fas fa-calendar-times me-2"></i> Días No Laborales (Feriados)
            </div>
            <div class="card-body">
                
                <form action="ConfigAgendaServlet" method="POST" class="row g-3 mb-4 border-bottom pb-4">
                    <input type="hidden" name="action" value="agregarFeriado">
                    <div class="col-md-4">
                        <label for="fechaFeriado" class="form-label">Fecha</label>
                        <input type="date" class="form-control" name="fechaFeriado" required>
                    </div>
                    <div class="col-md-6">
                        <label for="descFeriado" class="form-label">Descripción (Ej: Navidad)</label>
                        <input type="text" class="form-control" name="descFeriado" required>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-success w-100"><i class="fas fa-plus me-2"></i> Agregar</button>
                    </div>
                </form>

                <h5>Feriados Cargados</h5>
                <c:choose>
                    <c:when test="${empty listaFeriados}">
                        <div class="alert alert-info">No hay feriados cargados.</div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-striped table-sm">
                            <tbody>
                                <c:forEach var="feriado" items="${listaFeriados}">
                                    <tr>
                                        <td>${feriado.fecha}</td>
                                        <td>${feriado.descripcion}</td>
                                        <td class="text-end">
                                            <a href="ConfigAgendaServlet?action=borrarFeriado&id=${feriado.id}" class="btn btn-danger btn-sm">
                                                <i class="fas fa-trash"></i>
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

    </div> 

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>