package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.PizzeriaService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;

import jakarta.validation.Valid;

@Controller
public class ReviewController {
	
	@Autowired ReviewService reviewService;
	@Autowired PizzeriaService pizzeriaService;
	@Autowired CredentialsService credentialsService;
	@Autowired UserService userService;
	
	@GetMapping("/user/pizzerie/{idP}/formNewReview")
	public String showUserFormNewReview(@PathVariable("idP") Long idP, Model model) {
		if(!reviewService.existsByPizzeriaAndUser(pizzeriaService.getPizzeriaById(idP).get(),credentialsService.getCurrentUser())) {
			model.addAttribute("review", new Review());
			model.addAttribute("pizzeria",pizzeriaService.getPizzeriaById(idP).get());
			return "user/formNewReview.html";
		}
		else {
			model.addAttribute("giaEsiste", "Hai già scritto una recensione per questa Pizzeria");
			model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
			return "user/formNewReview.html";
		}
			
	}
	
	@PostMapping("/user/pizzerie/{idP}/addReview")
	public String userAddsReview(@Valid @ModelAttribute("review") Review r, BindingResult bindingResult, @PathVariable("idP") Long idP, Model model) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("review", r);
			model.addAttribute("pizzeria",pizzeriaService.getPizzeriaById(idP).get());
			return "user/formNewReview.html";
		}
		
		Review review = new Review();
		Pizzeria p = pizzeriaService.getPizzeriaById(idP).get();
		User u = credentialsService.getCurrentUser();
		
		review.setPizzeria(p);
		review.setTesto(r.getTesto());
		review.setTitolo(r.getTitolo());
		review.setUtente(u);
		review.setVoto(r.getVoto());
		
		
		p.getRecensioni().add(review);
		u.getRecensioni().add(review);
		
		
		
		reviewService.saveReview(review);
		
		pizzeriaService.savePizzeria(p);
		userService.saveUser(u);
		
		model.addAttribute("pizzeria", p);
		return "redirect:/user/pizzerie/" + idP;
	}
	
	@GetMapping("/user/pizzerie/{idP}/formEditReview/{idR}")
	public String showUserFormEditReview(@PathVariable("idP") Long idP, @PathVariable("idR") Long idR, Model model) {
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
		model.addAttribute("review", reviewService.getReviewById(idR));
		return "user/formEditReview.html";
	}
	
	@PostMapping("/user/pizzerie/{idP}/editReview/{idR}")
	public String userEditsReview(@Valid @ModelAttribute("review") Review r, BindingResult bindingResult, @PathVariable("idP") Long idP, @PathVariable("idR") Long idR, Model model) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("review", r);
			model.addAttribute("pizzeria",pizzeriaService.getPizzeriaById(idP).get());
			return "user/formEditReview.html";
		}
		
		Review review = reviewService.getReviewById(idR);
		
		review.setTesto(r.getTesto());
		review.setTitolo(r.getTitolo());
		review.setVoto(r.getVoto());
		
		reviewService.saveReview(review);
		
		
		
		pizzeriaService.savePizzeria(review.getPizzeria());
		userService.saveUser(review.getUtente());
		
		model.addAttribute("pizzeria", review.getPizzeria());
		return "redirect:/user/pizzerie/" + idP;
	}
	
	@GetMapping("/user/pizzerie/{idP}/removeReview/{idR}")
	@Transactional
	public String userDeletesReview(@PathVariable("idP") Long idP, @PathVariable("idR") Long idR, Model model) {
		
		Review r = reviewService.getReviewById(idR);
		Pizzeria p = r.getPizzeria();
		User u = credentialsService.getCurrentUser();
		if(r.getUtente().equals(u)) {
			u.getRecensioni().remove(r);
			p.getRecensioni().remove(r);
			
			userService.saveUser(u);
			pizzeriaService.savePizzeria(p);
			
			
			reviewService.deleteReview(r);
			
			
			return "redirect:/user/pizzerie/" + p.getId();
		}
		else return "error.html";
	}
	
	@GetMapping("/admin/pizzerie/{idP}/removeReview/{idR}")
	@Transactional
	public String adminDeletesReview(@PathVariable("idP") Long idP, @PathVariable("idR") Long idR, Model model) {
		
		Review r = reviewService.getReviewById(idR);
		Pizzeria p = r.getPizzeria();
		User u = r.getUtente();
		
			u.getRecensioni().remove(r);
			p.getRecensioni().remove(r);
			
			userService.saveUser(u);
			pizzeriaService.savePizzeria(p);
			
			
			reviewService.deleteReview(r);
			
			
			return "redirect:/admin/pizzerie/" + p.getId();
		
	}
}
