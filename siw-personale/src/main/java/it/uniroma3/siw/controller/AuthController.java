package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	@Autowired CredentialsService credentialsService;
	@Autowired UserService userService;
	
	@GetMapping("/")
	public String getIndex() {
		return "index.html";
	}
	
	@GetMapping("/admin")
	public String getIndexAdmin() {
		return "admin/adminHome.html";
	}
	
	@GetMapping("/user")
	public String getIndexUser() {
		return "user/userHome.html";
	}
	
	@GetMapping(value = "/login")
	public String getFormLogin(Model model) {
		return "login.html";
	}
	
	@GetMapping(value = "/register")
	public String getFormRegister(Model model) {
		model.addAttribute("utente",new User());
		model.addAttribute("credentials",new Credentials());
		return "register.html";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("utente") User u,BindingResult bindingResultUser,@Valid @ModelAttribute("credentials") Credentials c, BindingResult bindingResultCredentials,Model model) {
		if(credentialsService.existsByUsername(c.getUsername())) {
			model.addAttribute("esiste", "utente già presente nel db");
			model.addAttribute("utente",u);
			model.addAttribute("credentials",c);
			return "register.html";
		}
		else if (bindingResultUser.hasErrors() || bindingResultCredentials.hasErrors()){
			model.addAttribute("utente",u);
			model.addAttribute("credentials",c);
			return "register.html";
		}
		
		else {
			
		c.setUtente(u);
		credentialsService.saveCredentials(c);
		
		// Redirect al login con messaggio di successo
	    return "redirect:/login?registered=true";
		
		}
	}
}
