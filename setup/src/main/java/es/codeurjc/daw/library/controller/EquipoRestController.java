package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
        Equipo equipo = equipoService.findById(id).orElseThrow();
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
    public EquipoDTO replaceEquipo(@PathVariable long id, @RequestBody EquipoBasicDTO newEquipoDTO) {
        if (equipoService.findById(id).isPresent()) {
            Equipo newEquipo = mapper.toDomain(newEquipoDTO);
            newEquipo.setId(id); // Mantenemos el ID original
            equipoService.save(newEquipo);
            
            return mapper.toDTO(newEquipo);
        } else {
            throw new NoSuchElementException();
        }
    }

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
