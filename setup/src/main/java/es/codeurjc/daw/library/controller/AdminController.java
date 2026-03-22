package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Match;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.repository.TeamRepository;
import es.codeurjc.daw.library.repository.MatchRepository;
import es.codeurjc.daw.library.service.MatchService;
import es.codeurjc.daw.library.service.TournamentService;

@Controller
public class AdminController {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchService matchService;

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model, HttpServletRequest request) {
        model.addAttribute("torneosAdmin", tournamentService.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/admin/leagues/status")
    public String updateLeagueStatus(@RequestParam Long torneoId, @RequestParam String estado) {
        Optional<Tournament> tournamentOpt = tournamentService.findById(torneoId);
        if (tournamentOpt.isPresent()) {
            Tournament tournament = tournamentOpt.get();

            // If the admin changes to "EN_CURSO" and the tournament was in "INSCRIPCIONES",
            // we generate a calendar
            if ("EN_CURSO".equals(estado) && "INSCRIPCIONES".equals(tournament.getStatus())) {
                tournamentService.generateSchedule(tournament);
            }

            tournament.setStatus(estado);
            tournamentService.save(tournament);
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/leagues/delete")
    public String deleteLeague(@RequestParam Long torneoId) {
        Optional<Tournament> tournamentOpt = tournamentService.findById(torneoId);

        if (tournamentOpt.isPresent()) {
            Tournament tournament = tournamentOpt.get();

            tournament.getTeams().clear();
            tournamentService.save(tournament);

            // Delete the tournament
            tournamentService.delete(torneoId);
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/leagues/new")
    public String createLeague(@RequestParam String nombre,
            @RequestParam int maxParticipantes,
            @RequestParam("imagen") MultipartFile imagen) throws IOException, SQLException {

        // Validate the number of participants
        if (maxParticipantes >= 2 && maxParticipantes <= 20) {
            Tournament tournament = new Tournament(nombre, "LIGA", "INSCRIPCIONES", maxParticipantes);

            // Check if the admin has uploaded an image file
            if (!imagen.isEmpty()) {
                byte[] bytes = imagen.getBytes();
                Blob blob = new SerialBlob(bytes);
                tournament.setImage(blob);
                tournament.setHasImage(true);
            }

            tournamentService.save(tournament);
        }

        return "redirect:/admin-dashboard";
    }

    @GetMapping("/admin/teams")
    public String adminTeams(Model model, HttpServletRequest request) {
        if (!request.isUserInRole("ADMIN")) {
            return "redirect:/";
        }

        // If coming from a failed delete attempt, show the error
        if (request.getParameter("error") != null) {
            model.addAttribute("error",
                    "Acción denegada: No puedes eliminar a un administrador ni a tu propio equipo.");
        }

        model.addAttribute("equipos", teamRepository.findAll());
        return "admin-teams";
    }

    @PostMapping("/admin/teams/delete")
    public String deleteTeam(@RequestParam Long teamId, HttpServletRequest request) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            // Get the currently logged-in username
            String currentUsername = request.getUserPrincipal().getName();

            String teamManager = team.getUsername();
            boolean isTargetAdmin = team.getRoles().contains("ADMIN");

            // VALIDATION: If the team is yours, or the owner is another ADMIN, cancel the deletion
            if (teamManager.equals(currentUsername) || isTargetAdmin) {
                return "redirect:/admin/teams?error=true";
            }

            // If validation passes, unlink from tournaments...
            for (Tournament tournament : team.getTournaments()) {
                tournament.getTeams().remove(team);
                tournamentService.save(tournament);
            }

            teamRepository.delete(team);
        }

        return "redirect:/admin/teams";
    }

    @PostMapping("/admin/match/simulate")
    public String simulateMatch(@RequestParam Long partidoId) {
        Optional<Match> matchOpt = matchRepository.findById(partidoId);

        if (matchOpt.isPresent()) {
            Match match = matchOpt.get();
            matchService.simulateMatch(match);
            return "redirect:/tournament/" + match.getTournament().getId();
        }

        // If there's an error and the match is not found, return to dashboard
        return "redirect:/admin-dashboard";
    }
}
