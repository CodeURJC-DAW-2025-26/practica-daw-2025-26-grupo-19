package es.codeurjc.daw.library.controller; // Ajusta el paquete si cambiaste el nombre del proyecto

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
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
import es.codeurjc.daw.library.service.TorneoService;
import es.codeurjc.daw.library.service.EquipoService;
import es.codeurjc.daw.library.service.JugadorService;

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

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorService jugadorService;

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
            Model model, @RequestParam("image") MultipartFile image) throws IOException, SQLException {

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

        if (image.isEmpty()) {
            model.addAttribute("error", "La foto del equipo es obligatoria");
            return "register";
        }

        // 3. Crear el nuevo usuario y cifrar la contraseña
        String encodedPassword = passwordEncoder.encode(password);

        // Se inicializa el Equipo con el ROL de "USER"
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
    public String userProfile(Model model, HttpServletRequest request, @RequestParam(required = false) String error) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        // Usamos el servicio en lugar del repositorio directamente
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

            // Delegamos la lógica de ordenación al servicio
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
            // Si llegamos aqui no hay dorsal repetido

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
    public String borrarJugador(@PathVariable Long id) {
        Optional<Jugador> jugadorOpt = jugadorRepository.findById(id);
        if (jugadorOpt.isPresent()) {
            jugadorRepository.delete(jugadorOpt.get());
        }
        return "redirect:/profile";
    }

    @GetMapping("/torneo/{id}")
    public String torneoDetalle(@PathVariable Long id, Model model) {
        Optional<Torneo> torneoOpt = torneoService.findById(id);

        if (torneoOpt.isPresent()) {
            Torneo torneo = torneoOpt.get();

            // Lista donde guardaremos las estadísticas de cada equipo
            List<EstadisticasEquipo> clasificacion = new ArrayList<>();

            for (Equipo equipo : torneo.getEquipos()) {
                EstadisticasEquipo stats = new EstadisticasEquipo(equipo);

                for (Partido partido : torneo.getPartidos()) {
                    // Solo contamos los partidos que ya se han jugado
                    if (partido.isJugado()) {
                        boolean esLocal = partido.getEquipoLocal().getId().equals(equipo.getId());
                        boolean esVisitante = partido.getEquipoVisitante().getId().equals(equipo.getId());

                        // Si el equipo participó en este partido
                        if (esLocal || esVisitante) {
                            stats.setJugados(stats.getJugados() + 1);

                            // Detectamos cuántos goles metió y cuántos le metieron
                            int golesFavor = esLocal ? partido.getGolesLocal() : partido.getGolesVisitante();
                            int golesContra = esLocal ? partido.getGolesVisitante() : partido.getGolesLocal();

                            stats.setGolesFavor(stats.getGolesFavor() + golesFavor);
                            stats.setGolesContra(stats.getGolesContra() + golesContra);

                            // Calculamos puntos y resultados (3 pts victoria, 1 pt empate)
                            if (golesFavor > golesContra) {
                                stats.setVictorias(stats.getVictorias() + 1);
                                stats.setPuntos(stats.getPuntos() + 3);
                            } else if (golesFavor == golesContra) {
                                stats.setEmpates(stats.getEmpates() + 1);
                                stats.setPuntos(stats.getPuntos() + 1);
                            } else {
                                stats.setDerrotas(stats.getDerrotas() + 1);
                            }
                        }
                    }
                }
                clasificacion.add(stats);
            }

            // Ordenamos la clasificación: primero el que tenga más Puntos, luego Diferencia
            // de Goles
            clasificacion.sort((a, b) -> {
                if (a.getPuntos() != b.getPuntos()) {
                    return Integer.compare(b.getPuntos(), a.getPuntos()); // Mayor a menor
                } else {
                    return Integer.compare(b.getDiferenciaGoles(), a.getDiferenciaGoles());
                }
            });

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

            // Lista de jugadores
            List<Jugador> jugadores = equipo.getJugadores();
            model.addAttribute("jugadores", jugadores);
            model.addAttribute("totalJugadores", jugadores.size());

            return "equipo-detalle"; // Nueva plantilla HTML que vamos a crear
        }

        // Si el equipo no existe, volvemos a la página principal
        return "redirect:/";
    }

    // NUEVO: Eliminar liga
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
}
