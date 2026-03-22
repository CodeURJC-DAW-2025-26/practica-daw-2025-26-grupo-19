package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.dto.JugadorBasicDTO;
import es.codeurjc.daw.library.dto.JugadorDTO;
import es.codeurjc.daw.library.dto.JugadorMapper;
import es.codeurjc.daw.library.dto.TorneoBasicDTO;
import es.codeurjc.daw.library.service.JugadorService;
import es.codeurjc.daw.library.repository.EquipoRepository;

@RestController
@RequestMapping("/api/v1/jugadores")
public class JugadorRestController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private JugadorMapper mapper;

    // 1. OBTENER TODOS (Devuelve DTOs Básicos sin listas para ser eficiente)
@GetMapping("/")
    public Page<JugadorDTO> getJugadores(Pageable pageable) {
        
        Page<Jugador> jugadorPage = jugadorService.getJugadores(pageable);
        
        return jugadorPage.map(mapper::toDTO);    
}

    // 2. OBTENER UNO POR ID (Devuelve DTO Completo)
    @GetMapping("/{id}")
    public JugadorDTO getPlayerByID(@PathVariable long id) {
        Jugador jugador = jugadorService.findById(id).orElseThrow();
        return mapper.toDTO(jugador);
    }

    // 3. CREAR UN JUGADOR
@PostMapping("/")
    public ResponseEntity<JugadorDTO> createPlayer(@RequestBody JugadorBasicDTO jugadorDTO, Principal principal) {
        
        //  Comprobamos si hay un usuario logueado
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        // Obtenemos el equipo que está haciendo la petición
        Equipo equipoLogueado = equipoRepository.findByUsername(principal.getName()).orElseThrow();

        //  Convertimos el DTO a Entidad
        Jugador jugador = mapper.toDomain(jugadorDTO);
        
        jugador.setEquipo(equipoLogueado); 
        
        //  Guardamos en base de datos
        jugadorService.save(jugador);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(jugador.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(mapper.toDTO(jugador));
    }

    
    // 4. ACTUALIZAR UN JUGADOR
@PutMapping("/{id}")
    public ResponseEntity<JugadorDTO> replacePlayer(@PathVariable long id, @RequestBody JugadorBasicDTO newJugadorDTO, Principal principal) {
        // Buscamos el jugador original
        Jugador jugadorExistente = jugadorService.findById(id).orElseThrow(() -> new NoSuchElementException());
        
        // Control de acceso
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401: No ha iniciado sesión
        }
        
        Equipo equipoLogueado = equipoRepository.findByUsername(principal.getName()).orElseThrow();
        boolean esDueño = jugadorExistente.getEquipo() != null && jugadorExistente.getEquipo().getId().equals(equipoLogueado.getId());
        boolean esAdmin = equipoLogueado.getRoles().contains("ADMIN");

        if (!esDueño && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403: No tiene permiso
        }

        
        Jugador newJugador = mapper.toDomain(newJugadorDTO);
        newJugador.setId(id); 
        newJugador.setEquipo(jugadorExistente.getEquipo()); // Conservamos el equipo original
        jugadorService.save(newJugador);
        
        return ResponseEntity.ok(mapper.toDTO(newJugador));
    }
 
     // 5. BORRAR UN JUGADOR
@DeleteMapping("/{id}")
    public ResponseEntity<JugadorDTO> deletePlayer(@PathVariable long id, Principal principal) {
        // Buscamos el jugador original
        Jugador jugador = jugadorService.findById(id).orElseThrow(() -> new NoSuchElementException());

        // Control de acceso
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401: No ha iniciado sesión
        }

        Equipo equipoLogueado = equipoRepository.findByUsername(principal.getName()).orElseThrow();
        boolean esDueño = jugador.getEquipo() != null && jugador.getEquipo().getId().equals(equipoLogueado.getId());
        boolean esAdmin = equipoLogueado.getRoles().contains("ADMIN");

        if (!esDueño && !esAdmin) {
            throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, 
            "Acceso denegado: No tienes permiso para modificar o borrar un jugador que pertenece a otro equipo."
            );
}

        jugadorService.deleteById(id);
        return ResponseEntity.ok(mapper.toDTO(jugador));
    }

}
