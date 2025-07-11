package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.service.PizzeriaService;

@Controller
public class PizzeriaController {

	@Autowired PizzeriaService pizzeriaService;
	
	@GetMapping("/pizzerie")
	public String showPizzerie(Model model) {
		model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
		return "pizzerie.html";
	}
	
	
}
