<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Seguridad --%>
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
        .table thead th { background-color: #0d6efd; color: white; }
        /* Estilo para <pre> en modal */
        .modal-diagnostico {
            white-space: pre-wrap;
            word-wrap: break-word;
            background-color: #e9ecef;
            border: 1px solid #ced4da;
            padding: 0.5rem 0.75rem;
            border-radius: 0.25rem;
            min-height: 100px;
        }
    </style>
</head>
<body>

    <%@ include file="../includes/navbarTecnico.jsp" %>

    <div class="container mt-4">
        <h1 class="mb-4 text-dark"><i class="fas fa-tasks me-3 text-primary"></i> Mis Solicitudes Asignadas</h1>
        <hr>

        <%-- Mensajes --%>
        <c:if test="${param.error == 'carga_solicitudes'}"> <div class="alert alert-danger">Error al cargar las solicitudes.</div> </c:if>
        <c:if test="${not empty param.exito}"> <div class="alert alert-success alert-dismissible fade show">${param.exito} <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div> </c:if>
        <c:if test="${not empty param.error}"> <div class="alert alert-danger alert-dismissible fade show">${param.error} <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div> </c:if>

        <div class="card shadow-sm">
            <div class="card-body table-responsive">
                <c:choose>
                    <c:when test="${empty listaSolicitudesAsignadas}">
                        <div class="alert alert-info text-center">No tienes solicitudes activas asignadas.</div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-hover table-bordered align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Fecha Creación</th>
                                    <th>Fecha Turno</th> <%-- ✅ Columna Nueva --%>
                                    <th>Cliente</th>
                                    <th>Servicio</th>
                                    <th>Prioridad</th>
                                    <th>Estado</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="sol" items="${listaSolicitudesAsignadas}">
                                    <tr>
                                        <td>${sol.idSolicitud}</td>
                                        <td>${String.format('%02d/%02d', sol.fechaHoraCreacion.dayOfMonth, sol.fechaHoraCreacion.monthValue)} ${String.format('%02d:%02d', sol.fechaHoraCreacion.hour, sol.fechaHoraCreacion.minute)}</td>
                                        <td>
                                            <%-- ✅ Fecha Turno Agregada --%>
                                            ${String.format('%02d/%02d', sol.turno.fechaHoraInicio.dayOfMonth, sol.turno.fechaHoraInicio.monthValue)} ${String.format('%02d:%02d', sol.turno.fechaHoraInicio.hour, sol.turno.fechaHoraInicio.minute)}
                                        </td>
                                        <td>${sol.cliente.nombre} ${sol.cliente.apellido}</td>
                                        <td>${sol.servicio.nombre}</td>
                                        <td><span class="badge ${sol.prioridad == 'URGENTE' ? 'bg-danger' : sol.prioridad == 'ALTA' ? 'bg-warning text-dark' : 'bg-secondary'}">${sol.prioridad}</span></td>
                                        <td><span class="badge ${sol.estado == 'RECIBIDA' || sol.estado == 'EN_DIAGNOSTICO' ? 'bg-warning text-dark' : sol.estado == 'APROBADA' || sol.estado == 'EN_REPARACION' ? 'bg-info text-dark' : sol.estado == 'LISTA_PARA_RETIRO' ? 'bg-primary' : 'bg-secondary'}">${sol.estado}</span></td>
                                        <td class="text-center">
                                            
                                            <%-- ✅ BOTÓN VER DETALLE (Abre Modal 1) --%>
                                            <button type="button" class="btn btn-sm btn-outline-primary"
                                                    onclick="abrirModalDetalle(
                                                        '${sol.idSolicitud}',
                                                        '${sol.cliente.nombre} ${sol.cliente.apellido}',
                                                        '${sol.cliente.telefono}',
                                                        '${sol.equipoCliente.marca} ${sol.equipoCliente.modelo}',
                                                        '${fn:escapeXml(sol.diagnostico)}',
                                                        '${fn:escapeXml(sol.diagnosticoTecnico)}'
                                                    )">
                                                <i class="fas fa-eye"></i> Ver
                                            </button>
                                            
                                            <%-- ✅ BOTÓN GESTIONAR (Abre Modal 2) --%>
                                            <button type="button" class="btn btn-sm btn-primary"
                                                    onclick="abrirModalGestionar(${sol.idSolicitud}, '${sol.estado}')">
                                                <i class="fas fa-edit"></i> Gestionar
                                            </button>
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

    <%-- ========================================================= --%>
    <%-- MODAL 1: VER DETALLES --%>
    <%-- ========================================================= --%>
    <div class="modal fade" id="modalVerDetalle" tabindex="-1" aria-labelledby="modalDetalleLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalDetalleLabel">Detalles de Solicitud ID: <span id="detalleId"></span></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
             <h6>Cliente: <span id="detalleCliente" class="text-muted fw-normal"></span></h6>
             <h6>Teléfono: <span id="detalleTelefono" class="text-muted fw-normal"></span></h6>
             <h6>Equipo: <span id="detalleEquipo" class="text-muted fw-normal"></span></h6>
             <hr>
             <label class="fw-bold">Diagnóstico del Cliente:</label>
             <pre id="detalleDiagnosticoCliente" class="modal-diagnostico"></pre>
             <label class="fw-bold mt-2">Diagnóstico del Técnico (si existe):</label>
             <pre id="detalleDiagnosticoTecnico" class="modal-diagnostico"></pre>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
          </div>
        </div>
      </div>
    </div>
    
    <%-- ========================================================= --%>
    <%-- MODAL 2: GESTIONAR / CAMBIAR ESTADO --%>
    <%-- ========================================================= --%>
    <div class="modal fade" id="modalGestionarEstado" tabindex="-1" aria-labelledby="modalGestionarLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="modalGestionarLabel">Gestionar Solicitud</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="formGestionar" action="SolicitudesTecnicoServlet" method="POST">
             <div class="modal-body">
                <input type="hidden" name="idSolicitud" id="gestionarIdSolicitud">
                
                <p>Solicitud ID: <strong id="gestionarIdLabel"></strong></p>
                <p>Estado Actual: <strong id="gestionarEstadoActual"></strong></p>
                
                <hr>
                
                <div class="mb-3">
                    <label for="nuevoEstado" class="form-label fw-bold">Seleccionar Nuevo Estado:</label>
                    <select class="form-select" name="nuevoEstado" id="nuevoEstado" required>
                        <%-- Lista de estados que el técnico puede asignar --%>
                        <option value="EN_DIAGNOSTICO">En Diagnóstico</option>
                        <option value="ESPERANDO_APROBACION">Esperando Aprobación (Presupuesto)</option>
                        <option value="APROBADA">Aprobada por Cliente</option>
                        <option value="EN_REPARACION">En Reparación</option>
                        <option value="LISTA_PARA_RETIRO">Lista para Retiro</option>
                        <option value="FINALIZADA">Finalizada (Completada)</option>
                        <option value="CANCELADA">Cancelada (por Técnico/Cliente)</option>
                    </select>
                </div>
                
                <div class="mb-3">
                    <label for="diagnosticoTecnico" class="form-label fw-bold">Añadir/Actualizar Diagnóstico Técnico:</label>
                    <textarea class="form-control" name="diagnosticoTecnico" rows="4" placeholder="Escribe tu diagnóstico, presupuesto o notas internas aquí..."></textarea>
                </div>
             </div>
             <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-primary">Actualizar Estado</button>
             </div>
          </form>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <%-- JavaScript para los Modales --%>
    <script>
        var modalDetalle = new bootstrap.Modal(document.getElementById('modalVerDetalle'));
        var modalGestionar = new bootstrap.Modal(document.getElementById('modalGestionarEstado'));

        // Función para el Modal "Ver Detalle"
        function abrirModalDetalle(id, cliente, telefono, equipo, diagCliente, diagTecnico) {
            document.getElementById('detalleId').innerText = id;
            document.getElementById('detalleCliente').innerText = cliente;
            document.getElementById('detalleTelefono').innerText = telefono;
            document.getElementById('detalleEquipo').innerText = equipo;
            document.getElementById('detalleDiagnosticoCliente').innerText = diagCliente;
            document.getElementById('detalleDiagnosticoTecnico').innerText = (diagTecnico && diagTecnico !== 'null') ? diagTecnico : 'Aún no hay diagnóstico técnico.';
            
            modalDetalle.show();
        }

        // Función para el Modal "Gestionar"
        function abrirModalGestionar(id, estadoActual) {
            document.getElementById('gestionarIdSolicitud').value = id;
            document.getElementById('gestionarIdLabel').innerText = id;
            document.getElementById('gestionarEstadoActual').innerText = estadoActual;
            
            // Opcional: seleccionar el estado actual en el dropdown
            document.getElementById('nuevoEstado').value = estadoActual; 
            
            modalGestionar.show();
        }
    </script>
</body>
</html>