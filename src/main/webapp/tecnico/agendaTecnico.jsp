<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Seguridad --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Tecnico'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestionar Agenda</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js'></script>
    
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        #calendar { max-width: 1100px; margin: 20px auto; }
        /* Estilo para los bloqueos del técnico */
        .fc-event.evento-bloqueo {
            background-color: #dc3545; /* Rojo */
            border-color: #dc3545;
            cursor: pointer;
        }
        .fc-event.evento-reservado {
    background-color: #0d6efd; /* Azul */
    border-color: #0d6efd;
}
    </style>
</head>
<body>

    <%@ include file="../includes/navbarTecnico.jsp" %>

    <div class="container-fluid px-4">
        <h1 class="mb-4 text-dark"><i class="fas fa-calendar-alt me-3 text-warning"></i> Mi Agenda</h1>
        <p class="text-muted">Selecciona un rango de fechas en el calendario para bloquearlo, o haz clic en un bloqueo rojo para eliminarlo.</p>
        <hr>
        
        <div id='calendar'></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        const calendarEl = document.getElementById('calendar');
        
        // URL correcta que apunta al servlet del técnico
        const servletURL = '${pageContext.request.contextPath}/tecnico/AgendaTecnicoServlet';

        // --- 1. PRIMERO, PEDIMOS LAS REGLAS (Horarios) ---
        fetch(servletURL + '?action=fetchRules')
            .then(response => response.json())
            .then(rules => {
                // 'rules' es el Map: { horarios: [...], config: {...} }
                const javaHorarios = rules.horarios;
                const configAgenda = rules.config;

                // --- 2. TRADUCIMOS LAS REGLAS ---
                const businessHoursConfig = javaHorarios
                    .filter(h => h.horaInicio && h.horaFin)
                    .map(h => {
                        const fcDia = (h.diaSemana === 7) ? 0 : h.diaSemana;
                        return {
                            daysOfWeek: [ fcDia ],
                            startTime: h.horaInicio,
                            endTime: h.horaFin
                        };
                    });
                
                // --- 3. CREAMOS EL CALENDARIO ---
                const calendar = new FullCalendar.Calendar(calendarEl, {
                    headerToolbar: {
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,timeGridWeek,timeGridDay'
                    },
                    initialView: 'timeGridWeek',
                    locale: 'es',
                    selectable: true,
                    
                    // --- 4. APLICAMOS LAS REGLAS (CAPA 1) ---
                    businessHours: businessHoursConfig,
                    slotDuration: '00:' + configAgenda.intervaloMinutos + ':00',
                    
                    // --- 5. PEDIMOS LOS EVENTOS (CAPAS 2, 3 y 4) ---
                    events: function(fetchInfo, successCallback, failureCallback) {
                        fetch(servletURL + '?action=fetchEvents')
                            .then(response => response.json())
                            .then(data => {
                                // 'data' es el Map: { bloqueos: [...], feriados: [...], turnos: [...] }

                                // Capa 2: Feriados (Amarillo)
                                const feriados = data.feriados.map(f => ({
                                    title: f.descripcion,
                                    start: f.fecha,
                                    allDay: true,
                                    display: 'background',
                                    color: '#ffc107',
                                    editable: false // No se puede editar
                                }));
                                
                                // Capa 3: Bloqueos del Técnico (Rojo)
                                // (Esta es la parte que no querías que se rompiera)
                                const bloqueos = data.bloqueos.map(b => ({
                                    id: b.id,
                                    title: b.motivo || 'Bloqueado',
                                    start: b.fechaHoraInicio,
                                    end: b.fechaHoraFin,
                                    className: 'evento-bloqueo' // <-- Estilo rojo
                                }));
                                
                                // ⬇️ --- ¡AQUÍ ESTÁ EL CÓDIGO NUEVO! --- ⬇️
                                // Capa 4: Turnos Reservados (Azul)
                                const turnos = data.turnos.map(t => ({
                                    id: t.id,
                                    title: t.titulo, // Ej: "Reservado"
                                    start: t.fechaHoraInicio,
                                    end: t.fechaHoraFin,
                                    className: 'evento-reservado', // <-- Estilo azul
                                    editable: false, // No se puede mover
                                    overlap: false   // No deja superponer
                                }));
                                // ⬆️ --- FIN DEL CÓDIGO NUEVO --- ⬆️

                                // ⬇️ --- LÍNEA MODIFICADA --- ⬇️
                                // Combinamos las TRES listas de eventos
                                successCallback(feriados.concat(bloqueos).concat(turnos));
                            })
                            .catch(e => failureCallback(e));
                    },

                    // --- 6. ACCIONES (CREAR/BORRAR) ---
                    
                    // CREAR BLOQUEO (Rojo)
                    select: function(selectionInfo) {
                        if (!selectionInfo.resource && !calendar.view.type.startsWith('timeGrid')) {
                            return;
                        }
                        const motivo = prompt('Motivo del bloqueo (opcional):');
                        if (motivo !== null) {
                            const data = {
                                action: 'crearBloqueo',
                                inicio: selectionInfo.startStr,
                                fin: selectionInfo.endStr,
                                motivo: motivo
                            };
                            fetch(servletURL, {
                                method: 'POST',
                                headers: {'Content-Type': 'application/json'},
                                body: JSON.stringify(data)
                            })
                            .then(response => response.json())
                            .then(result => {
                                if (result.status === 'success') calendar.refetchEvents();
                                else alert('Error al crear bloqueo: ' + result.message);
                            });
                        }
                        calendar.unselect();
                    },
                    
                    // BORRAR BLOQUEO (Rojo)
                    eventClick: function(clickInfo) {
                        // Solo permite borrar si es un evento rojo
                        if (clickInfo.event.classNames.includes('evento-bloqueo')) {
                            if (confirm('¿Desea eliminar este bloqueo?')) {
                                const data = { action: 'borrarBloqueo', id: clickInfo.event.id };
                                fetch(servletURL, {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/json'},
                                    body: JSON.stringify(data)
                                })
                                .then(response => response.json())
                                .then(result => {
                                    if (result.status === 'success') calendar.refetchEvents();
                                    else alert('Error al borrar bloqueo: ' + result.message);
                                });
                            }
                        }
                        // Si es azul (reservado) o amarillo (feriado), no hace nada.
                    }
                }); // Fin de new FullCalendar.Calendar

                // 7. Renderizar el calendario
                calendar.render();

            }) // Fin del .then() principal (fetchRules)
            .catch(error => {
                console.error('Error fatal al cargar las reglas de la agenda:', error);
                alert('No se pudieron cargar las reglas de la agenda. Contacte al administrador.');
            });
    });
</script>
</body>
</html>