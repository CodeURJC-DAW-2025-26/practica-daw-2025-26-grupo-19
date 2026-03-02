package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.EstadisticasEquipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Partido;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.repository.TorneoRepository;
import es.codeurjc.daw.library.repository.PartidoRepository;
import es.codeurjc.daw.library.service.TorneoService;
import es.codeurjc.daw.library.service.EquipoService;
import es.codeurjc.daw.library.service.JugadorService;
import es.codeurjc.daw.library.service.PartidoService;

@Controller
public class WebController {

    @Autowired
    private JavaMailSender mailSender;

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

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private PartidoService partidoService;

    // Global method to determine if the user is logged in to any page
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

        // 2. We look for "ELIMINATORY" type tournaments
        model.addAttribute("copas", torneoRepository.findByTipo("ELIMINATORIA"));

        // 3. Top 5 Players with the Most Goals (Logic in Service)
        model.addAttribute("goleadores", jugadorService.getTop5GoleadoresConPorcentaje());

        // 4. Top 5 Players with the Most Assists (Logic in Service)
        model.addAttribute("asistentes", jugadorService.getTop5AsistentesConPorcentaje());

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
            Model model, @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        // 1. Verify that the passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }

        // 2. Validate that the user does not already exist
        if (equipoRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "register";
        }

        if (image.isEmpty()) {
            model.addAttribute("error", "La foto del equipo es obligatoria");
            return "register";
        }

        if (equipoRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El email ya está en uso.");
            return "register";
        }

        // 3. Create the new user and encrypt the password
        String encodedPassword = passwordEncoder.encode(password);

        // The team is initialized with the "USER" role
        Equipo nuevoEquipo = new Equipo(username, email, encodedPassword, nombreEquipo, "USER");

        byte[] bytes = image.getBytes();
        Blob blob = new SerialBlob(bytes);
        nuevoEquipo.setImagen(blob);
        nuevoEquipo.setHasImagen(true);

        equipoRepository.save(nuevoEquipo);

        return "redirect:/login";
    }

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

    @GetMapping("/profile")
    public String userProfile(Model model, HttpServletRequest request, @RequestParam(required = false) String error) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        // We use the service instead of the repository directly
        Optional<Equipo> equipoOpt = equipoService.findByUsername(username);

        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            if ("dorsal".equals(error)) {
                model.addAttribute("errorDorsal", true);
            }

            if ("image".equals(error)) {
                model.addAttribute("errorImage", true);
            }

            model.addAttribute("username", equipo.getUsername());
            model.addAttribute("email", equipo.getEmail());
            model.addAttribute("nombreEquipo", equipo.getNombreEquipo());
            model.addAttribute("id", equipo.getId());
            model.addAttribute("hasImagen", equipo.isHasImagen());

            // We delegate the ordering logic to the service
            List<Jugador> jugadores = equipoService.getJugadoresOrdenadosPorDorsal(equipo);

            model.addAttribute("jugadores", jugadores);
            model.addAttribute("totalJugadores", jugadores.size());

            return "profile";
        }

        return "redirect:/";
    }

    @PostMapping("/equipo/jugador/nuevo")
    public String nuevoJugador(Model model, HttpServletRequest request, @RequestParam String nombre,
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
        Optional<Equipo> equipoOpt = equipoService.findByUsername(username);
        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            if (equipoService.isDorsalRepetido(equipo, dorsal)) {
                return "redirect:/profile?error=dorsal";
            }
            // If we get here, there are no repeated numbers.

            Jugador nuevoJugador = new Jugador(nombre, posicion, dorsal, equipo);
            byte[] bytes = image.getBytes();
            Blob blob = new SerialBlob(bytes);
            nuevoJugador.setImagen(blob);
            nuevoJugador.setHasImagen(true);
            jugadorRepository.save(nuevoJugador);
        }

        return "redirect:/profile";
    }

