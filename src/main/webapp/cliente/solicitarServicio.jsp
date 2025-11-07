<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> 
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Lógica de seguridad (sin cambios) --%>
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
    
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js'></script>
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .card-header { background-color: #0d6efd; color: white; }
        
        /* Estilos para el calendario del cliente */
        #calendario-cliente .fc-event {
            cursor: pointer; /* Hace que los slots parezcan clickeables */
            background-color: #198754; /* Verde (disponible) */
            border-color: #198754;
        }
        #calendario-cliente .fc-event-main {
             font-weight: bold;
        }
        /* Oculta el fondo gris de "businessHours" que no necesitamos */
        .fc-non-business { background: none; }
    </style>
    </head>
<body>

    <%@ include file="../includes/navbarCliente.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-primary"><i class="fas fa-tools me-3"></i> Formulario de Solicitud de Servicio</h1>
        <hr>

        <%-- Mensajes de Error (sin cambios) --%>
        <%-- ... --%>

        <div class="row">
            <div class="col-lg-8">
                <div class="card shadow-lg p-4 mb-4">
                    <h5 class="card-title text-center mb-4 border-bottom pb-2">Datos para Agendar Cita</h5>

                    <form action="${pageContext.request.contextPath}/cliente/SolicitudServlet" method="POST" id="formSolicitud">

                        <%-- Sección Equipo (sin cambios) --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-desktop me-2"></i> 1. Equipo a Reparar:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                            <%-- ... (tu código de <select id="equipoSelect"> se queda igual) ... --%>
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


                        <%-- Sección Servicio (sin cambios) --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-cogs me-2"></i> 2. Tipo de Servicio:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                             <%-- ... (tu código de <select id="servicioSelect"> se queda igual) ... --%>
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


                        <h6 class="fw-bold text-dark"><i class="fas fa-calendar-alt me-2"></i> 3. Turno de Asistencia:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                            <label class="form-label">Selecciona un Turno Disponible *</label>
                            
                            <div id="calendario-cliente"></div>
                            
                            <input type="hidden" id="turnoSeleccionadoInput" name="fechaHoraTurno" value="" required>
                            
                            <div id="turnoHelper" class="alert alert-info mt-3" style="display:none;">
                                <strong>Turno seleccionado:</strong> <span id="turnoTexto"></span>
                            </div>
                        </div>
                        <%-- Sección Detalle y Prioridad (sin cambios) --%>
                        <h6 class="fw-bold text-dark"><i class="fas fa-file-alt me-2"></i> 4. Detalle y Prioridad:</h6>
                        <div class="mb-4 border p-3 bg-light rounded">
                             <%-- ... (tu código de <textarea> y <select> se queda igual) ... --%>
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
                 <%-- ... (tu card de "Importante" se queda igual) ... --%>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const calendarEl = document.getElementById('calendario-cliente');
            const hiddenInput = document.getElementById('turnoSeleccionadoInput');
            const turnoHelper = document.getElementById('turnoHelper');
            const turnoTexto = document.getElementById('turnoTexto');
            
            // ⬇️ --- CORREGIDO 2/2: Ruta absoluta al servlet (para el GET de AJAX) --- ⬇️
            const servletURL = '${pageContext.request.contextPath}/cliente/SolicitudServlet';

            const calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: 'timeGridWeek', // Vista semanal
                locale: 'es',
                headerToolbar: {
                    left: 'prev,next',
                    center: 'title',
                    right: 'today'
                },
                
                // Esta URL ahora SÍ es correcta y el 404 desaparecerá
                events: servletURL + '?action=fetchSlotsDisponibles',
                
                // Formato de hora
                slotLabelFormat: { hour: '2-digit', minute: '2-digit', hour12: false },
                eventTimeFormat: { hour: '2-digit', minute: '2-digit', hour12: false },
                
                // --- Acción al hacer clic en un slot verde ---
                eventClick: function(info) {
                    const evento = info.event;
                    const fechaInicio = evento.start; // Objeto Date de JS
                    
                    // 1. Guardar la fecha/hora en el input oculto (formato ISO para el servlet)
                    hiddenInput.value = fechaInicio.toISOString().substring(0, 19);
                    
                    // 2. Mostrar la selección al usuario
                    const fechaFormato = fechaInicio.toLocaleString('es-AR', {
                        day: '2-digit', month: '2-digit', year: 'numeric',
                        hour: '2-digit', minute: '2-digit'
                    });
                    turnoTexto.textContent = fechaFormato;
                    turnoHelper.style.display = 'block';
                    
                    // (Opcional) Resaltar el slot seleccionado
                    document.querySelectorAll('.fc-event.slot-seleccionado').forEach(el => {
                        el.classList.remove('slot-seleccionado');
                        el.style.backgroundColor = '#198754'; // Verde
                    });
                    info.el.style.backgroundColor = '#0a58ca'; // Azul
                    info.el.classList.add('slot-seleccionado');
                }
            });

            calendar.render();
        });
    </script>
    </body>
</html>