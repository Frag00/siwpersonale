package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Ordine;
import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.OrdineRepository;

@Service
public class OrdineService {

	@Autowired OrdineRepository ordineRepository;
	
	public void saveOrdine(Ordine o) {
		ordineRepository.save(o);
	}
	
	public Ordine getOrdineById(Long id) {
		return ordineRepository.findById(id).get();
	}
	
	public List<Ordine> findByUtente(User u) {
		return ordineRepository.findByUtente(u);
	}
	
	public List<Ordine> findByPizzeria(Pizzeria p){
		return ordineRepository.findByPizzeria(p);
	}
	
	public void deleteOrdine(Ordine o) {
		ordineRepository.delete(o);
	}
	
}
