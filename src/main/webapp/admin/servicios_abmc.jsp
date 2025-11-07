<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Seguridad --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Administrador'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ABMC Servicios - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style> body { padding-top: 56px; background-color: #f8f9fa; } </style>
</head>
<body>

    <%@ include file="../includes/navbarAdmin.jsp" %>

    <div class="container mt-4">
        
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h3"><i class="fas fa-cogs me-3 text-success"></i> Gestión de Catálogo de Servicios</h1>
            <button type="button" class="btn btn-primary" onclick="abrirModalCreacion()">
                <i class="fas fa-plus me-2"></i> Crear Nuevo Servicio
            </button>
        </div>
        <hr>

        <%-- Mensajes de Éxito/Error --%>
        <c:if test="${param.exito == 'creado'}"> <div class="alert alert-success">¡Servicio creado!</div> </c:if>
        <c:if test="${param.exito == 'editado'}"> <div class="alert alert-success">¡Servicio actualizado!</div> </c:if>
        <c:if test="${param.exito == 'eliminado'}"> <div class="alert alert-success">¡Servicio eliminado!</div> </c:if>
        <c:if test="${not empty param.error}"> <div class="alert alert-danger">${param.error}</div> </c:if>

        <%-- Tabla de Servicios --%>
        <div class="card shadow-sm">
            <div class="card-body table-responsive">
                <table class="table table-hover table-striped align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Costo Base</th>
                            <th>Tiempo Est. (hs)</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="servicio" items="${listaServicios}">
                            <tr>
                                <td>${servicio.idServicio}</td>
                                <td>${servicio.nombre}</td>
                                <td>${fn:substring(servicio.descripcion, 0, 100)}...</td>
                                <td>$${servicio.costoBase}</td>
                                <td>${servicio.tiempoEstimado}</td>
                                <td class="text-center">
                                    <button type="button" class="btn btn-sm btn-warning me-1" 
                                            onclick="abrirModalEdicion(
                                                ${servicio.idServicio}, '${fn:escapeXml(servicio.nombre)}', '${fn:escapeXml(servicio.descripcion)}',
                                                ${servicio.costoBase}, ${servicio.tiempoEstimado}
                                            )">
                                        <i class="fas fa-edit"></i> Editar
                                    </button>
                                    <form action="AdminServiciosServlet" method="POST" style="display:inline;" onsubmit="return confirm('¿Seguro? Si el servicio está en uso no podrá eliminarse.');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="idServicio" value="${servicio.idServicio}">
                                        <button type="submit" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i> Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div> <%-- Fin container --%>

    <%-- MODAL (Sirve para Crear y Editar Servicios) --%>
    <div class="modal fade" id="modalServicio" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="modalLabel">Crear Nuevo Servicio</h5> 
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="formServicio" action="AdminServiciosServlet" method="POST"> 
             <div class="modal-body">
                <input type="hidden" name="action" id="formAction" value="create">
                <input type="hidden" name="idServicio" id="formIdServicio" value="0"> 
             
                <div class="mb-3">
                    <label for="nombre" class="form-label">Nombre del Servicio:</label>
                    <input type="text" class="form-control" id="nombre" name="nombre" required>
                </div>
                <div class="mb-3">
                    <label for="descripcion" class="form-label">Descripción:</label>
                    <textarea class="form-control" id="descripcion" name="descripcion" rows="3" required></textarea>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="costoBase" class="form-label">Costo Base ($):</label>
                        <input type="number" step="0.01" class="form-control" id="costoBase" name="costoBase" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="tiempoEstimado" class="form-label">Tiempo Estimado (en horas):</label>
                        <input type="number" class="form-control" id="tiempoEstimado" name="tiempoEstimado" required>
                    </div>
                </div>
             </div>
             <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-primary" id="btnGuardar">Guardar</button> 
             </div>
          </form>
        </div>
      </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        var modalServicio = new bootstrap.Modal(document.getElementById('modalServicio'));
        var modalLabel = document.getElementById('modalLabel');
        var btnGuardar = document.getElementById('btnGuardar');
        var formServicio = document.getElementById('formServicio');
        
        function abrirModalCreacion() {
            modalLabel.textContent = 'Crear Nuevo Servicio';
            btnGuardar.textContent = 'Guardar';
            btnGuardar.classList.remove('btn-warning');
            btnGuardar.classList.add('btn-primary');
            formServicio.reset(); 
            document.getElementById('formAction').value = 'create';
            document.getElementById('formIdServicio').value = '0';
            modalServicio.show();
        }

        function abrirModalEdicion(id, nombre, descripcion, costo, tiempo) {
            modalLabel.textContent = 'Editar Servicio (ID: ' + id + ')';
            btnGuardar.textContent = 'Guardar Cambios';
            btnGuardar.classList.remove('btn-primary');
            btnGuardar.classList.add('btn-warning');
            formServicio.reset();
            document.getElementById('formAction').value = 'update';
            document.getElementById('formIdServicio').value = id;
            
            document.getElementById('nombre').value = nombre;
            document.getElementById('descripcion').value = descripcion;
            document.getElementById('costoBase').value = costo;
            document.getElementById('tiempoEstimado').value = tiempo;

            modalServicio.show();
        }
    </script>
</body>
</html>