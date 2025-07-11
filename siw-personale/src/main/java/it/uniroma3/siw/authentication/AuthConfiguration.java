package it.uniroma3.siw.authentication;

import static it.uniroma3.siw.model.Credentials.ruoloAdmin;
import static it.uniroma3.siw.model.Credentials.ruoloDefault;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.uniroma3.siw.service.CredentialsService;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.authoritiesByUsernameQuery("SELECT username, ruolo from credentials WHERE username=?")
				.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
	}


	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().and().cors().disable().authorizeHttpRequests()
				// Consentiti a tutti (occasionali)
				.requestMatchers(HttpMethod.GET, "/", "/index", "/pizzerie/**", "/author/**",
						"/login", "/register", "/css/**", "/immagini/**", "favicon.ico")
				.permitAll().requestMatchers(HttpMethod.POST, "/register", "/login",  "/immagini/**")
				.permitAll()

				// Solo ADMIN_ROLE
				.requestMatchers("/admin/**").hasAuthority(ruoloAdmin)

				// Solo DEFAULT_ROLE
				.requestMatchers("/user/**").hasAuthority(ruoloDefault)

				// Qualunque altra richiesta: autenticazione
				.anyRequest().authenticated().and().formLogin().loginPage("/login") // Pagina di login di default
																							// per tutti
				.loginProcessingUrl("/login") // URL di submit form login user
				.usernameParameter("username").passwordParameter("pwd")
				.successHandler((request, response, authentication) -> {
					// Success handler custom: redirect in base al ruolo
					var principal = authentication.getPrincipal();
					// Recupero id utente dal Principal
					Long idUtente = null;
					String username = null;
					if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
						// Ottieni ID utente qui secondo la tua implementazione
						// Esempio: CredentialsService -> trova utente per username
						username = userDetails.getUsername();
						idUtente = this.credentialsService.getCredentialsByUsername(username).getUtente().getId();
					
					}
					boolean isAdmin = this.credentialsService.getCredentialsByUsername(username).getRuolo().equals(ruoloAdmin);
					if (isAdmin) {
						// Se ADMIN, redirect operatore (sostituisci idUtente)
						response.sendRedirect(idUtente != null ? "/admin/profile": "/login");
					} else {
						// Se Utente, redirect utente (sostituisci idUtente)
						response.sendRedirect(idUtente != null ? "/user/profile" : "/login");
					}
				}).failureUrl("/login?error=true").permitAll().and().logout().logoutUrl("/logout")
				.logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).clearAuthentication(true).permitAll();
		return httpSecurity.build();
	}

}