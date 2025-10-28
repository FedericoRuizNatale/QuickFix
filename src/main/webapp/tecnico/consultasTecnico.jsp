<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%-- Para formatear fechas --%>

<%-- Seguridad: Verifica sesión y rol de Técnico --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Tecnico'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Consultas Pendientes - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .table thead th { background-color: #198754; color: white; } /* Verde para consultas */
    </style>
</head>
<body>

    <%-- Incluir la Barra de Navegación del Técnico --%>
    <%@ include file="../includes/navbarTecnico.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-dark"><i class="fas fa-question-circle me-3 text-success"></i> Consultas Técnicas Pendientes</h1>
        <p class="text-muted">Revisa las consultas pendientes realizadas por los clientes y asigna una solución.</p>
        <hr>

        <%-- Mensajes de Éxito o Error (desde el Servlet) --%>
        <c:if test="${param.exito == 'consulta_respondida'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ¡Consulta respondida y asignada correctamente!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                Error al procesar la consulta: ${param.error}
                <%-- Mensaje específico si la solución estaba vacía --%>
                <c:if test="${param.error == 'solucion_vacia'}"> (Consulta ID: ${param.idConsultaError})</c:if>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <%-- Tabla de Consultas Pendientes --%>
        <div class="card shadow-sm">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty listaConsultasPendientes}">
                        <div class="alert alert-info text-center" role="alert">
                            <i class="fas fa-check-circle me-2"></i> ¡No hay consultas pendientes por responder en este momento!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-hover table-bordered align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Fecha</th>
                                    <th>Cliente</th>
                                    <th>Descripción del Problema</th>
                                    <th class="text-center">Acción</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="consulta" items="${listaConsultasPendientes}">
                                    <tr>
                                        <td>${consulta.idConsulta}</td>
                                        <td>
                                            <%-- Formato de fecha corregido para LocalDateTime --%>
                                            ${String.format('%02d', consulta.fechaHora.dayOfMonth)}/${String.format('%02d', consulta.fechaHora.monthValue)}/${consulta.fechaHora.year} ${String.format('%02d', consulta.fechaHora.hour)}:${String.format('%02d', consulta.fechaHora.minute)}
                                        </td>
                                        <td>${consulta.cliente.nombre} ${consulta.cliente.apellido}</td>
                                        <%-- Truncar descripción larga si es necesario --%>
                                        <td>
                                            <c:choose>
                                                <c:when test="${fn:length(consulta.descripcionProblema) > 100}">
                                                    ${fn:substring(consulta.descripcionProblema, 0, 100)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${consulta.descripcionProblema}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <%-- Botón para abrir el Modal de Respuesta --%>
                                            <button type="button" class="btn btn-success btn-sm"
                                                    onclick="abrirModalRespuesta(${consulta.idConsulta}, '${consulta.descripcionProblema}')">
                                                <i class="fas fa-reply me-1"></i> Responder
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
    <%-- MODAL PARA RESPONDER LA CONSULTA --%>
    <%-- ========================================================= --%>
    <div class="modal fade" id="modalResponderConsulta" tabindex="-1" aria-labelledby="modalResponderLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg"> <%-- Modal más grande para la descripción --%>
        <div class="modal-content">
          <div class="modal-header bg-success text-white">
            <h5 class="modal-title" id="modalResponderLabel">Responder Consulta Técnica</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="formResponderConsulta" action="ConsultasTecnicoServlet" method="POST">
             <div class="modal-body">
                <%-- Campo oculto para enviar el ID de la consulta --%>
                <input type="hidden" name="idConsulta" id="formIdConsulta" value="0">

                <%-- Mostrar la descripción del problema (solo lectura) --%>
                <div class="mb-3">
                    <label class="form-label fw-bold">Problema Descrito por el Cliente:</label>
                    <p id="modalDescripcionProblema" class="form-control bg-light" style="min-height: 100px;"></p>
                </div>

                <%-- Campo para escribir la solución (obligatorio) --%>
                <div class="mb-3">
                    <label for="solucionTextarea" class="form-label fw-bold">Tu Respuesta / Solución *</label>
                    <textarea class="form-control" id="solucionTextarea" name="solucion" rows="5" required placeholder="Escribe aquí la solución o los pasos a seguir..."></textarea>
                </div>
             </div>
             <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-success">Enviar Respuesta</button>
             </div>
          </form>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

    <%-- ========================================================= --%>
    <%-- JAVASCRIPT PARA EL MODAL DE RESPUESTA --%>
    <%-- ========================================================= --%>
    <script>
        // Obtener la instancia del modal y sus elementos
        var modalRespuesta = new bootstrap.Modal(document.getElementById('modalResponderConsulta'));
        var modalLabel = document.getElementById('modalResponderLabel');
        var formIdConsulta = document.getElementById('formIdConsulta');
        var modalDescripcionProblema = document.getElementById('modalDescripcionProblema');
        var solucionTextarea = document.getElementById('solucionTextarea');

        // Función que se llama al hacer clic en "Responder"
        function abrirModalRespuesta(idConsulta, descripcionProblema) {
            // Establecer el ID de la consulta en el campo oculto
            formIdConsulta.value = idConsulta;
            
            // Mostrar la descripción del problema en el modal
            // Usamos innerText para evitar problemas con caracteres especiales en la descripción
            modalDescripcionProblema.innerText = descripcionProblema; 
            
            // Limpiar el campo de solución por si tenía algo de antes
            solucionTextarea.value = ''; 
            
            // Actualizar el título (opcional)
            modalLabel.textContent = 'Responder Consulta ID: ' + idConsulta;
            
            // Abrir el modal
            modalRespuesta.show();
        }
        
        // Opcional: Limpiar el formulario al cerrar el modal
         var modalElement = document.getElementById('modalResponderConsulta');
         modalElement.addEventListener('hidden.bs.modal', function (event) {
             document.getElementById('formResponderConsulta').reset();
             modalDescripcionProblema.innerText = ''; // Limpiar también la descripción
             modalLabel.textContent = 'Responder Consulta Técnica'; // Resetear título
         });
    </script>
</body>
</html>