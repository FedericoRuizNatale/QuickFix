<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        /* Estilo rápido para centrar verticalmente (puedes moverlo a un CSS) */
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background-color: #f8f9fa; /* Un fondo gris claro suave */
        }
        .login-card {
            max-width: 450px; /* Ancho máximo de la tarjeta */
            width: 100%;
        }
        .card-header img {
             max-height: 50px; /* Ajusta el tamaño de tu logo */
             display: block;
             margin: 0 auto 10px; /* Centra el logo */
        }
    </style>
</head>
<body>

    <div class="card login-card shadow-sm">
        <div class="card-header text-center bg-primary text-white">
             <h4>QuickFix Service</h4>
        </div>
        <div class="card-body">
            
            <h5 class="card-title text-center mb-4">Iniciar Sesión</h5>
            
            <form action="LoginServlet" method="POST">
                
                <div class="mb-3">
                    <label for="emailInput" class="form-label">Correo Electrónico</label>
                    <input type="email" class="form-control" id="emailInput" name="email" required placeholder="tuemail@ejemplo.com">
                </div>
                
                <div class="mb-3">
                    <label for="passwordInput" class="form-label">Contraseña</label>
                    <input type="password" class="form-control" id="passwordInput" name="password" required placeholder="••••••••">
                </div>
                
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary btn-block">Ingresar</button>
                </div>
                
                <%@ taglib prefix="c" uri="jakarta.tags.core" %>
                <c:if test="${param.error == 'true'}">
                    <div class="alert alert-danger mt-3" role="alert">
                        Usuario o contraseña incorrectos.
                    </div>
                </c:if>
                
            </form>
            
            <div class="text-center mt-4">
                 <p class="small">¿No tienes una cuenta? <a href="register.jsp">Regístrate aquí</a></p>
                 </div>
            
        </div>
        <div class="card-footer text-muted text-center small">
             &copy; 2025 QuickFix - Todos los derechos reservados.
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>