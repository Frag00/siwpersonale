package it.uniroma3.siw.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Ordine;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.model.Pizzeria;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.RigaOrdine;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.OrdineService;
import it.uniroma3.siw.service.PictureService;
import it.uniroma3.siw.service.PizzeriaService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.RigaOrdineService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class PizzeriaController {

	@Autowired PizzeriaService pizzeriaService;
	@Autowired ProductService productService;
	@Autowired PictureService pictureService;
	@Autowired UserService userService;
	@Autowired RigaOrdineService rigaOrdineService;
	@Autowired ReviewService reviewService;
	@Autowired OrdineService ordineService;
	
	
	@GetMapping("/pizzerie")
	public String showPizzerie(Model model) {
		model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
		return "pizzerie.html";
	}
	
	@GetMapping("/pizzerie/{id}")
	public String showPizzeria(@PathVariable("id") Long id ,Model model) {
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		return "pizzeria.html";
	}
	
	@GetMapping("/pizzerie/{id}/menu")
	public String showMenu(@PathVariable("id") Long id , Model model) {
		
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		model.addAttribute("bevande", productService.getProductsByTipoAndPizzeria("Bevanda", pizzeriaService.getPizzeriaById(id).get()));
		model.addAttribute("fritti", productService.getProductsByTipoAndPizzeria("Fritto", pizzeriaService.getPizzeriaById(id).get()));
		model.addAttribute("pizze", productService.getProductsByTipoAndPizzeria("Pizza", pizzeriaService.getPizzeriaById(id).get()));
		return "menu.html";
	}
	
	/* 	ADMIN ***************************/
	@GetMapping("/admin/pizzerie")
	public String showAdminPizzerie(Model model) {
		model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
		return "admin/pizzerie.html";
	}
	
	@GetMapping("/admin/pizzerie/{id}")
	public String showAdminPizzeria(@PathVariable("id") Long id ,Model model) {
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		return "admin/pizzeria.html";
	}
	
	@GetMapping("/admin/pizzerie/{id}/formNewProduct")
	public String showAdminFormNewProduct(@PathVariable("id") Long id,Model model) {
		model.addAttribute("prodotto", new Product());
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		return "admin/formNewProduct";
	}
	
	@PostMapping("/admin/pizzerie/{id}/addProduct")
	public String adminAddsProductToPizzeria(@Valid @ModelAttribute("prodotto") Product prodotto, BindingResult bindingResult,@PathVariable("id") Long idP, @RequestParam("imageFile") MultipartFile imageFile,Model model) {
		if(bindingResult.hasErrors()) {
			System.out.println("Binding error: " + bindingResult);
			model.addAttribute("prodotto", prodotto);
			model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
			return "admin/formNewProduct.html";
		}
		else if(productService.existsByNomeAndPizzeria(prodotto.getNome(),pizzeriaService.getPizzeriaById(idP).get())) {
			model.addAttribute("errEsiste","Prodotto già presente nel menu di questa pizzeria");
			model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
			return "admin/formNewProduct.html";
		}
		else{
	/* gestione delle immagini */
	        
	        if(imageFile==null || imageFile.isEmpty()) {
	        	model.addAttribute("errIMG", "Errore con l'immagine");
	        	model.addAttribute("prodotto",prodotto);
	    		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
	            return "admin/formNewProduct.html";
	        }

	        try {
	            // Gestione immagini
	               	String name = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
	                Picture img = new Picture(name);
	                this.pictureService.savePhysicalImage(imageFile.getBytes(), name);	               	               
	        // Associamo la foto all'autore
	        prodotto.setImmagine(img);
	        
		Pizzeria corrente = pizzeriaService.getPizzeriaById(idP).get();
	        
	    prodotto.setPizzeria(corrente);

	    prodotto.setId(null);
		productService.saveProduct(prodotto);
		
		corrente.getMenu().add(prodotto);
		pizzeriaService.savePizzeria(corrente);
		
		model.addAttribute("pizzeria", corrente);
		return "redirect:/admin/pizzerie/" + idP + "/menu";
		}
	        catch (Exception e) {
	        	e.printStackTrace();
	    		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
	            return "admin/formNewProduct.html";
	        }
		
		}
	}
	
	
	
	@GetMapping("/admin/pizzerie/{id}/menu")
	public String showAdminMenu(@PathVariable("id") Long id , Model model) {
		
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		model.addAttribute("bevande", productService.getProductsByTipoAndPizzeria("Bevanda", pizzeriaService.getPizzeriaById(id).get()));
		model.addAttribute("fritti", productService.getProductsByTipoAndPizzeria("Fritto", pizzeriaService.getPizzeriaById(id).get()));
		model.addAttribute("pizze", productService.getProductsByTipoAndPizzeria("Pizza", pizzeriaService.getPizzeriaById(id).get()));
		return "admin/menu.html";
	}
	
	@GetMapping("/admin/formNewPizzeria")
	public String showAdminFormPizzeria(Model model) {
		model.addAttribute("pizzeria", new Pizzeria());
		return "admin/formNewPizzeria.html";
	}
	
	@PostMapping("/admin/addPizzeria")
	public String adminAddsPizzeria(@Valid @ModelAttribute("pizzeria") Pizzeria pizzeria,BindingResult bindingResult,@RequestParam("imageFiles") MultipartFile[] imageFiles,Model model) {
		if(this.pizzeriaService.existsByNomeAndIndirizzo(pizzeria)) {
			model.addAttribute("errEsiste","Pizzeria già presente");
			return "admin/formNewPizzeria.html";
		}
		else if(bindingResult.hasErrors()) {
			return "admin/formNewPizzeria.html";
		}
		
		else {
	        
	        /* gestione delle immagini */
	        
	        if(imageFiles.length == 0 || (imageFiles.length == 1 && imageFiles[0].isEmpty())) {
	        	model.addAttribute("pizzeria",pizzeria);
	            return "admin/formNewPizzeria.html";
	        }

	        try {
	            // Gestione immagini
	            Set<Picture> images = new HashSet<Picture>();
	            for (MultipartFile file : imageFiles) {
	                if (!file.isEmpty()) {
	                	String name = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	                    Picture img = new Picture(name);
	                    this.pictureService.savePhysicalImage(file.getBytes(), name);
	                    images.add(img);
	                }
	            }

	        // Associamo le immagini alla pizzeria
	        pizzeria.setImmagini(images);
	        
		this.pizzeriaService.savePizzeria(pizzeria);
		model.addAttribute("pizzeria",pizzeria);
		return "redirect:/admin/pizzerie/"+pizzeria.getId();
	      }
	        
	        catch (Exception e) {
	            return "admin/formNewPizzeria.html";
	        }
		}
	}
	
	/*
	@GetMapping("/admin/pizzerie/{idP}/delete")
	public String adminDeletesPizzeria(@PathVariable("idP") Long idP, Model model) {
		
		Pizzeria p = pizzeriaService.getPizzeriaById(idP).get();
		
		// rimuovo prima tutti i legami che avevano i campi della pizzeria con altre entità
		
		for(Review r : p.getRecensioni()) {
			User u = r.getUtente();
			if(u!=null) {
				u.getRecensioni().remove(r);
				userService.saveUser(u);
			}
			r.setUtente(null); 
			r.setPizzeria(null);
			
			reviewService.deleteReview(r);
		}
		
		for(Ordine o : p.getOrdini()) {
			User u = o.getUtente();
			if(u!=null) {
				u.getOrdini().remove(o);
				userService.saveUser(u);
			}
			// Elimina righe ordine associate
		    for(RigaOrdine ro : o.getRigaOrdine()) {
		        ro.setOrdine(null);
		        ro.setProdotto(null);
		        rigaOrdineService.save(ro);
		        rigaOrdineService.delete(ro);  
		    }

		    o.setUtente(null);
		    o.setPizzeria(null);
		    ordineService.saveOrdine(o);
		    ordineService.deleteOrdine(o);
		}
		
		//rimuovo le immagini dal progetto
		for(Picture pic : p.getImmagini()) {
			if(pic!=null)
				pictureService.deletePhysicalImage(pic);
		}
		
		for (Product prod : p.getMenu()) {
		    List<RigaOrdine> righe = rigaOrdineService.findByProdotto(prod);
		    for (RigaOrdine ro : righe) {
		        ro.setProdotto(null);
		        ro.setOrdine(null);
		        rigaOrdineService.save(ro);
		        rigaOrdineService.delete(ro);
		    }
		}
		
		for(Product prod : p.getMenu()) {
			if(prod!=null)
				if(prod.getImmagine() != null)
			        pictureService.deletePhysicalImage(prod.getImmagine());
			    
			    prod.setPizzeria(null); 
			    productService.saveProduct(prod);
			    productService.deleteProduct(prod); 
		}
		
	
	
		pizzeriaService.deletePizzeria(p);
		
		model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
		return "redirect:/admin/pizzerie";
	}*/
	
	@Transactional
	@GetMapping("/admin/pizzerie/{idP}/delete")
	public String adminDeletesPizzeria(@PathVariable("idP") Long idP, Model model) {
	    Pizzeria p = pizzeriaService.getPizzeriaById(idP).get();

	    // 1. Rimuovo righe ordine da ogni prodotto del menu
	    for (Product prod : p.getMenu()) {
	        List<RigaOrdine> righe = rigaOrdineService.findByProdotto(prod);
	        for (RigaOrdine ro : righe) {
	            ro.setProdotto(null);
	            ro.setOrdine(null);
	            rigaOrdineService.delete(ro);
	        }
	    }

	    // 2. Rimuovo prodotti e le immagini associate
	    for (Product prod : p.getMenu()) {
	        if (prod.getImmagine() != null)
	            pictureService.deletePhysicalImage(prod.getImmagine());
	        prod.setPizzeria(null);
	        productService.deleteProduct(prod);
	    }

	    // 3. Rimuovo gli ordini e le loro righe
	    for (Ordine o : p.getOrdini()) {
	        User u = o.getUtente();
	        if (u != null) {
	            u.getOrdini().remove(o);
	            userService.saveUser(u);
	        }
	        for (RigaOrdine ro : o.getRigaOrdine()) {
	            ro.setOrdine(null);
	            ro.setProdotto(null);
	            rigaOrdineService.delete(ro);
	        }
	        o.setUtente(null);
	        o.setPizzeria(null);
	        ordineService.deleteOrdine(o);
	    }

	    // 4. Rimuovo le recensioni
	    for (Review r : p.getRecensioni()) {
	        User u = r.getUtente();
	        if (u != null) {
	            u.getRecensioni().remove(r);
	            userService.saveUser(u);
	        }
	        r.setUtente(null);
	        r.setPizzeria(null);
	        reviewService.deleteReview(r);
	    }

	    // 5. Rimuovo immagini della pizzeria
	    for (Picture pic : p.getImmagini()) {
	        if (pic != null)
	            pictureService.deletePhysicalImage(pic);
	    }

	    // 6. Elimina la pizzeria
	    pizzeriaService.deletePizzeria(p);

	    model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
	    return "redirect:/admin/pizzerie";
	}
	
	
	@GetMapping("/admin/pizzerie/{idP}/menu/{idProd}/remove")
	public String adminRemovesProductFromPizzeria(@PathVariable("idP") Long idPizzeria, @PathVariable("idProd") Long idProd, Model model) {
		
		Product p = productService.getProductById(idProd);
		
		Pizzeria pizzeria = pizzeriaService.getPizzeriaById(idPizzeria).get();
		
		
		pizzeria.getMenu().remove(p);
		
		//Rimuovo il prodotto da tutte le righe ordine che lo usano
	    List<RigaOrdine> righe = rigaOrdineService.findByProdotto(p);
	    for (RigaOrdine riga : righe) {
	        riga.setProdotto(null); // disaccoppia
	        rigaOrdineService.delete(riga); 
	    }
		
		//elimino il file dal progetto
		if(p.getImmagine()!=null)
			pictureService.deletePhysicalImage(p.getImmagine());
		
		productService.deleteProduct(p);
		
		return "redirect:/admin/pizzerie/" + idPizzeria + "/menu";
	}
	
	@GetMapping("/admin/pizzerie/{idP}/menu/{idPr}/formEditProduct")
	public String showAdminFormEditProduct(@PathVariable("idP") Long idP, @PathVariable("idPr") Long idPr, Model model) {
		
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
		model.addAttribute("prodotto", productService.getProductById(idPr));
		// per cambiarle dopo 
		model.addAttribute("foto", productService.getProductById(idPr).getImmagine());
		return "admin/formEditProduct.html";
	}
	
	
	@PostMapping("/admin/pizzerie/{idP}/menu/{idPr}/editProduct")
	public String adminModifiesProductOfPizzeria(@Valid @ModelAttribute("prodotto") Product prodotto, BindingResult bindingResult,@PathVariable("idP") Long idP, @PathVariable("idPr") Long idPr, @RequestParam("imageFile") MultipartFile imageFile, @ModelAttribute("foto") Picture foto, Model model) {
		if(bindingResult.hasErrors()) {
			System.out.println("Binding error: " + bindingResult);
			model.addAttribute("prodotto", prodotto);
			model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
			model.addAttribute("foto", productService.getProductById(idPr).getImmagine());
			return "admin/formEditProduct.html";
		}
		
		else{
			Product pr = productService.getProductById(idPr);
			
	/* gestione delle immagini */
	        
	        if(imageFile==null || imageFile.isEmpty()) {
	        	model.addAttribute("errIMG", "Errore con l'immagine");
	        	model.addAttribute("prodotto",prodotto);
	    		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
	    		model.addAttribute("foto", productService.getProductById(idPr).getImmagine());
	            return "admin/formEditProduct.html";
	        }

	        try {
	            // Gestione immagini
	               	String name = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
	                Picture img = new Picture(name);
	                this.pictureService.savePhysicalImage(imageFile.getBytes(), name);	               	               
	        // Associamo la foto all'autore
	        pr.setImmagine(img);
	        pr.setNome(prodotto.getNome());
	        pr.setDescrizione(prodotto.getDescrizione());
	        pr.setPrezzo(prodotto.getPrezzo());
	        pr.setTipo(prodotto.getTipo());
	        
	        if(foto!=null)
	        	pictureService.deletePhysicalImage(foto);
	        
	    productService.saveProduct(pr);
		
		
		pizzeriaService.savePizzeria(pr.getPizzeria());
		
		model.addAttribute("pizzeria", pr.getPizzeria());
		return "redirect:/admin/pizzerie/" + idP + "/menu";
		}
	        catch (Exception e) {
	        	e.printStackTrace();
	    		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(idP).get());
	    		model.addAttribute("foto", productService.getProductById(idPr).getImmagine());
	            return "admin/formEditProduct.html";
	        }
		
		}
	}
	
	@GetMapping("/admin/pizzerie/{idP}/formEditPizzeria")
	public String showAdminFormEditPizzeria(@PathVariable("idP") Long idP, Model model) {
		
		model.addAttribute("Pics", pizzeriaService.getPizzeriaById(idP).get().getImmagini());
		model.addAttribute("pizzeria",pizzeriaService.getPizzeriaById(idP).get());
		
		return "admin/formEditPizzeria.html";
	}
	
	@PostMapping("/admin/pizzerie/{pId}/editPizzeria")
	public String adminEditsPizzeria(@Valid @ModelAttribute("pizzeria") Pizzeria pizzeria,BindingResult bindingResult,@RequestParam("imageFiles") MultipartFile[] imageFiles, @PathVariable("pId") Long idP, @ModelAttribute("Pics") Set<Picture> pics, Model model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("Pics", pizzeriaService.getPizzeriaById(idP).get().getImmagini());
			pizzeria.setId(idP);
			model.addAttribute("pizzeria",pizzeria);
			return "admin/formEditPizzeria.html";
		}
		
		else {
	        
			Pizzeria pi = pizzeriaService.getPizzeriaById(idP).get();
			
			Set<Picture> oldImages = new HashSet<>(pi.getImmagini());
			
	        /* gestione delle immagini */
	        
	        if(imageFiles.length == 0 || (imageFiles.length == 1 && imageFiles[0].isEmpty())) {
	        	pizzeria.setId(idP);
				model.addAttribute("pizzeria",pizzeria);
	        	model.addAttribute("Pics", pizzeriaService.getPizzeriaById(idP).get().getImmagini());
	            return "admin/formEditPizzeria.html";
	        }

	        try {
	            // Gestione immagini
	            Set<Picture> images = new HashSet<Picture>();
	            for (MultipartFile file : imageFiles) {
	                if (!file.isEmpty()) {
	                	String name = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	                    Picture img = new Picture(name);
	                    this.pictureService.savePhysicalImage(file.getBytes(), name);
	                    images.add(img);
	                }
	            }

	        // Associamo le immagini alla pizzeria
	        pi.setImmagini(images);
	        pi.setCitta(pizzeria.getCitta());
	        pi.setIndirizzo(pizzeria.getIndirizzo());
	        pi.setNome(pizzeria.getNome());
	      
	        
	     // elimino le vecchie immagini
			for(Picture foto : oldImages) {
				if(foto!=null)
					pictureService.deletePhysicalImage(foto);
			}    
	        
		this.pizzeriaService.savePizzeria(pi);
		
		
		
		model.addAttribute("pizzeria",pi);
		return "redirect:/admin/pizzerie/"+ pi.getId();
	      }
	        
	        catch (Exception e) {
	        	model.addAttribute("Pics", pizzeriaService.getPizzeriaById(idP).get().getImmagini());
	        	pizzeria.setId(idP);
				model.addAttribute("pizzeria",pizzeria);
	        	return "admin/formEditPizzeria.html";
	        }
		}
	}
	
	
	
	
	
	/* 	USER ***************************/
	@GetMapping("/user/pizzerie")
	public String showUserPizzerie(Model model) {
		model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
		return "user/pizzerie.html";
	}
	
	@GetMapping("/user/pizzerie/{id}")
	public String showUserPizzeria(@PathVariable("id") Long id ,Model model) {
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		return "user/pizzeria.html";
	}
	
	@GetMapping("/user/pizzerie/{id}/menu")
	public String showUserMenu(@PathVariable("id") Long id , Model model) {
		
		model.addAttribute("pizzeria", pizzeriaService.getPizzeriaById(id).get());
		model.addAttribute("bevande", productService.getProductsByTipoAndPizzeria("Bevanda", pizzeriaService.getPizzeriaById(id).get()));
		model.addAttribute("fritti", productService.getProductsByTipoAndPizzeria("Fritto", pizzeriaService.getPizzeriaById(id).get()));
		model.addAttribute("pizze", productService.getProductsByTipoAndPizzeria("Pizza", pizzeriaService.getPizzeriaById(id).get()));
		return "user/menu.html";
	}
	
	/* gestione della ricerca delle pizzerie */
	
	// utente qualsiasi
	@GetMapping("/pizzerie/search")
	public String searchPizzerie(@RequestParam("ricerca") String ricerca, Model model) {
	    
		
	    if (ricerca != null && !ricerca.isEmpty()) {
	    	// stringa di ricerca non vuota
	        model.addAttribute("pizzerie", pizzeriaService.searchPizzerie(ricerca));
	        model.addAttribute("ricerca", ricerca);
	    } else {
	        // Se la stringa di ricerca è vuota allora mostra tutte le pizzerie
	       
	        model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
	    }
	    
	    return "pizzerie.html";
	}
	
	//utenti registrati 
	@GetMapping("/user/pizzerie/search")
	public String userSearchPizzerie(@RequestParam("ricerca") String ricerca, Model model) {
	    
		
	    if (ricerca != null && !ricerca.isEmpty()) {
	    	// stringa di ricerca non vuota
	        model.addAttribute("pizzerie", pizzeriaService.searchPizzerie(ricerca));
	        model.addAttribute("ricerca", ricerca);
	    } else {
	        // Se la stringa di ricerca è vuota allora mostra tutte le pizzerie
	       
	        model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
	    }
	    
	    return "user/pizzerie.html";
	}
	
	//admin
	@GetMapping("/admin/pizzerie/search")
	public String adminSearchPizzerie(@RequestParam("ricerca") String ricerca, Model model) {
	    
		
	    if (ricerca != null && !ricerca.isEmpty()) {
	    	// stringa di ricerca non vuota
	        model.addAttribute("pizzerie", pizzeriaService.searchPizzerie(ricerca));
	        model.addAttribute("ricerca", ricerca);
	    } else {
	        // Se la stringa di ricerca è vuota allora mostra tutte le pizzerie
	       
	        model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
	    }
	    
	    return "admin/pizzerie.html";
	}
	
}
