package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired UserService userService;
	@Autowired CredentialsService credentialsService;
	@Autowired ReviewService reviewService;
	
	private boolean verifyId(Long idUrl, Long idUser) {
		return idUser!= null && idUrl == idUser;
	}
	
	@GetMapping("/user/profile")
	public String profiloUtente(Model model) {
			model.addAttribute("recensioni",userService.getCurrentUser().getRecensioni());
			model.addAttribute("ordini", userService.getCurrentUser().getOrdini());
			return "user/profile.html";
	}
	
	@GetMapping("/user/profile/formChangePassword")
	public String userFormChangePwd() {
		return "user/formChangePassword.html";
	}
	
	@PostMapping("/user/profile/changePassword")
	public String userChangesPwd(@RequestParam @Valid String confirmPwd, @RequestParam @Valid String newPwd, Model model) {
		if(newPwd == null || confirmPwd == null || newPwd.equals("") || confirmPwd.equals("") || !newPwd.equals(confirmPwd)) {
			model.addAttribute("msgError", "Il campo della nuova password è vuoto o le password non coincidono!");
			return "user/formChangePassword.html";
		}
		User user = userService.getCurrentUser();
		Credentials credentials = this.credentialsService.getCredentialsByUser(user);
		credentials.setPassword(newPwd);
		this.credentialsService.saveCredentials(credentials);
		return "redirect:/user/profile";
	}
	
	@GetMapping("/user/profile/formChangeProfile")
	public String userFormChangeProfile() {
		return "user/formChangeProfile.html";
	}
	
	@PostMapping("/user/profile/changeProfile")
	public String userChangesProfile(@Valid @ModelAttribute("user") User u, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("user", userService.getCurrentUser());
			return "user/formChangeProfile.html";
		}
		userService.saveUser(u);
		return "redirect:/user/profile";
	}
	
	/* PER L'ADMIN */
	
	@GetMapping("/admin/profile")
	public String profiloAdmin(Model model) {
			model.addAttribute("recensioni",userService.getCurrentUser().getRecensioni());
			return "admin/profile.html";
	}
	
	@GetMapping("/admin/profile/formChangePassword")
	public String adminFormChangePwd() {
		return "admin/formChangePassword.html";
	}
	
	@PostMapping("/admin/profile/changePassword")
	public String adminChangesPwd(@RequestParam @Valid String confirmPwd, @RequestParam @Valid String newPwd, Model model) {
		if(newPwd == null || confirmPwd == null || newPwd.equals("") || confirmPwd.equals("") || !newPwd.equals(confirmPwd)) {
			model.addAttribute("msgError", "Il campo della nuova password è vuoto o le password non coincidono!");
			return "admin/formChangePassword.html";
		}
		User user = userService.getCurrentUser();
		Credentials credentials = this.credentialsService.getCredentialsByUser(user);
		credentials.setPassword(newPwd);
		this.credentialsService.saveCredentialsForAdmin(credentials);
		return "redirect:/admin/profile";
	}
	
	@GetMapping("/admin/profile/formChangeProfile")
	public String adminFormChangeProfile() {
		return "admin/formChangeProfile.html";
	}
	
	@PostMapping("/admin/profile/changeProfile")
	public String adminChangesProfile(@Valid @ModelAttribute("user") User u, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("user", userService.getCurrentUser());
			return "admin/formChangeProfile.html";
		}
		userService.saveUser(u);
		return "redirect:/admin/profile";
	}
}
