<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %> <%-- ✅ DIRECTIVA AGREGADA --%>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top shadow">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/tecnico/homeTecnico.jsp">QuickFix - Técnico</a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTecnicoMenu" aria-controls="navbarTecnicoMenu" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarTecnicoMenu">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <%-- ✅ CORRECCIÓN: Usar fn:endsWith --%>
                    <a class="nav-link <c:if test='${fn:endsWith(pageContext.request.requestURI, "homeTecnico.jsp")}'>active</c:if>" 
                       href="${pageContext.request.contextPath}/tecnico/homeTecnico.jsp">Inicio</a>
                </li>
                
                <li class="nav-item">
                     <%-- ✅ CORRECCIÓN: Usar fn:contains --%>
                    <a class="nav-link <c:if test='${fn:contains(pageContext.request.requestURI, "SolicitudesTecnicoServlet")}'>active</c:if>" 
                       href="${pageContext.request.contextPath}/tecnico/SolicitudesTecnicoServlet">Mis Solicitudes</a>
                </li>
                
                <li class="nav-item">
                     <%-- ✅ CORRECCIÓN: Usar fn:contains --%>
                    <a class="nav-link <c:if test='${fn:contains(pageContext.request.requestURI, "ConsultasTecnicoServlet")}'>active</c:if>" 
                       href="${pageContext.request.contextPath}/tecnico/ConsultasTecnicoServlet">Mis Consultas</a>
                </li>
                
                <li class="nav-item">
                     <%-- ✅ CORRECCIÓN: Usar fn:contains --%>
                    <a class="nav-link <c:if test='${fn:contains(pageContext.request.requestURI, "GestionTurnosServlet")}'>active</c:if>" 
                       href="${pageContext.request.contextPath}/tecnico/GestionTurnosServlet">Gestionar Disponibilidad</a>
                </li>
            </ul>
        </div>
        
        <div class="d-flex align-items-center">
            <c:if test="${not empty sessionScope.usuarioLogueado}">
                <p class="navbar-text me-3 mb-0 text-white-50">Hola, ${sessionScope.usuarioLogueado.nombre}</p>
                <a class="btn btn-danger btn-sm" href="${pageContext.request.contextPath}/LogoutServlet">
                    <i class="fas fa-sign-out-alt me-1"></i> Salir
                </a>
            </c:if>
        </div>
    </div>
</nav>