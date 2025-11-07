<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- 1. Seguridad: Verificar Sesión y Rol de Administrador --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Administrador'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Panel Administrador - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .sidebar { background-color: #dc3545; min-height: 100vh; } /* Rojo para Admin */
        .sidebar .nav-link { color: #f8f9fa; padding: 1rem; }
        .sidebar .nav-link:hover { background-color: #c82333; }
        .stat-card { text-align: center; }
        .stat-card .card-body { font-size: 1.5rem; font-weight: bold; }
    </style>
</head>
<body>

    <%-- 2. Barra de Navegación (Modular - Podrías crear includes/navbarAdmin.jsp) --%>
    <%@ include file="../includes/navbarAdmin.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-dark"><i class="fas fa-user-shield me-3 text-danger"></i> Panel de Administración</h1>
        <p class="text-muted">Gestión centralizada de usuarios, servicios, asignaciones y reportes del sistema.</p>
        <hr>

        <%-- Mensajes (ej: éxito al crear usuario) --%>
        <c:if test="${not empty param.exito}">
             <div class="alert alert-success">${param.exito}</div>
        </c:if>
        <c:if test="${not empty param.error}">
             <div class="alert alert-danger">${param.error}</div>
        </c:if>

        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card stat-card bg-info text-white shadow-sm">
                    <div class="card-body">XX</div>
                    <div class="card-footer">Usuarios Totales</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card stat-card bg-primary text-white shadow-sm">
                    <div class="card-body">YY</div>
                    <div class="card-footer">Solicitudes Pendientes</div>
                </div>
            </div>
             <div class="col-md-3">
                <div class="card stat-card bg-warning text-dark shadow-sm">
                    <div class="card-body">ZZ</div>
                    <div class="card-footer">Consultas Pendientes</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card stat-card bg-success text-white shadow-sm">
                    <div class="card-body">AA</div>
                    <div class="card-footer">Servicios Activos</div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-4 mb-3">
                <div class="card h-100 shadow">
                    <div class="card-body text-center">
                        <i class="fas fa-users-cog fa-3x text-info mb-3"></i>
                        <h5 class="card-title">Gestionar Usuarios</h5>
                        <p class="card-text small text-muted">ABMC de Clientes, Técnicos y Administradores.</p>
                        <a href="AdminUsuariosServlet?rol=tecnico" class="btn btn-info text-white">Administrar Usuarios</a>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-3">
                 <div class="card h-100 shadow">
                    <div class="card-body text-center">
                        <i class="fas fa-cogs fa-3x text-success mb-3"></i>
                        <h5 class="card-title">Gestionar Servicios</h5>
                        <p class="card-text small text-muted">ABMC del catálogo de servicios ofrecidos.</p>
                        <a href="AdminServiciosServlet" class="btn btn-success">Administrar Servicios</a>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-3">
                 <div class="card h-100 shadow">
                    <div class="card-body text-center">
                         <i class="fas fa-clipboard-list fa-3x text-primary mb-3"></i>
                        <h5 class="card-title">Asignar Solicitudes</h5>
                        <p class="card-text small text-muted">Asignar solicitudes nuevas a los técnicos disponibles.</p>
                        <a href="AsignacionServlet" class="btn btn-primary">Ver Pendientes</a>
                    </div>
                </div>
            </div>
             <div class="col-lg-4 mb-3">
                 <div class="card h-100 shadow">
                    <div class="card-body text-center">
                         <i class="fas fa-calendar-plus fa-3x text-secondary mb-3"></i>
                        <h5 class="card-title">Gestionar Turnos Base</h5>
                        <p class="card-text small text-muted">Crear o administrar los horarios base disponibles.</p>
                        <a href="ConfigAgendaServlet" class="btn btn-secondary">Administrar Turnos</a>
                    </div>
                </div>
            </div>
             

    </div> <%-- Fin container --%>

    <footer class="bg-dark text-white text-center py-3 mt-5">
        <p class="mb-0 small">&copy; 2025 QuickFix - Panel de Administración</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>