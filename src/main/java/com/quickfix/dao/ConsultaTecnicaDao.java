package com.quickfix.dao;

import com.quickfix.entities.ConsultaTecnica;
import com.quickfix.persistencia.GenericDao;
import jakarta.persistence.EntityManagerFactory;

public class ConsultaTecnicaDao extends GenericDao<ConsultaTecnica, Integer>{

	   
	public ConsultaTecnicaDao(EntityManagerFactory emf) {
        super(ConsultaTecnica.class, emf);
	}
}
