package it.uniroma3.siw.service;

import java.util.Optional;

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

	public Optional<Pizzeria> getPizzeriaById(Long id) {
		return pizzeriaRepository.findById(id);
	}
	
	public boolean existsByNomeAndIndirizzo(Pizzeria p) {
		return pizzeriaRepository.existsByNomeAndIndirizzo(p.getNome(),p.getIndirizzo());
	}
	
	public void savePizzeria(Pizzeria p) {
		pizzeriaRepository.save(p);
	}
	
	public void deletePizzeria(Pizzeria p) {	
		pizzeriaRepository.delete(p);
	}
}
