package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.repository.ProductRepository;
import jakarta.validation.Valid;

@Service
public class ProductService {

	@Autowired ProductRepository productRepository;
	
	public List<Product> getProductsByTipoAndPizzeria(String tipo, Pizzeria pizzeria){
		return productRepository.findByTipoAndPizzeria(tipo, pizzeria);
	}
	
	public boolean existsByNomeAndPizzeria(String nome, Pizzeria pizzeria) {
		return productRepository.existsByNomeAndPizzeria(nome,pizzeria);
	}

	public void saveProduct(Product prodotto) {
		productRepository.save(prodotto);
		
	}
}
