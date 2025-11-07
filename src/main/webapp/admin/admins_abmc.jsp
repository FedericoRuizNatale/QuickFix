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
    <title>ABMC Admins - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style> body { padding-top: 56px; background-color: #f8f9fa; } </style>
</head>
<body>

    <%@ include file="../includes/navbarAdmin.jsp" %>

    <div class="container mt-4">
        
        <h1 class="mb-4 text-dark"><i class="fas fa-users-cog me-3 text-info"></i> Gestión de Usuarios</h1>
        <hr>

        <%-- Pestañas de Navegación --%>
        <ul class="nav nav-tabs mb-3">
            <li class="nav-item">
                <a class="nav-link" href="AdminUsuariosServlet?rol=cliente">Clientes</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="AdminUsuariosServlet?rol=tecnico">Técnicos</a>
            </li>
            <li class="nav-item">
                <a class="nav-link active" href="AdminUsuariosServlet?rol=administrador">Administradores</a>
            </li>
        </ul>

        <%-- Botón CREAR --%>
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="h4">Listado de Administradores</h3>
            <button type="button" class="btn btn-primary" onclick="abrirModalCreacion()">
                <i class="fas fa-plus me-2"></i> Crear Nuevo Admin
            </button>
        </div>
        
        <%-- Mensajes --%>
        <c:if test="${param.exito == 'creado'}"> <div class="alert alert-success">¡Admin creado!</div> </c:if>
        <c:if test="${param.exito == 'editado'}"> <div class="alert alert-success">¡Admin actualizado!</div> </c:if>
        <c:if test="${param.exito == 'eliminado'}"> <div class="alert alert-success">¡Admin eliminado!</div> </c:if>
        <c:if test="${not empty param.error}"> <div class="alert alert-danger">${param.error}</div> </c:if>

        <%-- Tabla de Admins --%>
        <div class="card shadow-sm">
            <div class="card-body">
                <table class="table table-hover table-striped align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th> <th>Nombre</th> <th>Email</th> <th>Nivel Acceso</th> <th>Estado</th> <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${listaUsuarios}">
                            <tr>
                                <td>${user.idUsuario}</td>
                                <td>${user.nombre} ${user.apellido}</td>
                                <td>${user.email}</td>
                                <td>${user.nivelAcceso}</td> <%-- Específico --%>
                                <td><span class="badge ${user.estado ? 'bg-success' : 'bg-danger'}">${user.estado ? 'Activo' : 'Inactivo'}</span></td>
                                <td class="text-center">
                                    <button type="button" class="btn btn-sm btn-warning me-1" 
                                            onclick="abrirModalEdicion(
                                                ${user.idUsuario}, '${user.nombre}', '${user.apellido}', '${user.email}', 
                                                '${user.telefono}', '${user.direccion}', ${user.estado}, ${user.nivelAcceso}
                                            )">
                                        <i class="fas fa-edit"></i> Editar
                                    </button>
                                    <form action="AdminUsuariosServlet" method="POST" style="display:inline;" onsubmit="return confirm('¿Seguro? Se eliminará el admin.');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="rol" value="administrador">
                                        <input type="hidden" name="idUsuario" value="${user.idUsuario}">
                                        <%-- Deshabilitar el botón si es el usuario actual --%>
                                        <button type="submit" class="btn btn-sm btn-danger" ${sessionScope.usuarioLogueado.idUsuario == user.idUsuario ? 'disabled' : ''}>
                                            <i class="fas fa-trash"></i> Eliminar
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div> <%-- Fin container --%>

    <%-- MODAL (Sirve para Crear y Editar Admins) --%>
    <div class="modal fade" id="modalUsuario" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="modalLabel">Crear Nuevo Admin</h5> 
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="formUsuario" action="AdminUsuariosServlet" method="POST"> 
             <div class="modal-body">
                <input type="hidden" name="action" id="formAction" value="create">
                <input type="hidden" name="rol" value="administrador"> 
                <input type="hidden" name="idUsuario" id="formIdUsuario" value="0"> 
             
                <div class="row">
                    <div class="col-md-6 mb-3"><label>Nombre:</label><input type="text" class="form-control" id="nombre" name="nombre" required></div>
                    <div class="col-md-6 mb-3"><label>Apellido:</label><input type="text" class="form-control" id="apellido" name="apellido" required></div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3"><label>Email:</label><input type="email" class="form-control" id="email" name="email" required></div>
                    <div class="col-md-6 mb-3"><label>Contraseña:</label><input type="password" class="form-control" id="password" name="password" placeholder="(Dejar en blanco para no cambiar)"></div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3"><label>Teléfono:</label><input type="text" class="form-control" id="telefono" name="telefono"></div>
                    <div class="col-md-6 mb-3"><label>Dirección:</label><input type="text" class="form-control" id="direccion" name="direccion"></div>
                </div>
                <hr>
                <%-- Campo Específico de Admin --%>
                <div class="row">
                    <div class="col-md-6 mb-3"><label>Nivel de Acceso (ej: 1):</label><input type="number" class="form-control" id="nivelAcceso" name="nivelAcceso" required></div>
                    <div class="col-md-6 mb-3 d-flex align-items-center">
                        <div class="form-check form-switch"><input class="form-check-input" type="checkbox" id="estado" name="estado" checked><label class="form-check-label ms-2" for="estado">Usuario Activo</label></div>
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
        var modalUsuario = new bootstrap.Modal(document.getElementById('modalUsuario'));
        var modalLabel = document.getElementById('modalLabel');
        var btnGuardar = document.getElementById('btnGuardar');
        var formUsuario = document.getElementById('formUsuario');
        
        function abrirModalCreacion() {
            modalLabel.textContent = 'Crear Nuevo Admin';
            btnGuardar.textContent = 'Guardar';
            btnGuardar.classList.remove('btn-warning');
            btnGuardar.classList.add('btn-primary');
            formUsuario.reset();
            document.getElementById('formAction').value = 'create';
            document.getElementById('formIdUsuario').value = '0';
            document.getElementById('estado').checked = true;
            document.getElementById('password').required = true;
            modalUsuario.show();
        }

        function abrirModalEdicion(id, nombre, apellido, email, telefono, direccion, estado, nivelAcceso) {
            modalLabel.textContent = 'Editar Admin (ID: ' + id + ')';
            btnGuardar.textContent = 'Guardar Cambios';
            btnGuardar.classList.remove('btn-primary');
            btnGuardar.classList.add('btn-warning');
            formUsuario.reset();
            document.getElementById('formAction').value = 'update';
            document.getElementById('formIdUsuario').value = id;
            document.getElementById('password').required = false; 
            
            // Llenar campos comunes
            document.getElementById('nombre').value = nombre;
            document.getElementById('apellido').value = apellido;
            document.getElementById('email').value = email;
            document.getElementById('telefono').value = telefono;
            document.getElementById('direccion').value = direccion;
            document.getElementById('estado').checked = estado;
            // Llenar campo específico
            document.getElementById('nivelAcceso').value = nivelAcceso;

            modalUsuario.show();
        }
    </script>
</body>
</html>