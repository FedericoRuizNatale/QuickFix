package com.quickfix.dao;

import com.quickfix.entities.AgendaConfiguracion;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManagerFactory;

public class AgendaConfiguracionDao extends GenericDao<AgendaConfiguracion, Integer> {

    public AgendaConfiguracionDao(EntityManagerFactory emf) {
        super(AgendaConfiguracion.class, emf);
    }
    
    
}