<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.quickfix.logic.ControladorLogica" %>
<%@ page import="com.quickfix.entities.Cliente" %>
<%@ page import="com.quickfix.entities.EquipoCliente" %> <%-- Necesario para la lista --%>
<%@ page import="java.util.List" %> <%-- Necesario para la lista --%>

<%
    // ====================================================================
    // LÓGICA DE SEGURIDAD (CRUCIAL)
    // ====================================================================

    Cliente clienteLogueado = (Cliente) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rolUsuario");

    if (clienteLogueado == null || !"Cliente".equals(rolUsuario)) {
        // La redirección se hará con c:redirect.
    }

    // ====================================================================
    // LÓGICA DE VALIDACIÓN DE EQUIPO
    // ====================================================================

    ControladorLogica controlLogica = ControladorLogica.getInstance();

    boolean tieneEquipos = false;
    if (clienteLogueado != null) {
        tieneEquipos = controlLogica.clienteTieneEquipos(clienteLogueado);
    }

    request.setAttribute("tieneEquipos", tieneEquipos);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mi Panel - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        body { padding-top: 56px; }
        .masthead { background-color: #198754; color: white; padding: 3rem 0; margin-bottom: 2rem;}
    </style>
</head>
<body>

    <c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Cliente'}">
        <c:redirect url="../login.jsp"/>
    </c:if>

    <%@ include file="../includes/navbarCliente.jsp" %>

    <header class="masthead text-center">
        <div class="container">
            <h1 class="display-5 fw-bold">Bienvenido, ${sessionScope.usuarioLogueado.nombre}</h1>
            <p class="lead mb-0">Ya puedes gestionar tus equipos y solicitar soporte.</p>
        </div>
    </header>

    <%-- ✅ CORRECCIÓN: El valor del parámetro debe coincidir con el del Servlet --%>
    <c:if test="${param.exito == 'solicitud_ok'}">
        <div class="container mt-3">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <strong>¡Turno Agendado Correctamente!</strong> Tu solicitud de servicio ha sido enviada.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
    </c:if>

    <div class="container py-3">

        <div class="row mb-5">
            <div class="col-12">
                <h2>Acciones de Servicio</h2>
                <hr>

                <c:if test="${tieneEquipos}">
                    <p class="text-success fw-bold">✔️ Tienes equipos cargados. Puedes solicitar un servicio.</p>

                    <a href="PreSolicitudServlet" class="btn btn-primary btn-lg me-3">
                        <i class="fas fa-hand-holding-box me-2"></i> Solicitar Nuevo Servicio
                    </a>

                    <a href="PreConsultaServlet" class="btn btn-success btn-lg">
                        <i class="fas fa-comments me-2"></i> Realizar Consulta Técnica
                    </a>
                </c:if>

                <c:if test="${!tieneEquipos}">
                    <div class="alert alert-warning mt-4 p-4 text-center">
                        <h4 class="alert-heading">⚠️ ¡Equipo Requerido!</h4>
                        <p class="mb-3">Debes registrar **al menos un equipo** (PC o Laptop) antes de solicitar un servicio o hacer una consulta técnica.</p>
                        <a href="miEquipo.jsp" class="btn btn-lg btn-warning fw-bold">
                            <i class="fas fa-desktop me-2"></i> Ir a Cargar Datos de mi Equipo Ahora
                        </a>
                    </div>
                </c:if>
            </div>
        </div>

        <div class="row">
            <div class="col-12 text-center">
                <a href="miEquipo.jsp" class="btn btn-link">Ver/Administrar Mis Equipos</a>
            </div>
        </div>
    </div>

    <footer class="bg-dark text-white text-center py-3 mt-auto">
        <div class="container">
            <p class="mb-0 small">&copy; 2025 QuickFix</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>