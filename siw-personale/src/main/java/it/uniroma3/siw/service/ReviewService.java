package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {
	@Autowired ReviewRepository reviewRepository;
	
	public boolean existsByPizzeriaAndUser(Pizzeria pizzeria, User user) {
		return reviewRepository.existsByPizzeriaAndUtente(pizzeria,user);
	}
	
	public void saveReview(Review r) {
		reviewRepository.save(r);
	}
}
