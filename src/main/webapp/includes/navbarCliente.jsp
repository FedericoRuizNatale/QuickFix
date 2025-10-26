<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- 
    Nota: Se utiliza 'c:if' para verificar la sesión, aunque esta barra será incluida
    en páginas que ya deberían tener la sesión validada.
--%>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top shadow">
    <div class="container">
        
        <a class="navbar-brand fw-bold" href="homeCliente.jsp">QuickFix - Cliente</a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarClienteMenu" aria-controls="navbarClienteMenu" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarClienteMenu">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                
                <li class="nav-item">
                    <a class="nav-link" href="miEquipo.jsp">Mis Equipos</a> 
                </li>
                
                <li class="nav-item">
                    <a class="nav-link" href="solicitar_servicio.jsp">Solicitar Servicio</a> 
                </li>
                
                <li class="nav-item">
                    <a class="nav-link" href="consultaTecnica.jsp">Consulta Técnica</a> 
                </li>
                
                <li class="nav-item">
                    <a class="nav-link" href="#">Historial</a>
                </li>
            </ul>
        </div>
        
        <div class="d-flex align-items-center">
            
            <%-- Verifica si el usuario está logueado para mostrar su nombre --%>
            <c:if test="${not empty sessionScope.usuarioLogueado}">
                <p class="navbar-text me-3 mb-0 text-white-50">Hola, ${sessionScope.usuarioLogueado.nombre}</p>
                
                <a class="btn btn-danger" href="../LogoutServlet">
                    <i class="fas fa-sign-out-alt me-1"></i> Salir
                </a>
            </c:if>
            
        </div>
    </div>
</nav>