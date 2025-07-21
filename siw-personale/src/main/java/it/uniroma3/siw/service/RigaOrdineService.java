package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.RigaOrdine;
import it.uniroma3.siw.repository.RigaOrdineRepository;

@Service
public class RigaOrdineService {
	@Autowired RigaOrdineRepository rigaOrdineRepository;
	
	public List<RigaOrdine> findByProdotto(Product p){
		return rigaOrdineRepository.findByProdotto(p);
	}
	public void delete(RigaOrdine r) {
		rigaOrdineRepository.delete(r);
	}
	public void save(RigaOrdine r) {
		rigaOrdineRepository.save(r);
	}
}
