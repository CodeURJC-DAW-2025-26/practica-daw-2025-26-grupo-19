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

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.repository.TorneoRepository;

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

        // 2. Validar que el usuario no existe ya (asume que has añadido findByUsername en EquipoRepository)
        if (equipoRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "register";
        }

        // 3. Crear el nuevo usuario y cifrar la contraseña
        String encodedPassword = passwordEncoder.encode(password);
        
        // Se inicializa el Equipo con el ROL de "USER"
        Equipo nuevoEquipo = new Equipo(username, email, encodedPassword, nombreEquipo, "USER");

        // 4. Guardar en la base de datos
        equipoRepository.save(nuevoEquipo);

        // 5. Redirigir al login tras un registro exitoso
        return "redirect:/login";
    }
}

   