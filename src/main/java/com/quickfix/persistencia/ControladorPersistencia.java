package com.quickfix.persistencia;

import com.quickfix.dao.*; // Importa todos tus DAOs
import com.quickfix.entities.EquipoCliente;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ControladorPersistencia {

    // 1. La ÚNICA instancia del EntityManagerFactory
    private final EntityManagerFactory emf;
    
    // 2. Instancias de todos los DAOs que el sistema necesita
    public final ClienteDao clienteDao;
    public final TecnicoDao tecnicoDao;
    public final AdministradorDao adminDao;
    public final ServicioDao servicioDao;
    public final UsuarioDao usuarioDao; // El del login
    public final EquipoClienteDao equipoClienteDao;
    // ... etc.
    public ControladorPersistencia() {
        // 3. Se crea el EMF una sola vez
        this.emf = Persistence.createEntityManagerFactory("quickfix");
        
        // 4. Se crean los DAOs y se les "inyecta" el EMF
        this.clienteDao = new ClienteDao(emf);
        this.tecnicoDao = new TecnicoDao(emf);
        this.adminDao = new AdministradorDao(emf);
        this.servicioDao = new ServicioDao(emf);
        this.usuarioDao = new UsuarioDao(emf); // (El UsuarioDao también debería recibir el EMF)
        this.equipoClienteDao = new EquipoClienteDao(EquipoCliente.class, emf);
    }
    
    
    
    // Método para cerrar la conexión principal al final de la aplicación
    public void closeEmf() {
        if (emf != null) {
            emf.close();
        }
    }
}
