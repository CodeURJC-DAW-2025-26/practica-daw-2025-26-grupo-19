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

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.EstadisticasEquipo;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.service.TorneoService;

@Controller
public class TorneoController {
    @Autowired
    private TorneoService torneoService;

    @Autowired
    private EquipoRepository equipoRepository;

    @GetMapping("/torneo/{id}")
    public String torneoDetalle(@PathVariable Long id, Model model) {
        Optional<Torneo> torneoOpt = torneoService.findById(id);

        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();
            List<EstadisticasEquipo> clasificacion = torneo.getClasificacion();

            model.addAttribute("torneo", torneo);
            model.addAttribute("clasificacion", clasificacion);
            return "torneo";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/torneo/inscribir")
    public String inscribirEquipo(@RequestParam Long torneoId, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null)
            return "redirect:/login";

        String username = principal.getName();
        Optional<Equipo> equipoOpt = equipoRepository.findByUsername(username);
        Optional<Torneo> torneoOpt = torneoService.findById(torneoId);

        if (equipoOpt.isPresent() && torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();
            Equipo equipo = equipoOpt.get();

            // Verify that the tournament is open for registration and is not full
            boolean enInscripcion = "INSCRIPCIONES".equals(torneo.getEstado());
            boolean hayEspacio = torneo.getEquipos().size() < torneo.getMaxParticipantes();
            boolean yaInscrito = torneo.getEquipos().contains(equipo);

            if (enInscripcion && hayEspacio && !yaInscrito) {
                torneo.getEquipos().add(equipo);
                torneoService.save(torneo);
            }
        }
        return "redirect:/";
    }
}
