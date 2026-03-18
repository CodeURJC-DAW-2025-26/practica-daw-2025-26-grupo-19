package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.dto.JugadorBasicDTO;
import es.codeurjc.daw.library.dto.JugadorDTO;
import es.codeurjc.daw.library.dto.JugadorMapper;
import es.codeurjc.daw.library.service.JugadorService;

@RestController
@RequestMapping("/api/v1/jugadores")
public class JugadorRestController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private JugadorMapper mapper;

    // 1. OBTENER TODOS (Devuelve DTOs Básicos sin listas para ser eficiente)
    @GetMapping("/")
    public Collection<JugadorBasicDTO> getAllPlayers() {
        return mapper.toDTOs(jugadorService.findAll());
    }

    // 2. OBTENER UNO POR ID (Devuelve DTO Completo)
    @GetMapping("/{id}")
    public JugadorDTO getPlayerByID(@PathVariable long id) {
        Jugador jugador = jugadorService.findById(id).orElseThrow();
        return mapper.toDTO(jugador);
    }

    // 3. CREAR UN JUGADOR
    @PostMapping("/")
    public ResponseEntity<JugadorDTO> createPlayer(@RequestBody JugadorBasicDTO jugadorDTO) {
        // Convertimos el DTO a Entidad
        Jugador jugador = mapper.toDomain(jugadorDTO);
        
        // (Nota: Aquí encriptarías la contraseña si viaja en el DTO antes de guardar)
        
        // Guardamos usando el servicio (que trabaja con entidades)
        jugadorService.save(jugador);
        
        // Generamos la URL del nuevo recurso creado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(jugador.getId())
                .toUri();
        
        // Devolvemos 201 Created con el DTO del jugador creado
        return ResponseEntity.created(location).body(mapper.toDTO(jugador));
    }

    
    // 4. ACTUALIZAR UN JUGADOR
    @PutMapping("/{id}")
    public JugadorDTO replacePlayer(@PathVariable long id, @RequestBody JugadorBasicDTO newJugadorDTO) {
        if (jugadorService.findById(id).isPresent()) {
            Jugador newJugador = mapper.toDomain(newJugadorDTO);
            newJugador.setId(id); 
            jugadorService.save(newJugador);
            
            return mapper.toDTO(newJugador);
        } else {
            throw new NoSuchElementException();
        }
    }
 
     // 5. BORRAR UN JUGADOR
    @DeleteMapping("/{id}")
    public JugadorDTO deletePlayer(@PathVariable long id) {
        Jugador jugador = jugadorService.findById(id).orElseThrow();
        jugadorService.deleteById(id);
        return mapper.toDTO(jugador);
    }

}
