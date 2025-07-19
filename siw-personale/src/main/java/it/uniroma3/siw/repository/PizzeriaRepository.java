package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Pizzeria;

public interface PizzeriaRepository extends CrudRepository<Pizzeria,Long>{

	public boolean existsByNomeAndIndirizzo(String nome, String indirizzo);

	public Iterable<Pizzeria> findByNomeContainingIgnoreCaseOrCittaContainingIgnoreCase(String nome, String citta);
}
