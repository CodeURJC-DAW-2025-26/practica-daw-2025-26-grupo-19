package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.daw.library.repository.TorneoRepository;
import es.codeurjc.daw.library.service.JugadorService;

@Controller
public class HomeController {
    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JugadorService jugadorService;

    @GetMapping("/")
    public String index(Model model) {
        // 1. Ligas
        model.addAttribute("ligas", torneoRepository.findByTipo("LIGA"));

        // 2. We look for "ELIMINATORY" type tournaments
        model.addAttribute("copas", torneoRepository.findByTipo("ELIMINATORIA"));

        // 3. Top 5 Players with the Most Goals (Logic in Service)
        model.addAttribute("goleadores", jugadorService.getTop5GoleadoresConPorcentaje());

        // 4. Top 5 Players with the Most Assists (Logic in Service)
        model.addAttribute("asistentes", jugadorService.getTop5AsistentesConPorcentaje());

        return "index";
    }
}
