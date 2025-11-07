<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top shadow">
        <div class="container">
            <a class="navbar-brand fw-bold" href="homeAdmin.jsp">QuickFix - Admin</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarAdminMenu" aria-controls="navbarAdminMenu" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarAdminMenu">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link active" href="homeAdmin.jsp">Inicio</a></li>
                    <li class="nav-item"><a class="nav-link" href="AdminUsuariosServlet?rol=tecnico">Gestionar Usuarios</a></li>
                    <li class="nav-item"><a class="nav-link" href="AdminServiciosServlet">Gestionar Servicios</a></li>
                    <li class="nav-item"><a class="nav-link" href="AsignacionServlet">Asignar Solicitudes</a></li>
                    <li class="nav-item"><a class="nav-link" href="ConfigAgendaServlet">Gestionar Turnos</a></li>
                </ul>
            </div>
            <div class="d-flex align-items-center">
                <p class="navbar-text me-3 mb-0 text-white-50">Hola, Admin ${sessionScope.usuarioLogueado.nombre}</p>
                <a class="btn btn-danger btn-sm" href="../LogoutServlet"><i class="fas fa-sign-out-alt me-1"></i> Salir</a>
            </div>
        </div>
    </nav>