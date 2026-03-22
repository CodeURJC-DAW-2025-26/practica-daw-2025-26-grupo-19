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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.dto.EquipoDTO;
import es.codeurjc.daw.library.dto.EquipoBasicDTO;
import es.codeurjc.daw.library.dto.EquipoMapper;
import es.codeurjc.daw.library.dto.JugadorDTO;
import es.codeurjc.daw.library.dto.RegisterDTO;
import es.codeurjc.daw.library.service.EquipoService;
import org.springframework.transaction.annotation.Transactional;


@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoRestController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private EquipoMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. OBTENER TODOS (Devuelve DTOs Básicos sin listas para ser eficiente)
@GetMapping("/")
    public Page<EquipoDTO> getJugadores(Pageable pageable) {
        
        Page<Equipo> equipoPage = equipoService.getEquipos(pageable);
        
        return equipoPage.map(mapper::toDTO);    
}

    // 2. OBTENER UNO POR ID (Devuelve DTO Completo con jugadores y torneos)
    @GetMapping("/{id}")
    public EquipoDTO getEquipoById(@PathVariable long id) {
        Equipo equipo = equipoService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El equipo con ID " + id + " no existe"));
        return mapper.toDTO(equipo);
    }

    // 3. CREAR UN EQUIPO
    @PostMapping("/")
    public ResponseEntity<EquipoDTO> createEquipo(@RequestBody EquipoBasicDTO equipoDTO) {
        Equipo equipo = mapper.toDomain(equipoDTO);
        
        
        equipoService.save(equipo);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(equipo.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(mapper.toDTO(equipo));
    }

    // 4. ACTUALIZAR UN EQUIPO
@PutMapping("/{id}")
    public ResponseEntity<EquipoDTO> replaceEquipo(@PathVariable long id, @RequestBody EquipoBasicDTO newEquipoDTO, Principal principal) {
        
        // 1. Recuperamos el equipo existente de la base de datos
        Equipo equipoExistente = equipoService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipo no encontrado"));

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión para editar un equipo");
        }


        Equipo equipoLogueado = equipoService.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        // Verificamos si el equipo que intenta editar es el dueño de esa ID o si es Administrador
        boolean esDueño = equipoExistente.getId().equals(equipoLogueado.getId());
        boolean esAdmin = equipoLogueado.getRoles().contains("ADMIN");

        if (!esDueño && !esAdmin) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, 
                "Acceso denegado: Solo puedes editar tu propio perfil de equipo."
            );
        }

        
        equipoExistente.setUsername(newEquipoDTO.username());
        equipoExistente.setEmail(newEquipoDTO.email());
        equipoExistente.setNombreEquipo(newEquipoDTO.nombreEquipo());
        

        // 3. Guardamos los cambios
        equipoService.save(equipoExistente);
        
        // 4. Devolvemos el DTO actualizado
        return ResponseEntity.ok(mapper.toDTO(equipoExistente));
    }
    @Transactional
    @DeleteMapping("/{id}")
    public EquipoDTO deleteEquipo(@PathVariable long id) {
        Equipo equipo = equipoService.findById(id).orElseThrow();
        equipoService.deleteById(id);
        return mapper.toDTO(equipo);
    }

    @PostMapping("/register")
    public ResponseEntity<EquipoBasicDTO> registrarEquipo(@RequestBody RegisterDTO registroDTO) {

        Equipo equipo = new Equipo(
            registroDTO.username(),
            registroDTO.email(),
            passwordEncoder.encode(registroDTO.password()), 
            registroDTO.nombreEquipo(),
            "USER" 
        );

        
        equipoService.save(equipo); 

        EquipoBasicDTO savedEquipoDTO = mapper.toBasicDTO(equipo); 

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedEquipoDTO.id()).toUri();

        return ResponseEntity.created(location).body(savedEquipoDTO);
    }

}
