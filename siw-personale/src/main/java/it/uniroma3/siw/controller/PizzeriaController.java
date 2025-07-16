package it.uniroma3.siw.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.PictureService;
import it.uniroma3.siw.service.PizzeriaService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class PizzeriaController {

	@Autowired PizzeriaService pizzeriaService;
	@Autowired ProductService productService;
	@Autowired PictureService pictureService;
	@Autowired UserService userService;
	
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
		}
		
		for(Ordine o : p.getOrdini()) {
			User u = o.getUtente();
			if(u!=null) {
				u.getOrdini().remove(o);
				userService.saveUser(u);
			}
		}
		
		//rimuovo le immagini dal progetto
		for(Picture pic : p.getImmagini()) {
			if(pic!=null)
				pictureService.deletePhysicalImage(pic);
		}
		
		for(Product prod : p.getMenu()) {
			if(prod!=null)
				if(prod.getImmagine()!=null)
					pictureService.deletePhysicalImage(prod.getImmagine());
		}
	
		pizzeriaService.deletePizzeria(p);
		
		model.addAttribute("pizzerie", pizzeriaService.getAllPizzerie());
		return "redirect:/admin/pizzerie";
	}
	
	@GetMapping("/admin/pizzerie/{idP}/menu/{idProd}/remove")
	public String adminRemovesProductFromPizzeria(@PathVariable("idP") Long idPizzeria, @PathVariable("idProd") Long idProd, Model model) {
		
		Product p = productService.getProductById(idProd);
		
		Pizzeria pizzeria = pizzeriaService.getPizzeriaById(idPizzeria).get();
		
		
		pizzeria.getMenu().remove(p);
		
		//elimino il file dal progetto
		pictureService.deletePhysicalImage(p.getImmagine());
		
		productService.deleteProduct(p);
		
		return "redirect:/admin/pizzerie/" + idPizzeria + "/menu";
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
}
