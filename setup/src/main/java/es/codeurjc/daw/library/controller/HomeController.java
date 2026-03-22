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

import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.service.PlayerService;
import es.codeurjc.daw.library.service.TournamentService;

@Controller
public class HomeController {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/")
    public String index(Model model) {
        // AJAX
        boolean showButton = true;
        int tournamentsShown = 0;
        int numResults = 3;

            // Tournaments shown initially
        List<Tournament> tournaments = tournamentService.getTournaments(tournamentsShown, numResults);

        List<Tournament> allTournaments = tournamentService.findAll();

            // Don't show LoadMore button if there aren't any more leagues
        if(numResults >= allTournaments.size()) {
            showButton = false;
        } 

        model.addAttribute("ligas", tournaments);
        model.addAttribute("showButton", showButton);

        // 3. Top 5 Players with the Most Goals (Logic in Service)
        model.addAttribute("goleadores", playerService.getTop5ScorersWithPercentage());

        // 4. Top 5 Players with the Most Assists (Logic in Service)
        model.addAttribute("asistentes", playerService.getTop5AssistersWithPercentage());

        return "index";
    }

    @GetMapping("/tournaments")
    public String moreLeagues(Model model, @RequestParam int from, @RequestParam int to) {
        List<Tournament> moreTournaments = tournamentService.getTournaments(from, to);

        model.addAttribute("moreTorneos", moreTournaments);

        return("torneos");
    }

    @GetMapping("/showLoadMore")
    @ResponseBody
    public Map<String, Boolean> showLoadMore(@RequestParam int to) {
        List<Tournament> allTournaments = tournamentService.findAll();
        boolean showButton = true;

        if(to >= allTournaments.size()) {
            showButton = false;
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("showButton", showButton);

        return response;
    }
}
