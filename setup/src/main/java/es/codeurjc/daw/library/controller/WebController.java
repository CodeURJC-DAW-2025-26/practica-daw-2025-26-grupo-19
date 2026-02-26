package es.codeurjc.daw.library.controller; // Ajusta el paquete si cambiaste el nombre del proyecto

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;


import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.repository.TorneoRepository;
import es.codeurjc.daw.library.service.TorneoService;

@Controller
public class WebController {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TorneoService torneoService;

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

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String nombreEquipo,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // 1. Validar que las contraseñas coinciden
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        // 2. Validar que el usuario no existe ya
        if (equipoRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "register";
        }

        // 3. Crear el nuevo usuario y cifrar la contraseña
        String encodedPassword = passwordEncoder.encode(password);

        // Se inicializa el Equipo con el ROL de "USER"
        Equipo nuevoEquipo = new Equipo(username, email, encodedPassword, nombreEquipo, "USER");
        equipoRepository.save(nuevoEquipo);

        return "redirect:/login";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model, HttpServletRequest request) {

        model.addAttribute("torneosAdmin", torneoService.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/admin/leagues/new")
    public String createLeague(@RequestParam String nombre, @RequestParam int maxParticipantes) {
        if (maxParticipantes >= 2 && maxParticipantes <= 20) {
            Torneo torneo = new Torneo(nombre, "LIGA", "INSCRIPCIONES", maxParticipantes);
            torneoService.save(torneo);
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/leagues/status")
    public String updateLeagueStatus(@RequestParam Long torneoId, @RequestParam String estado) {
        Optional<Torneo> torneoOpt = torneoService.findById(torneoId);
        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();
            torneo.setEstado(estado);
            torneoService.save(torneo);
        }
        return "redirect:/admin-dashboard";
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

            // Validar que el torneo está en periodo de inscripción y no está lleno
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

@GetMapping("/profile")
public String userProfile(Model model, HttpServletRequest request) {
    Principal principal = request.getUserPrincipal();
    String username = principal.getName();
    
    Optional<Equipo> equipoOpt = equipoRepository.findByUsername(username);

    if (equipoOpt.isPresent()) {
        Equipo equipo = equipoOpt.get();

        model.addAttribute("username", equipo.getUsername());
        model.addAttribute("email", equipo.getEmail());
        model.addAttribute("nombreEquipo", equipo.getNombreEquipo());

        List<Jugador> jugadores = equipo.getJugadores();
        model.addAttribute("jugadores", jugadores);
        model.addAttribute("totalJugadores", jugadores.size());
        
        // Comprobamos si el equipo tiene escudo para pasarlo en Base64 al HTML
        if (equipo.getEscudo() != null) {
            model.addAttribute("hasEscudo", true);
            String base64Image = Base64.getEncoder().encodeToString(equipo.getEscudo());
            model.addAttribute("escudoBase64", base64Image);
        } else {
            model.addAttribute("hasEscudo", false);
        }

        return "profile"; 
    }

    // Si por algún motivo el equipo no existe en BD, lo mandamos al inicio
    return "redirect:/";
}

    @PostMapping("/equipo/jugador/nuevo")
    public String nuevoJugador(HttpServletRequest request, @RequestParam String nombre, @RequestParam String posicion, @RequestParam int dorsal) {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }
        // 2. Buscamos el equipo del usuario que ha iniciado sesión
        String username = principal.getName();
        Optional<Equipo> equipoOpt = equipoRepository.findByUsername(username);

        if (equipoOpt.isPresent()) {
            Equipo equipo = (Equipo) equipoOpt.get();

            Jugador nuevoJugador = new Jugador(nombre, posicion, dorsal, equipo);

            jugadorRepository.save(nuevoJugador);
        }

        return "redirect:/profile";
    }

    @GetMapping("/torneo/{id}")
    public String torneoDetalle(@PathVariable Long id, Model model) {
        Optional<Torneo> torneoOpt = torneoService.findById(id);
        
        if (torneoOpt.isPresent()) {
            model.addAttribute("torneo", torneoOpt.get());
            return "torneo"; // Nombre de la nueva plantilla HTML
        } else {
            // Si el torneo no existe, redirigimos al inicio
            return "redirect:/"; 
        }
    }

    

}
