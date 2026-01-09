package com.airline.airline_app.controller;

import com.airline.airline_app.entity.Client;
import com.airline.airline_app.service.ClientService;
import com.airline.airline_app.specification.ClientSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String nom,
                      @RequestParam(required = false) String prenom,
                      @RequestParam(required = false) String email,
                      Model model) {
        
        Specification<Client> spec = ClientSpecification.filterBy(nom, prenom, email);
        List<Client> clients = clientService.findAll(spec);
        
        model.addAttribute("clients", clients);
        model.addAttribute("nom", nom);
        model.addAttribute("prenom", prenom);
        model.addAttribute("email", email);
        
        return "client/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new Client());
        return "client/form";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        model.addAttribute("client", client);
        return "client/detail";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        model.addAttribute("client", client);
        return "client/form";
    }
    
    @PostMapping
    public String save(@ModelAttribute Client client, RedirectAttributes redirectAttributes) {
        try {
            if (client.getId() == null) {
                clientService.save(client);
                redirectAttributes.addFlashAttribute("success", "Client créé avec succès");
            } else {
                clientService.update(client.getId(), client);
                redirectAttributes.addFlashAttribute("success", "Client modifié avec succès");
            }
            return "redirect:/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/clients/new";
        }
    }
    
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Client supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/clients";
    }
}
