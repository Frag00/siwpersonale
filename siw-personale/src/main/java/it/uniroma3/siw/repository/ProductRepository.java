package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.Product;

public interface ProductRepository extends CrudRepository<Product,Long> {
	
	List<Product> findByTipoAndPizzeria(String tipo, Pizzeria pizzeria);

	boolean existsByNomeAndPizzeria(String nome, Pizzeria pizzeria);
}
