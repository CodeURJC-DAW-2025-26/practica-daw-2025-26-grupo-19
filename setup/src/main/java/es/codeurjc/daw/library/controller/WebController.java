package es.codeurjc.daw.library.controller; // Ajusta el paquete si cambiaste el nombre del proyecto

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.repository.TorneoRepository;

@Controller
public class WebController {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    // Método global para saber si el usuario está logueado en cualquier página
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }


    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("ligas", torneoRepository.findByTipo("LIGA"));
        
        // 2. Buscamos Torneos de tipo "ELIMINATORIA"
        model.addAttribute("copas", torneoRepository.findByTipo("ELIMINATORIA"));
        
        // 3. Top 5 de Jugadores con más goles
        model.addAttribute("goleadores", jugadorRepository.findTop5ByOrderByGolesDesc());
        
        // 4. Top 5 de Jugadores con más asistencias
        model.addAttribute("asistentes", jugadorRepository.findTop5ByOrderByAsistenciasDesc());

        return "index";
        
    }

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/loginerror")
    public String loginerror(Model model) {
        model.addAttribute("error", true);
        return "login"; 
    }

    @GetMapping("/register")
    public String register() {
        return "register"; 
    }
}

   