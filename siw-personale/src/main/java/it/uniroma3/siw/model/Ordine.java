package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ordine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private User utente;
	
	@ManyToOne
	private Pizzeria pizzeria;
	
	@OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL)
    private List<RigaOrdine> righeOrdine;
	
	public Ordine() {}
	
	public void aggiungiProdotto(Product prodotto, int quantita) {
        RigaOrdine op = new RigaOrdine();
        op.setProdotto(prodotto);
        op.setQuantita(quantita);
        op.setOrdine(this);
        righeOrdine.add(op);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUtente() {
		return utente;
	}

	public void setUtente(User utente) {
		this.utente = utente;
	}

	public Pizzeria getPizzeria() {
		return pizzeria;
	}

	public void setPizzeria(Pizzeria pizzeria) {
		this.pizzeria = pizzeria;
	}

	public List<RigaOrdine> getRigaOrdine() {
		return righeOrdine;
	}

	public void setRigaOrdine(List<RigaOrdine> rigaOrdine) {
		this.righeOrdine = rigaOrdine;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pizzeria, utente);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordine other = (Ordine) obj;
		return Objects.equals(pizzeria, other.pizzeria) && Objects.equals(utente, other.utente);
	}
	
	
}
