<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Seguridad --%>
<c:if test="${empty sessionScope.usuarioLogueado || sessionScope.rolUsuario != 'Cliente'}">
    <c:redirect url="../login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Realizar Consulta Técnica - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style> body { padding-top: 56px; background-color: #f8f9fa; } </style>
</head>
<body>

    <%@ include file="../includes/navbarCliente.jsp" %>

    <div class="container mt-4">
        
        <h1 class="mb-4 text-success"><i class="fas fa-comments me-3"></i> Realizar Consulta Técnica</h1>
        <p class="lead text-muted">Describe tu problema o duda técnica. Un técnico responderá a la brevedad.</p>
        <hr>

        <%-- Mensajes de Éxito o Error --%>
        <c:if test="${param.exito == 'true'}">
            <div class="alert alert-success mt-3">✅ ¡Consulta enviada con éxito! Recibirás una notificación cuando sea respondida.</div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger mt-3">❌ Error al enviar la consulta. Intenta de nuevo.</div>
        </c:if>

        <div class="row justify-content-center">
            <div class="col-lg-8">
                
                <div class="card shadow-lg p-4">
                    <form action="ConsultaServlet" method="POST"> <%-- Apunta al Servlet que procesará la consulta --%>
                        
                        <%-- 1. SELECCIÓN OPCIONAL DEL EQUIPO --%>
                        <div class="mb-4">
                            <label for="equipoSelect" class="form-label fw-bold"><i class="fas fa-desktop me-2"></i> Equipo Relacionado (Opcional):</label>
                            
                            <select class="form-select" id="equipoSelect" name="idEquipo">
                                <option value="">-- No especificar equipo --</option> <%-- Opción para enviar sin equipo --%>
                                
                                <c:if test="${empty listaEquipos}">
                                     <option value="" disabled>No tienes equipos registrados</option>
                                </c:if>
                                
                                <c:forEach var="equipo" items="${listaEquipos}">
                                    <option value="${equipo.idEquipo}">
                                        [${equipo.tipo}] ${equipo.marca} ${equipo.modelo}
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="form-text">Si tu consulta es sobre un equipo específico, selecciónalo aquí.</div>
                        </div>
                        
                        <%-- 2. DESCRIPCIÓN DEL PROBLEMA (Obligatorio) --%>
                        <div class="mb-4">
                            <label for="descripcionProblema" class="form-label fw-bold"><i class="fas fa-file-alt me-2"></i> Describe tu Consulta *</label>
                            <textarea class="form-control" id="descripcionProblema" name="descripcionProblema" rows="5" required placeholder="Detalla tu problema o pregunta. Incluye mensajes de error, cuándo empezó, etc."></textarea>
                        </div>
                        
                        <%-- Botón de Envío --%>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-success btn-lg">
                                <i class="fas fa-paper-plane me-2"></i> Enviar Consulta
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>