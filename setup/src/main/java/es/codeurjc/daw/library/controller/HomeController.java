package es.codeurjc.daw.library.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.service.JugadorService;
import es.codeurjc.daw.library.service.TorneoService;

@Controller
public class HomeController {
    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/")
    public String index(Model model) {
        // AYAX
        boolean showButton = true;
        int torneosShown = 0;
        int numResults = 3;

            // Torneos shown initially
        List<Torneo> torneos = torneoService.getTorneos(torneosShown, numResults);

        List<Torneo> totalTorneos = torneoService.findAll();

            // Not show button LoadMore if there arent left anymore leagues
        if(numResults >= totalTorneos.size()) {
            showButton = false;
        } 

        model.addAttribute("ligas", torneos);
        model.addAttribute("showButton", showButton);

        // 3. Top 5 Players with the Most Goals (Logic in Service)
        model.addAttribute("goleadores", jugadorService.getTop5GoleadoresConPorcentaje());

        // 4. Top 5 Players with the Most Assists (Logic in Service)
        model.addAttribute("asistentes", jugadorService.getTop5AsistentesConPorcentaje());

        return "index";
    }

    @GetMapping("/torneos")
    public String moreLeagues(Model model, @RequestParam int from, @RequestParam int to) {
        List<Torneo> moreTorneos = torneoService.getTorneos(from, to);

        model.addAttribute("moreTorneos", moreTorneos);

        return("torneos");
    }

    @GetMapping("/showLoadMore")
    @ResponseBody
    public Map<String, Boolean> showLoadMore(@RequestParam int to) {
        List<Torneo> totalTorneos = torneoService.findAll();
        boolean showButton = true;

        if(to >= totalTorneos.size()) {
            showButton = false;
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("showButton", showButton);

        return response;
    }
}
