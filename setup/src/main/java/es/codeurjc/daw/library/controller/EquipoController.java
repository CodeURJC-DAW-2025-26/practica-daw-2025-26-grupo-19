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

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.service.EquipoService;


@Controller
public class EquipoController {
    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorRepository jugadorRepository;

    @GetMapping("/profile")
    public String userProfile(Model model, HttpServletRequest request, @RequestParam(required = false) String error) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
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
        
        return "redirect:/profile";
    }

    @GetMapping("/equipo/{id}")
    public String perfilEquipo(@PathVariable Long id, Model model) {
        Optional<Equipo> equipoOpt = equipoRepository.findById(id);

        if (equipoOpt.isPresent()) {
            Equipo equipo = equipoOpt.get();

            model.addAttribute("nombreEquipo", equipo.getNombreEquipo());
            model.addAttribute("email", equipo.getEmail()); 
            model.addAttribute("id", equipo.getId());
            model.addAttribute("hasImagen", equipo.isHasImagen());

            List<Jugador> jugadores = equipo.getJugadores();
            model.addAttribute("jugadores", jugadores);
            model.addAttribute("totalJugadores", jugadores.size());

            return "equipo-detalle"; 
        }

        return "redirect:/";
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
                Blob blob = new SerialBlob(bytes);
                equipo.setImagen(blob);
                equipo.setHasImagen(true);
            }

            equipoService.save(equipo);
        }

        return "redirect:/profile";
    }
}
