package com.quickfix.dao;

import com.quickfix.entities.DiaNoLaboral;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManagerFactory;

public class DiaNoLaboralDao extends GenericDao<DiaNoLaboral, Integer> {

    public DiaNoLaboralDao(EntityManagerFactory emf) {
        super(DiaNoLaboral.class, emf);
    }
    
    // Usaremos los que hereda (findAll, create, delete).
}
