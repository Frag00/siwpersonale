package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.validation.Valid;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CredentialsService credentialsService;

	public User getCurrentUser() {
		return credentialsService.getCurrentUser();
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public void saveUser(@Valid User user) {
		this.userRepository.save(user);
		
	}
}
