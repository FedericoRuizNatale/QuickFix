<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- 1. Seguridad: Verificar sesión y rol --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Tecnico'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Panel Técnico - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { padding-top: 56px; background-color: #f8f9fa; }
        .stat-card { text-align: center; }
        .stat-card .card-body { font-size: 1.5rem; font-weight: bold; }
    </style>
</head>
<body>

    <%-- 2. Barra de Navegación (Modular - Podrías crear includes/navbarTecnico.jsp) --%>
    <%@ include file="../includes/navbarTecnico.jsp" %>

    <div class="container mt-4">

        <h1 class="mb-4 text-dark"><i class="fas fa-user-cog me-3"></i> Panel del Técnico</h1>
        <hr>

        <%-- Mensajes (ej: éxito al actualizar estado) --%>
        <c:if test="${not empty param.exito}">
             <div class="alert alert-success">${param.exito}</div>
        </c:if>
        <c:if test="${not empty param.error}">
             <div class="alert alert-danger">${param.error}</div>
        </c:if>

        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card stat-card bg-primary text-white shadow-sm">
                    <div class="card-body">XX</div>
                    <div class="card-footer">Solicitudes Activas</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card stat-card bg-success text-white shadow-sm">
                    <div class="card-body">YY</div>
                    <div class="card-footer">Consultas Pendientes</div>
                </div>
            </div>
             <div class="col-md-4">
                <div class="card stat-card bg-warning text-dark shadow-sm">
                    <div class="card-body">ZZ</div>
                    <div class="card-footer">Turnos Disponibles Publicados</div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-4 mb-3">
                <div class="card h-100 shadow">
                    <div class="card-body text-center">
                        <i class="fas fa-tasks fa-3x text-primary mb-3"></i>
                        <h5 class="card-title">Gestionar Solicitudes</h5>
                        <p class="card-text small text-muted">Ver detalles, actualizar estado y añadir diagnóstico a las solicitudes de servicio asignadas.</p>
                        <a href="SolicitudesTecnicoServlet" class="btn btn-primary">Ver Mis Solicitudes</a>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-3">
                 <div class="card h-100 shadow">
                    <div class="card-body text-center">
                        <i class="fas fa-question-circle fa-3x text-success mb-3"></i>
                        <h5 class="card-title">Responder Consultas</h5>
                        <p class="card-text small text-muted">Revisar y responder las consultas técnicas asignadas por los clientes.</p>
                        <a href="ConsultasTecnicoServlet" class="btn btn-success">Ver Mis Consultas</a>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-3">
                 <div class="card h-100 shadow">
                    <div class="card-body text-center">
                         <i class="fas fa-calendar-alt fa-3x text-warning mb-3"></i>
                        <h5 class="card-title">Gestionar Disponibilidad</h5>
                        <p class="card-text small text-muted">Publicar u ocultar tus horarios disponibles para nuevas solicitudes.</p>
                        <a href="AgendaTecnicoServlet" class="btn btn-warning">Gestionar Turnos</a>
                    </div>
                </div>
            </div>
        </div>

    </div> <%-- Fin container --%>

    <footer class="bg-dark text-white text-center py-3 mt-5">
        <p class="mb-0 small">&copy; 2025 QuickFix</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>