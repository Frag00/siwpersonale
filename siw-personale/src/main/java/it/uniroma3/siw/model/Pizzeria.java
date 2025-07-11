package it.uniroma3.siw.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Pizzeria {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String nome;
	@NotBlank
	private String citta;
	@NotBlank
	private String indirizzo;
	
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Picture> immagini;
	
	@OneToMany(mappedBy = "pizzeria", cascade = CascadeType.ALL)
	private Set<Product> menu;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "pizzeria")
	private Set<Review> recensioni;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pizzeria")
	private Set<Ordine> ordini;

	public Pizzeria() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public Set<Picture> getImmagini() {
		return immagini;
	}

	public void setImmagini(Set<Picture> immagini) {
		this.immagini = immagini;
	}

	

	public Set<Product> getMenu() {
		return menu;
	}

	public void setMenu(Set<Product> menu) {
		this.menu = menu;
	}

	public Set<Ordine> getOrdini() {
		return ordini;
	}

	public void setOrdini(Set<Ordine> ordini) {
		this.ordini = ordini;
	}

	public Set<Review> getRecensioni() {
		return recensioni;
	}

	public void setRecensioni(Set<Review> recensioni) {
		this.recensioni = recensioni;
	}

	public Set<Ordine> getPrenotazioni() {
		return ordini;
	}

	public void setPrenotazioni(Set<Ordine> prenotazioni) {
		this.ordini = prenotazioni;
	}

	@Override
	public int hashCode() {
		return Objects.hash(citta, indirizzo, menu, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pizzeria other = (Pizzeria) obj;
		return Objects.equals(citta, other.citta) && Objects.equals(indirizzo, other.indirizzo)
				&& Objects.equals(menu, other.menu) && Objects.equals(nome, other.nome);
	}
	
	
	
	
	
}
