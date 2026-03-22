package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.TeamStatistics;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.repository.TeamRepository;
import es.codeurjc.daw.library.service.TournamentService;

@Controller
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/tournament/{id}")
    public String tournamentDetail(@PathVariable Long id, Model model) {
        Optional<Tournament> tournamentOpt = tournamentService.findById(id);

        if (tournamentOpt.isPresent()) {
            Tournament tournament = tournamentOpt.get();
            List<TeamStatistics> standings = tournament.getStandings();

            model.addAttribute("torneo", tournament);
            model.addAttribute("clasificacion", standings);
            return "torneo";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/tournament/enroll")
    public String enrollTeam(@RequestParam Long torneoId, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null)
            return "redirect:/login";

        String username = principal.getName();
        Optional<Team> teamOpt = teamRepository.findByUsername(username);
        Optional<Tournament> tournamentOpt = tournamentService.findById(torneoId);

        if (teamOpt.isPresent() && tournamentOpt.isPresent()) {
            Tournament tournament = tournamentOpt.get();
            Team team = teamOpt.get();

            // Verify that the tournament is open for registration and is not full
            boolean inRegistration = "INSCRIPCIONES".equals(tournament.getStatus());
            boolean hasSpace = tournament.getTeams().size() < tournament.getMaxParticipants();
            boolean alreadyEnrolled = tournament.getTeams().contains(team);

            if (inRegistration && hasSpace && !alreadyEnrolled) {
                tournament.getTeams().add(team);
                tournamentService.save(tournament);
            }
        }
        return "redirect:/";
    }
}
