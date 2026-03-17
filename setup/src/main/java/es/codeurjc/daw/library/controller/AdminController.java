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

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Partido;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.repository.PartidoRepository;
import es.codeurjc.daw.library.service.PartidoService;
import es.codeurjc.daw.library.service.TorneoService;

@Controller
public class AdminController {
    @Autowired
    private TorneoService torneoService;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private PartidoService partidoService;

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model, HttpServletRequest request) {
        model.addAttribute("torneosAdmin", torneoService.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/admin/leagues/status")
    public String updateLeagueStatus(@RequestParam Long torneoId, @RequestParam String estado) {
        Optional<Torneo> torneoOpt = torneoService.findById(torneoId);
        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();

            // If the admin changes to "EN_CURSO" and the tournament was in "INSCRIPCIONES",
            // we generate a calendar
            if ("EN_CURSO".equals(estado) && "INSCRIPCIONES".equals(torneo.getEstado())) {
                torneoService.generarCalendario(torneo);
            }

            torneo.setEstado(estado);
            torneoService.save(torneo);
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/leagues/delete")
    public String deleteLeague(@RequestParam Long torneoId) {
        Optional<Torneo> torneoOpt = torneoService.findById(torneoId);

        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();

            torneo.getEquipos().clear();
            torneoService.save(torneo);

            // Borramos definitivamente el torneo
            torneoService.delete(torneoId);
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/leagues/new")
    public String createLeague(@RequestParam String nombre,
            @RequestParam int maxParticipantes,
            @RequestParam("imagen") MultipartFile imagen) throws IOException, SQLException {

        // Validamos el número de participantes
        if (maxParticipantes >= 2 && maxParticipantes <= 20) {
            Torneo torneo = new Torneo(nombre, "LIGA", "INSCRIPCIONES", maxParticipantes);

            // Comprobamos si el administrador ha subido un archivo de imagen
            if (!imagen.isEmpty()) {
                byte[] bytes = imagen.getBytes();
                Blob blob = new SerialBlob(bytes);
                torneo.setImagen(blob);
                torneo.setHasImagen(true);
            }

            torneoService.save(torneo);
        }

        return "redirect:/admin-dashboard";
    }

    @GetMapping("/admin/teams")
    public String adminTeams(Model model, HttpServletRequest request) {
        if (!request.isUserInRole("ADMIN")) {
            return "redirect:/";
        }

        // Si venimos de un intento de borrado fallido, mostramos el error
        if (request.getParameter("error") != null) {
            model.addAttribute("error",
                    "Acción denegada: No puedes eliminar a un administrador ni a tu propio equipo.");
        }

        model.addAttribute("equipos", equipoRepository.findAll());
        return "admin-teams";
    }

    @PostMapping("/admin/teams/delete")
    public String deleteTeam(@RequestParam Long teamId, HttpServletRequest request) {
        Optional<Equipo> equipoOpt = equipoRepository.findById(teamId);

        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            // Obtenemos el nombre del usuario que está conectado ahora mismo
            String currentUsername = request.getUserPrincipal().getName();

            String teamManager = equipo.getUsername();
            boolean isTargetAdmin = equipo.getRoles().contains("ADMIN");

            // VALIDACIÓN: Si el equipo es tuyo, o el dueño es otro ADMIN, cancelamos el borrado
            if (teamManager.equals(currentUsername) || isTargetAdmin) {
                return "redirect:/admin/teams?error=true";
            }

            // Si pasa la validación, desvinculamos de los torneos...
            for (Torneo torneo : equipo.getTorneos()) {
                torneo.getEquipos().remove(equipo);
                torneoService.save(torneo);
            }

            equipoRepository.delete(equipo);
        }

        return "redirect:/admin/teams";
    }

    @PostMapping("/admin/partido/simular")
    public String simularPartido(@RequestParam Long partidoId) {
        Optional<Partido> partidoOpt = partidoRepository.findById(partidoId);

        if (partidoOpt.isPresent()) {
            Partido partido = partidoOpt.get();
            partidoService.simularPartido(partido);
            return "redirect:/torneo/" + partido.getTorneo().getId();
        }

        // Si hay algún error y no se encuentra el partido, vuelve al dashboard
        return "redirect:/admin-dashboard";
    }
}
