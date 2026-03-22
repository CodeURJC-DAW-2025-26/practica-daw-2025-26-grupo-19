package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.repository.TeamRepository;
import es.codeurjc.daw.library.repository.PlayerRepository;
import es.codeurjc.daw.library.service.TeamService;


@Controller
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/profile")
    public String userProfile(Model model, HttpServletRequest request, @RequestParam(required = false) String error) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Optional<Team> teamOpt = teamService.findByUsername(username);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            if ("dorsal".equals(error)) {
                model.addAttribute("errorDorsal", true);
            }

            if ("image".equals(error)) {
                model.addAttribute("errorImage", true);
            }

            model.addAttribute("username", team.getUsername());
            model.addAttribute("email", team.getEmail());
            model.addAttribute("nombreEquipo", team.getTeamName());
            model.addAttribute("id", team.getId());
            model.addAttribute("hasImagen", team.isHasImage());

            List<Player> players = teamService.getPlayersSortedByJerseyNumber(team);

            model.addAttribute("jugadores", players);
            model.addAttribute("totalJugadores", players.size());

            return "profile";
        }

        return "redirect:/";
    }

    @PostMapping("/team/player/new")
    public String newPlayer(Model model, HttpServletRequest request, @RequestParam String nombre,
            @RequestParam String posicion,
            @RequestParam int dorsal, @RequestParam("image") MultipartFile image) throws IOException, SQLException {
        
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }
        
        if (image.isEmpty()) {
            model.addAttribute("error", "La foto del jugador es obligatoria.");
            return "redirect:/profile?error=image";
        }
        
        String username = principal.getName();
        Optional<Team> teamOpt = teamService.findByUsername(username);
        
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            if (teamService.isDuplicateJerseyNumber(team, dorsal)) {
                return "redirect:/profile?error=dorsal";
            }

            Player newPlayer = new Player(nombre, posicion, dorsal, team);
            byte[] bytes = image.getBytes();
            Blob blob = new SerialBlob(bytes);
            newPlayer.setImage(blob);
            newPlayer.setHasImage(true);
            playerRepository.save(newPlayer);
        }

        return "redirect:/profile";
    }

    @PostMapping("/team/player/{id}/delete")
    public String removePlayer(@PathVariable Long id, HttpServletRequest request) {
        Optional<Player> playerOpt = playerRepository.findById(id);
        
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            String currentUsername = request.getUserPrincipal().getName();
            String playerOwner = player.getTeam().getUsername(); 
            boolean isAdmin = request.isUserInRole("ADMIN");
            
            if (!currentUsername.equals(playerOwner) && !isAdmin) {
                return "error"; 
            }
            
            playerRepository.delete(player);
        }
        
        return "redirect:/profile";
    }

    @GetMapping("/team/{id}")
    public String teamProfile(@PathVariable Long id, Model model) {
        Optional<Team> teamOpt = teamRepository.findById(id);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            model.addAttribute("nombreEquipo", team.getTeamName());
            model.addAttribute("email", team.getEmail()); 
            model.addAttribute("id", team.getId());
            model.addAttribute("hasImagen", team.isHasImage());

            List<Player> players = team.getPlayers();
            model.addAttribute("jugadores", players);
            model.addAttribute("totalJugadores", players.size());

            return "equipo-detalle"; 
        }

        return "redirect:/";
    }

    @PostMapping("/team/edit")
    public String editTeam(Model model, HttpServletRequest request,
            @RequestParam String nombreEquipo,
            @RequestParam String email,
            @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Optional<Team> teamOpt = teamService.findByUsername(username);

        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();

            team.setTeamName(nombreEquipo);
            team.setEmail(email);

            if (!image.isEmpty()) {
                byte[] bytes = image.getBytes();
                Blob blob = new SerialBlob(bytes);
                team.setImage(blob);
                team.setHasImage(true);
            }

            teamService.save(team);
        }

        return "redirect:/profile";
    }
}
