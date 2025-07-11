package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.repository.PizzeriaRepository;

@Service
public class PizzeriaService {
	@Autowired PizzeriaRepository pizzeriaRepository;
	
	public Iterable<Pizzeria> getAllPizzerie(){
		return pizzeriaRepository.findAll();
	}
}