@PostMapping("/equipo/jugador/{id}/borrar")
    public String despedirJugador(@PathVariable Long id, HttpServletRequest request) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(id);
        
        if (jugadorOpt.isPresent()) {
            Jugador jugador = jugadorOpt.get();
            
            String currentUsername = request.getUserPrincipal().getName();
            
           
            String duenoDelJugador = jugador.getEquipo().getUsername(); 
            
            boolean isAdmin = request.isUserInRole("ADMIN");
            
            if (!currentUsername.equals(duenoDelJugador) && !isAdmin) {
                return "error"; 
            }
            
            jugadorRepository.delete(jugador);
        }
        
        // Si todo va bien, devolvemos al usuario a su perfil
        return "redirect:/profile";
    }

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

    @GetMapping("/equipo/{id}")
    public String perfilEquipo(@PathVariable Long id, Model model) {
        Optional<Equipo> equipoOpt = equipoRepository.findById(id);

        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            // Datos básicos del equipo
            model.addAttribute("nombreEquipo", equipo.getNombreEquipo());
            model.addAttribute("email", equipo.getEmail()); // Información de contacto
            model.addAttribute("id", equipo.getId());
            model.addAttribute("hasImagen", equipo.isHasImagen());

            // Lista de jugadores
            List<Jugador> jugadores = equipo.getJugadores();
            model.addAttribute("jugadores", jugadores);
            model.addAttribute("totalJugadores", jugadores.size());

            return "equipo-detalle"; // Nueva plantilla HTML que vamos a crear
        }

        // Si el equipo no existe, volvemos a la página principal
        return "redirect:/";
    }

    // Eliminar liga
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
                // Sacamos los bytes del archivo y creamos un SerialBlob estándar
                byte[] bytes = imagen.getBytes();
                Blob blob = new SerialBlob(bytes);
                torneo.setImagen(blob);
                torneo.setHasImagen(true);
            }

            torneoService.save(torneo);
        }

        return "redirect:/admin-dashboard";
    }

    @GetMapping("/torneo/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Torneo> op = torneoService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/equipo/{id}/image")
    public ResponseEntity<Resource> downloadUserImage(@PathVariable long id) throws SQLException {
        Optional<Equipo> op = equipoService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/jugador/{id}/image")
    public ResponseEntity<Resource> downloadPlayerImage(@PathVariable long id) throws SQLException {
        Optional<Jugador> op = jugadorService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity
                    .ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 1. Mostrar la página de equipos
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

    // 2. Eliminar equipo con validación de seguridad
    @PostMapping("/admin/teams/delete")
    public String deleteTeam(@RequestParam Long teamId, HttpServletRequest request) {
        Optional<Equipo> equipoOpt = equipoRepository.findById(teamId);

        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            // Obtenemos el nombre del usuario que está conectado ahora mismo
            String currentUsername = request.getUserPrincipal().getName();

            String teamManager = equipo.getUsername();
            boolean isTargetAdmin = equipo.getRoles().contains("ADMIN");

            // VALIDACIÓN: Si el equipo es tuyo, o el dueño es otro ADMIN, cancelamos el
            // borrado
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

    @PostMapping("/equipo/editar")
    public String editarEquipo(Model model, HttpServletRequest request,
            @RequestParam String nombreEquipo,
            @RequestParam String email,
            @RequestParam("image") MultipartFile image) throws IOException, SQLException {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Optional<Equipo> equipoOpt = equipoService.findByUsername(username);

        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            equipo.setNombreEquipo(nombreEquipo);
            equipo.setEmail(email);

            if (!image.isEmpty()) {
                byte[] bytes = image.getBytes();
                Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
                equipo.setImagen(blob);
                equipo.setHasImagen(true);
            }

            equipoService.save(equipo);
        }

        return "redirect:/profile";
    }

    // Muestra el formulario para pedir el email
@GetMapping("/forgot-password")
public String showForgotPasswordForm() {
    return "forgot-password";
}

@PostMapping("/forgot-password")
public String processForgotPassword(HttpServletRequest request, Model model, @RequestParam String email) {
    String token = UUID.randomUUID().toString();
    try {
        equipoService.updateResetPasswordToken(token, email);
        
        String resetLink = "https://localhost:8443/reset-password?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Enlace para restablecer contraseña de FutbolManager");
        message.setText("Hola,\n\nHas solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para cambiarla:\n" + resetLink);
        
        mailSender.send(message);
        model.addAttribute("message", "Te hemos enviado un enlace a tu correo.");
        
    } catch (Exception e) {
        // ESTO ES CLAVE: Imprimirá el error real en tu consola
        e.printStackTrace(); 
        model.addAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
    }
    return "forgot-password";
}

// Muestra el formulario para escribir la nueva contraseña si el token es válido
@GetMapping("/reset-password")
public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
    Optional<Equipo> equipoOpt = equipoService.getByResetPasswordToken(token);
    if (equipoOpt.isEmpty()) {
        model.addAttribute("error", "Enlace inválido o caducado.");
        return "login"; // Redirigir o mostrar vista de error
    }
    model.addAttribute("token", token);
    return "reset-password";
}

@PostMapping("/reset-password")
public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
    Optional<Equipo> equipoOpt = equipoService.getByResetPasswordToken(token);
    if (equipoOpt.isPresent()) {
        // Usas el passwordEncoder que ya tienes inyectado en WebController
        String encodedPassword = passwordEncoder.encode(password);
        equipoService.updatePassword(equipoOpt.get(), encodedPassword);
        model.addAttribute("message", "Has cambiado tu contraseña exitosamente.");
    } else {
        model.addAttribute("error", "Token inválido.");
    }
    return "login";
}
}