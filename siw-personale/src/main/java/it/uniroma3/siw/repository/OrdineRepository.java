package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Ordine;
import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.User;

public interface OrdineRepository extends CrudRepository<Ordine, Long> {
	
	public List<Ordine> findByUtente(User u);
	public List<Ordine> findByPizzeria(Pizzeria p);

}
