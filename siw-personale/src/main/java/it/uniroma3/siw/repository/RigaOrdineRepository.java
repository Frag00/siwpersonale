package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.RigaOrdine;

public interface RigaOrdineRepository extends CrudRepository<RigaOrdine,Long> {
	public List<RigaOrdine> findByProdotto(Product p);
}
