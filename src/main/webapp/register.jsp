<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registro - QuickFix</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        /* Estilo para centrar verticalmente */
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background-color: #f8f9fa;
        }
        .register-card {
            max-width: 600px; /* Un poco más ancho que el login */
            width: 100%;
        }
    </style>
</head>
<body>

    <div class="card register-card shadow-lg">
        <div class="card-header text-center bg-primary text-white">
             <h4>Registro de Nuevo Cliente</h4>
        </div>
        <div class="card-body">
            
            <form action="RegisterServlet" method="POST">
                
                <%@ taglib prefix="c" uri="jakarta.tags.core" %>
                <c:if test="${param.exito == 'true'}">
                    <div class="alert alert-success" role="alert">
                        ¡Registro exitoso! Ya puedes <a href="login.jsp">iniciar sesión</a>.
                    </div>
                </c:if>
                <c:if test="${param.error == 'email'}">
                    <div class="alert alert-danger" role="alert">
                        Error: El correo electrónico ya está registrado.
                    </div>
                </c:if>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="nombreInput" class="form-label">Nombre</label>
                        <input type="text" class="form-control" id="nombreInput" name="nombre" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="apellidoInput" class="form-label">Apellido</label>
                        <input type="text" class="form-control" id="apellidoInput" name="apellido" required>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="emailInput" class="form-label">Email</label>
                        <input type="email" class="form-control" id="emailInput" name="email" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="passwordInput" class="form-label">Contraseña</label>
                        <input type="password" class="form-control" id="passwordInput" name="password" required>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label for="dniInput" class="form-label">DNI</label>
                        <input type="text" class="form-control" id="dniInput" name="dni" required>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="telefonoInput" class="form-label">Teléfono</label>
                        <input type="text" class="form-control" id="telefonoInput" name="telefono" required>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="direccionInput" class="form-label">Dirección</label>
                        <input type="text" class="form-control" id="direccionInput" name="direccion" required>
                    </div>
                </div>

                <div class="d-grid gap-2 mt-4">
                    <button type="submit" class="btn btn-success btn-block">Registrarse</button>
                </div>
            </form>
            
            <div class="text-center mt-3">
                 <p class="small">¿Ya tienes cuenta? <a href="login.jsp">Iniciar Sesión</a></p>
                 
                 <p class="small mt-2"><a href="index.jsp">Volver al Inicio</a></p>
            </div>
            
        </div>
        <div class="card-footer text-muted text-center small">
             &copy; 2025 QuickFix
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>