package it.uniroma3.siw.model;


import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String nome;
	
	@NotBlank
	private String descrizione;
	
	@NotBlank
	private String tipo;
	
	@NotNull
	@Min(0)
	private Float prezzo;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Picture immagine;
	
	@ManyToOne
	private Pizzeria pizzeria;
	
	public Pizzeria getPizzeria() {
		return pizzeria;
	}

	public void setPizzeria(Pizzeria pizzeria) {
		this.pizzeria = pizzeria;
	}

	public Product() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Float getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Float prezzo) {
		this.prezzo = prezzo;
	}

	public Picture getImmagine() {
		return immagine;
	}

	public void setImmagine(Picture immagine) {
		this.immagine = immagine;
	}


	@Override
	public int hashCode() {
		return Objects.hash(descrizione, nome, prezzo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(descrizione, other.descrizione) && Objects.equals(nome, other.nome)
				&& Float.floatToIntBits(prezzo) == Float.floatToIntBits(other.prezzo);
	}

	@Override
	public String toString() {
		return "Product [nome=" + nome + ", descrizione=" + descrizione + ", prezzo=" + prezzo + "]";
	}
	
	
}
