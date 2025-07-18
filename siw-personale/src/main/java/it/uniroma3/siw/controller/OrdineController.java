package it.uniroma3.siw.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Ordine;
import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.RigaOrdine;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.OrdineService;
import it.uniroma3.siw.service.PizzeriaService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrdineController {
	
	@Autowired PizzeriaService pizzeriaService;
	@Autowired UserService userService;
	@Autowired OrdineService ordineService;

	@GetMapping("/user/pizzerie/{pizzeriaId}/formNewOrdine")
	public String showUserformNewOrdine(@PathVariable("pizzeriaId") Long pizzeriaId, Model model) {
	  
	    Pizzeria pizzeria = pizzeriaService.getPizzeriaById(pizzeriaId).get();	   
	    model.addAttribute("pizzeria", pizzeria);
	    
	    return "user/formNewOrdine.html";
	}

	@PostMapping("/user/pizzerie/{pId}/ordine/riepilogo")
	public String mostraRiepilogo(@PathVariable("pId") Long pizzeriaId, 
	                             HttpServletRequest request,
	                             Model model) {
	    
	
	    Pizzeria pizzeria = pizzeriaService.getPizzeriaById(pizzeriaId).get();

	    // Mappa per salvare le quantità inserite
	    Map<Long, Integer> quantita = new HashMap<>();
	    
	    // Lista per il riepilogo
	    List<RigaOrdineTemp> riepilogo = new ArrayList<>();
	    double totale = 0;
	    
	    // Elabora i prodotti selezionati
	    for (Product prodotto : pizzeria.getMenu()) {
	        String quantitaParam = "quantita_" + prodotto.getId();
	        String quantitaStr = request.getParameter(quantitaParam);
	        
	        int quantitaProdotto = 0;
	        if (quantitaStr != null && !quantitaStr.isEmpty()) {
	        	// controllo nella conversione stringa-int (esclude "" e "abc")
	            try {
	                quantitaProdotto = Integer.parseInt(quantitaStr);
	            } catch (NumberFormatException e) {
	                quantitaProdotto = 0;
	            }
	        }
	        
	        // Salva la quantità nella mappa (anche se è 0)
	        quantita.put(prodotto.getId(), quantitaProdotto);
	        
	        // Aggiungi al riepilogo solo se la quantità è maggiore di 0
	        if (quantitaProdotto > 0) {
	            RigaOrdineTemp riga = new RigaOrdineTemp();
	            riga.setProdotto(prodotto);
	            riga.setQuantita(quantitaProdotto);
	            riepilogo.add(riga);
	            
	            totale += prodotto.getPrezzo() * quantitaProdotto;
	        }
	    }
	    
	    // Aggiornamento del model
	    model.addAttribute("pizzeria", pizzeria);
	    model.addAttribute("quantita", quantita);
	    model.addAttribute("riepilogo", riepilogo);
	    model.addAttribute("totale", totale);
	    
	    return "user/formNewOrdine.html";
	}

	@PostMapping("/user/pizzerie/{pId}/addOrdine")
	public String userMakesNewOrdine(@PathVariable("pId") Long pizzeriaId, 
	                        HttpServletRequest request,
	                        RedirectAttributes redirectAttributes) {
	    
	    try {
	        // Recupera l'utente corrente
	        User utenteCorrente = userService.getCurrentUser();
	        
	        // Recupera la pizzeria
	        Pizzeria pizzeria = pizzeriaService.getPizzeriaById(pizzeriaId).get();
	        if (pizzeria == null) {
	            redirectAttributes.addFlashAttribute("error", "Pizzeria non trovata");
	            return "redirect:/user/pizzerie";
	        }
	        
	        // Recupera orario e indirizzo per la consegna
	        String indirizzoConsegna = request.getParameter("indirizzoConsegna");
	        String orarioConsegnaStr = request.getParameter("orarioConsegna");

	        if (indirizzoConsegna == null || indirizzoConsegna.isBlank() || orarioConsegnaStr == null || orarioConsegnaStr.isBlank()) {
	            redirectAttributes.addFlashAttribute("error", "Inserisci indirizzo e orario di consegna");
	            return "redirect:/user/pizzerie/" + pizzeriaId + "/formNewOrdine";
	        }

	        LocalTime orarioConsegna = LocalTime.parse(orarioConsegnaStr);
	        
	        
	        
	        // Crea il nuovo ordine
	        Ordine nuovoOrdine = new Ordine();
	        nuovoOrdine.setUtente(utenteCorrente);
	        nuovoOrdine.setPizzeria(pizzeria);
	        nuovoOrdine.setIndirizzoConsegna(indirizzoConsegna);
	        nuovoOrdine.setOrarioConsegna(orarioConsegna);
	        nuovoOrdine.setRigaOrdine(new ArrayList<>());
	        
	        // Elabora i prodotti selezionati
	        boolean hasProducts = false;
	        
	        for (Product prodotto : pizzeria.getMenu()) {
	            String quantitaParam = "quantita_" + prodotto.getId();
	            String quantitaStr = request.getParameter(quantitaParam);
	            
	            if (quantitaStr != null && !quantitaStr.isEmpty()) {
	                int quantita = Integer.parseInt(quantitaStr);
	                
	                if (quantita > 0) {
	                    hasProducts = true;
	                    nuovoOrdine.aggiungiProdotto(prodotto, quantita);
	                }
	            }
	        }
	        
	        // Controlla se ci sono prodotti nell'ordine
	        if (!hasProducts) {
	            redirectAttributes.addFlashAttribute("error", "Devi selezionare almeno un prodotto");
	            return "redirect:/user/pizzerie/" + pizzeriaId + "/formNewOrdine";
	        }
	        
	        // Salva l'ordine
	        ordineService.saveOrdine(nuovoOrdine);
	        
	        redirectAttributes.addFlashAttribute("success", "Ordine creato con successo!");
	        return "redirect:/user/ordini/" + nuovoOrdine.getId();
	        
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Errore nella creazione dell'ordine");
	        return "redirect:/user/pizzerie/" + pizzeriaId + "/formNewOrdine";
	    }
	}

	@GetMapping("/user/ordini/{ordineId}")
	public String visualizzaOrdine(@PathVariable Long ordineId, Model model) {
	    Ordine ordine = ordineService.getOrdineById(ordineId);
	    if (ordine == null) {
	        return "redirect:/user/ordini";
	    }
	    
	    // Verifica che l'ordine appartenga all'utente corrente
	    User utenteCorrente = userService.getCurrentUser();
	    if (!ordine.getUtente().equals(utenteCorrente)) {
	        return "redirect:/user/ordini";
	    }
	    
	    // Calcola il totale dell'ordine
	    double totale = calcolaTotaleOrdine(ordine);
	    
	    model.addAttribute("ordine", ordine);
	    model.addAttribute("totale", totale);
	    
	    return "user/dettaglioOrdine.html";
	}

	@GetMapping("/user/ordini")
	public String listaOrdini(Model model) {
	    User utenteCorrente = userService.getCurrentUser();
	    List<Ordine> ordini = ordineService.findByUtente(utenteCorrente);
	    
	    model.addAttribute("ordini", ordini);
	    
	    return "user/listaOrdini.html";
	}

	// Metodo helper per calcolare il totale dell'ordine
	private double calcolaTotaleOrdine(Ordine ordine) {
	    return ordine.getRigaOrdine().stream()
	            .mapToDouble(riga -> riga.getProdotto().getPrezzo() * riga.getQuantita())
	            .sum();
	}



	// Classe helper per il riepilogo temporaneo
	public static class RigaOrdineTemp {
	    private Product prodotto;
	    private Integer quantita;
	    
	    public Product getProdotto() { return prodotto; }
	    public void setProdotto(Product prodotto) { this.prodotto = prodotto; }
	    public Integer getQuantita() { return quantita; }
	    public void setQuantita(Integer quantita) { this.quantita = quantita; }
	}
	
	
	@GetMapping("/admin/pizzerie/{pId}/ordini")
	public String showAdminOrdiniPizzeria(@PathVariable("pId") Long idP, Model model) {
		
		Pizzeria p = pizzeriaService.getPizzeriaById(idP).get();
		List<Ordine> ordini = ordineService.findByPizzeria(p);
		
		model.addAttribute("ordini", ordini);
		model.addAttribute("pizzeria",p);
		
		return "admin/ordiniPizzeria.html";
	}
	
	@GetMapping("/admin/pizzerie/{pId}/ordini/{oId}")
	public String showAdminOrdine(@PathVariable("pId") Long idP, @PathVariable("oId") Long idO, Model model) {
		Ordine ordine = ordineService.getOrdineById(idO);
	    if (ordine == null) {
	        return "redirect:/user/ordini";
	    }
	    
	    double totale = calcolaTotaleOrdine(ordine);
	    
	    model.addAttribute("ordine", ordine);
	    model.addAttribute("totale", totale);
		
		return "admin/ordine.html";
	}
	
	@GetMapping("/user/ordini/{oId}/delete")
	public String userDeletesOrdine(@PathVariable("oId") Long idO, Model model) {
		
		Ordine o = ordineService.getOrdineById(idO);
		Pizzeria p = o.getPizzeria();
		User u = o.getUtente();
		
		if(u.equals(userService.getCurrentUser())) {
			p.getOrdini().remove(o);
			u.getOrdini().remove(o);
			
			ordineService.deleteOrdine(o);
			
			pizzeriaService.savePizzeria(p);
			userService.saveUser(u);
			if(!u.getOrdini().isEmpty())
				return "redirect:/user/ordini";
			else 
				return "redirect:/user/profile";
			}
		else
			return "redirect:error.html";
	}
	
	@GetMapping("/admin/pizzerie/{pId}/ordini/{oId}/delete")
	public String adminDeletesOrdine(@PathVariable("oId") Long idO, @PathVariable("pId") Long pId, Model model) {
		
		Ordine o = ordineService.getOrdineById(idO);
		Pizzeria p = o.getPizzeria();
		User u = o.getUtente();
		
		
		p.getOrdini().remove(o);
		u.getOrdini().remove(o);
			
		ordineService.deleteOrdine(o);
			
		pizzeriaService.savePizzeria(p);
		userService.saveUser(u);
		
		if(!p.getOrdini().isEmpty())
			return "redirect:/admin/pizzerie/" + pId + "/ordini";
		else
			return "redirect:/admin/pizzerie/" + pId;
		
	}
	
	/* per la modifica dell'ordine **********************************/
	
	@GetMapping("/user/ordini/{ordineId}/edit")
	public String showEditOrderForm(@PathVariable("ordineId") Long ordineId, Model model, RedirectAttributes redirectAttributes) {
	    Ordine ordine = ordineService.getOrdineById(ordineId);
	    User utenteCorrente = userService.getCurrentUser();

	    if (ordine == null || !ordine.getUtente().equals(utenteCorrente)) {
	        redirectAttributes.addFlashAttribute("error", "Ordine non trovato o accesso non autorizzato");
	        return "redirect:/user/ordini";
	    }

	    Pizzeria pizzeria = ordine.getPizzeria();

	    // Prepara mappa quantità per precompilare il form
	    Map<Long, Integer> quantita = new HashMap<>();
	    for (Product prodotto : pizzeria.getMenu()) {
	        quantita.put(prodotto.getId(), 0);
	    }
	    for (RigaOrdine r : ordine.getRigaOrdine()) {
	        quantita.put(r.getProdotto().getId(), r.getQuantita());
	    }

	    model.addAttribute("ordine", ordine);
	    model.addAttribute("pizzeria", pizzeria);
	    model.addAttribute("quantita", quantita);

	    return "user/formEditOrdine.html";
	}

	
	
	@PostMapping("/user/ordini/{ordineId}/riepilogo")
	public String mostraRiepilogoModifica(@PathVariable("ordineId") Long ordineId, 
	                                     HttpServletRequest request,
	                                     Model model,
	                                     RedirectAttributes redirectAttributes) {

	    Ordine ordine = ordineService.getOrdineById(ordineId);
	    if (ordine == null) {
	        redirectAttributes.addFlashAttribute("error", "Ordine non trovato");
	        return "redirect:/user/ordini";
	    }

	    Pizzeria pizzeria = ordine.getPizzeria();

	    Map<Long, Integer> quantita = new HashMap<>();
	    List<RigaOrdineTemp> riepilogo = new ArrayList<>();
	    double totale = 0;

	    for (Product prodotto : pizzeria.getMenu()) {
	        String quantitaParam = "quantita_" + prodotto.getId();
	        String quantitaStr = request.getParameter(quantitaParam);

	        int quantitaProdotto = 0;
	        if (quantitaStr != null && !quantitaStr.isEmpty()) {
	            try {
	                quantitaProdotto = Integer.parseInt(quantitaStr);
	            } catch (NumberFormatException e) {
	                quantitaProdotto = 0;
	            }
	        }

	        quantita.put(prodotto.getId(), quantitaProdotto);

	        if (quantitaProdotto > 0) {
	            RigaOrdineTemp riga = new RigaOrdineTemp();
	            riga.setProdotto(prodotto);
	            riga.setQuantita(quantitaProdotto);
	            riepilogo.add(riga);

	            totale += prodotto.getPrezzo() * quantitaProdotto;
	        }
	    }

	    model.addAttribute("ordine", ordine);
	    model.addAttribute("pizzeria", pizzeria);
	    model.addAttribute("quantita", quantita);
	    model.addAttribute("riepilogo", riepilogo);
	    model.addAttribute("totale", totale);

	    // Passa anche indirizzo e orario (nel caso fossero già stati inseriti)
	    String indirizzoConsegna = request.getParameter("indirizzoConsegna");
	    String orarioConsegna = request.getParameter("orarioConsegna");
	    model.addAttribute("indirizzoConsegna", indirizzoConsegna);
	    model.addAttribute("orarioConsegna", orarioConsegna);

	    return "user/formEditOrdine.html";
	}

	
	@PostMapping("/user/ordini/{ordineId}/update")
	public String updateOrdine(@PathVariable("ordineId") Long ordineId,
	                           HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {

	    try {
	        Ordine ordine = ordineService.getOrdineById(ordineId);
	        User utenteCorrente = userService.getCurrentUser();

	        if (ordine == null || !ordine.getUtente().equals(utenteCorrente)) {
	            redirectAttributes.addFlashAttribute("error", "Ordine non trovato o accesso non autorizzato");
	            return "redirect:/user/ordini";
	        }

	        Pizzeria pizzeria = ordine.getPizzeria();

	        String indirizzoConsegna = request.getParameter("indirizzoConsegna");
	        String orarioConsegnaStr = request.getParameter("orarioConsegna");

	        if (indirizzoConsegna == null || indirizzoConsegna.isBlank() || orarioConsegnaStr == null || orarioConsegnaStr.isBlank()) {
	            redirectAttributes.addFlashAttribute("error", "Inserisci indirizzo e orario di consegna");
	            return "redirect:/user/ordini/" + ordineId + "/edit";
	        }

	        LocalTime orarioConsegna = LocalTime.parse(orarioConsegnaStr);

	        boolean hasProducts = false;
	        List<RigaOrdine> righeAggiornate = new ArrayList<>();

	        for (Product prodotto : pizzeria.getMenu()) {
	            String quantitaParam = "quantita_" + prodotto.getId();
	            String quantitaStr = request.getParameter(quantitaParam);

	            int quantitaProdotto = 0;
	            if (quantitaStr != null && !quantitaStr.isEmpty()) {
	                try {
	                    quantitaProdotto = Integer.parseInt(quantitaStr);
	                } catch (NumberFormatException e) {
	                    quantitaProdotto = 0;
	                }
	            }

	            if (quantitaProdotto > 0) {
	                hasProducts = true;
	                RigaOrdine riga = new RigaOrdine();
	                riga.setProdotto(prodotto);
	                riga.setQuantita(quantitaProdotto);
	                riga.setOrdine(ordine);
	                righeAggiornate.add(riga);
	            }
	        }

	        if (!hasProducts) {
	            redirectAttributes.addFlashAttribute("error", "Devi selezionare almeno un prodotto");
	            return "redirect:/user/ordini/" + ordineId + "/edit";
	        }

	        // Aggiorna dati ordine
	        ordine.setIndirizzoConsegna(indirizzoConsegna);
	        ordine.setOrarioConsegna(orarioConsegna);
	        
	        // Sostituisci le righe ordine con quelle aggiornate
	        ordine.getRigaOrdine().clear();

	        for (RigaOrdine riga : righeAggiornate) {
	            riga.setOrdine(ordine);   // assegna il riferimento bidirezionale
	            ordine.getRigaOrdine().add(riga);
	        }
	        ordineService.saveOrdine(ordine);

	        redirectAttributes.addFlashAttribute("success", "Ordine aggiornato con successo!");
	        return "redirect:/user/ordini/" + ordineId;

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Errore nell'aggiornamento dell'ordine");
	        return "redirect:/user/ordini/" + ordineId + "/edit";
	    }
	}

	
	
}
