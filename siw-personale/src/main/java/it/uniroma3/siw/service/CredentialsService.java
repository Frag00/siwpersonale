package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import static it.uniroma3.siw.model.Credentials.ruoloAdmin;
import static it.uniroma3.siw.model.Credentials.ruoloDefault;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import jakarta.validation.Valid;

@Service
public class CredentialsService {
	@Autowired
	private CredentialsRepository credentialsRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Credentials getCredentialsByUsername(String username) {
		return this.credentialsRepository.findByUsername(username).orElse(null);
	}

	public boolean existsByUsername(String username) {
		return this.credentialsRepository.existsByUsername(username);
	}

	public void saveCredentials(@Valid Credentials credentials) {
		credentials.setRuolo(ruoloDefault);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		this.credentialsRepository.save(credentials);

	}
	
	public void saveCredentialsForAdmin(@Valid Credentials credentials) {
		credentials.setRuolo(ruoloAdmin);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		this.credentialsRepository.save(credentials);

	}

	public Credentials getCredentialsByUser(User user) {
		return this.credentialsRepository.findByUtente(user).orElse(null);
	}

	public boolean isAdminCurrent(User admin) {
		try {
			return this.getCredentialsByUser(admin).getRuolo().equals(ruoloAdmin);
		} catch (Exception e) {
			return false;
		}
	}

	public User getCurrentUser() {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return this.getCredentialsByUsername(userDetails.getUsername()).getUtente();
	}
}
