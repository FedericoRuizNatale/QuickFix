<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- Seguridad --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Cliente'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Historial de Solicitudes - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .table thead th { background-color: #6c757d; color: white; }
    </style>
</head>
<body>

    <%@ include file="../includes/navbarCliente.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-secondary"><i class="fas fa-history me-3"></i> Historial de Solicitudes</h1>
        <hr>

        <c:if test="${param.error == 'historial'}">
            <div class="alert alert-danger">Error al cargar el historial.</div>
        </c:if>

        <c:choose>
            <c:when test="${empty historialSolicitudes}">
                <div class="alert alert-info text-center mt-4" role="alert">
                    <i class="fas fa-info-circle me-2"></i> Aún no has realizado ninguna solicitud.
                </div>
            </c:when>
            <c:otherwise>
                <div class="card shadow-sm">
                    <div class="card-body">
                        <table class="table table-hover table-striped align-middle">
                            <thead>
                                <tr>
                                    <%-- ✅ COLUMNA ID OCULTA --%>
                                    <%-- <th>ID Solicitud</th> --%>
                                    <th>Fecha Creación</th>
                                    <th>Equipo</th>
                                    <%-- ✅ COLUMNA SERVICIO ELIMINADA --%>
                                    <%-- <th>Servicio Solicitado</th> --%>
                                    <th>Estado Actual</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="solicitud" items="${historialSolicitudes}">
                                    <tr>
                                        <%-- ID Oculto --%>
                                        <%-- <td>${solicitud.idSolicitud}</td> --%>
                                        <td>
                                            ${String.format('%02d', solicitud.fechaHoraCreacion.dayOfMonth)}/${String.format('%02d', solicitud.fechaHoraCreacion.monthValue)}/${solicitud.fechaHoraCreacion.year}
                                            ${String.format('%02d', solicitud.fechaHoraCreacion.hour)}:${String.format('%02d', solicitud.fechaHoraCreacion.minute)}
                                        </td>
                                        <td>
                                            <c:if test="${not empty solicitud.equipoCliente}">
                                                ${solicitud.equipoCliente.marca} ${solicitud.equipoCliente.modelo}
                                            </c:if>
                                            <c:if test="${empty solicitud.equipoCliente}"> N/A </c:if>
                                        </td>
                                        <%-- Servicio Eliminado --%>
                                        <%-- <td>${solicitud.servicio.nombre}</td> --%>
                                        <td>
                                            <span class="badge ${solicitud.estado == 'RECIBIDA' || solicitud.estado == 'EN_DIAGNOSTICO' ? 'bg-warning text-dark' : solicitud.estado == 'APROBADA' || solicitud.estado == 'EN_REPARACION' ? 'bg-info text-dark' : solicitud.estado == 'LISTA_PARA_RETIRO' ? 'bg-primary' : solicitud.estado == 'FINALIZADA' ? 'bg-success' : solicitud.estado == 'CANCELADA' ? 'bg-danger' : 'bg-secondary'}">
                                                ${solicitud.estado}
                                            </span>
                                        </td>
                                        <td>
                                            <%-- ✅ BOTÓN VER (Llama a JavaScript para abrir el Modal) --%>
                                            <button type="button" class="btn btn-sm btn-outline-primary"
                                                    onclick="mostrarDetallesServicio(
                                                        '${solicitud.idSolicitud}',
                                                        '${solicitud.servicio.nombre}',
                                                        '${solicitud.servicio.descripcion}',
                                                        '${solicitud.servicio.costoBase}',
                                                        '${solicitud.servicio.tiempoEstimado}'
                                                    )">
                                                <i class="fas fa-eye"></i> Ver Solicitud
                                            </button>

                                            <%-- Botón Cancelar (si aplica) --%>
                                            <c:if test='${solicitud.estado == "RECIBIDA" || solicitud.estado == "APROBADA"}'>
                                                <form action="CancelarSolicitudServlet" method="POST" style="display:inline;" onsubmit="return confirm('¿Seguro que quieres cancelar esta solicitud?');">
                                                     <input type="hidden" name="idSolicitud" value="${solicitud.idSolicitud}">
                                                     <button type="submit" class="btn btn-sm btn-outline-danger ms-1">
                                                         <i class="fas fa-times"></i> Cancelar
                                                     </button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

    </div> <%-- Fin container --%>

    <%-- ========================================================= --%>
    <%-- ✅ MODAL PARA MOSTRAR DETALLES DEL SERVICIO --%>
    <%-- ========================================================= --%>
    <div class="modal fade" id="modalDetallesServicio" tabindex="-1" aria-labelledby="modalDetallesLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="modalDetallesLabel">Detalles del Servicio Solicitado</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
             <h6 class="fw-bold">Solicitud ID: <span id="modalSolicitudId" class="text-muted"></span></h6>
             <hr>
             <h5 id="modalServicioNombre" class="text-primary mb-3"></h5>
             <p><strong>Descripción:</strong></p>
             <p id="modalServicioDescripcion" class="text-muted"></p>
             <p><strong>Costo Base Estimado:</strong> $<span id="modalServicioCosto"></span></p>
             <p><strong>Tiempo Estimado:</strong> <span id="modalServicioTiempo"></span> horas</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

    <%-- ========================================================= --%>
    <%-- ✅ JAVASCRIPT PARA EL MODAL DE DETALLES --%>
    <%-- ========================================================= --%>
    <script>
        // Obtener la instancia del modal
        var detallesModal = new bootstrap.Modal(document.getElementById('modalDetallesServicio'));

        // Obtener los elementos dentro del modal donde mostraremos la info
        var modalSolicitudId = document.getElementById('modalSolicitudId');
        var modalServicioNombre = document.getElementById('modalServicioNombre');
        var modalServicioDescripcion = document.getElementById('modalServicioDescripcion');
        var modalServicioCosto = document.getElementById('modalServicioCosto');
        var modalServicioTiempo = document.getElementById('modalServicioTiempo');

        // Función que se llama al hacer clic en "Ver Servicio"
        function mostrarDetallesServicio(idSol, nombre, descripcion, costo, tiempo) {
            // Llenar el contenido del modal con los datos pasados
            modalSolicitudId.textContent = idSol;
            modalServicioNombre.textContent = nombre;
            modalServicioDescripcion.textContent = descripcion || 'No disponible'; // Muestra N/A si es null
            modalServicioCosto.textContent = parseFloat(costo).toFixed(2); // Formatea el costo a 2 decimales
            modalServicioTiempo.textContent = tiempo || '?'; // Muestra ? si es null

            // Mostrar el modal
            detallesModal.show();
        }
    </script>
</body>
</html>