<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>QuickFix - Soluciones Técnicas Rápidas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <style>
        /* Estilos básicos */
        body { padding-top: 56px; } 
        .masthead { background-color: #007bff; color: white; padding: 6rem 0; }
        .features-section { padding: 2rem 0; }
        .about-us-section { padding: 4rem 0; background-color: #f8f9fa; }
    </style>
</head>
<body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top shadow">
        <div class="container">
            <a class="navbar-brand fw-bold" href="index.jsp">QuickFix</a>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="#about-us">Sobre Nosotros</a> 
                    </li>
                    
                    <li class="nav-item">
                        <a class="nav-link text-warning" href="#puntos-lealtad">Puntos de Lealtad</a> 
                    </li>
                </ul>
            </div>
            
            <div class="d-flex">
                <a class="btn btn-outline-light me-2" href="login.jsp">
                    <i class="fas fa-sign-in-alt me-1"></i> Iniciar Sesión
                </a>
                <a class="btn btn-warning" href="register.jsp">
                    <i class="fas fa-user-plus me-1"></i> Regístrate
                </a>
            </div>
        </div>
    </nav>
    <header class="masthead text-center">
        <div class="container">
            <h1 class="display-4 fw-bold">Soluciones Técnicas Rápidas para tu PC</h1>
            <p class="lead mb-4">Solicita reparación, limpieza de software y consulta a expertos.</p>
            <a class="btn btn-warning btn-lg fw-bold shadow-lg" href="register.jsp">¡Empieza Ahora! Es Gratis</a>
        </div>
    </header>

    <%@ include file="sobreNosotros.jsp" %>
    
    <%@ include file="puntosLealtad.jsp" %>


    <section class="features-section text-center">
        </section>

    <footer class="bg-dark text-white text-center py-3 mt-auto">
        <div class="container">
            <p class="mb-0 small">&copy; 2025 QuickFix</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>