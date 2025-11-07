package com.quickfix.dao;

import com.quickfix.entities.HorarioLaboral;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManagerFactory;

public class HorarioLaboralDao extends GenericDao<HorarioLaboral, Integer> {

    public HorarioLaboralDao(EntityManagerFactory emf) {
        super(HorarioLaboral.class, emf);
    }
    
    // Por ahora usaremos los que hereda (findAll, update).
    // Tal vez luego agreguemos un "findByDiaSemana(int dia)".
}