package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.dto.TorneoDTO;
import es.codeurjc.daw.library.dto.TorneoBasicDTO;
import es.codeurjc.daw.library.dto.TorneoMapper;
import es.codeurjc.daw.library.service.TorneoService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/torneos")
public class TorneoRestController {

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private TorneoMapper mapper;

    // 1. OBTENER TODOS LOS TORNEOS (Devuelve DTOs Básicos sin listas extensas para ser eficiente)
@GetMapping("/")
    public Page<TorneoBasicDTO> getTorneos(Pageable pageable) {
        
        // 1. Obtenemos una página de entidades Torneo desde el servicio
        Page<Torneo> torneosPage = torneoService.getTorneos(pageable);
        
        // 2. Convertimos la página de Torneo a página de TorneoBasicDTO usando el mapper
        return torneosPage.map(mapper::toBasicDTO);    
}

    // 2. OBTENER UN TORNEO POR ID (Devuelve DTO Completo con equipos inscritos y partidos)
    @GetMapping("/{id}")
    public TorneoDTO getTorneoById(@PathVariable long id) {
        Torneo torneo = torneoService.findById(id).orElseThrow();
        return mapper.toDTO(torneo);
    }

    // 3. CREAR UN TORNEO
@PostMapping("/")
    public ResponseEntity<TorneoDTO> createTorneo(@RequestBody TorneoBasicDTO torneoDTO) {

        Torneo torneo = mapper.toDomain(torneoDTO);
        
        torneoService.save(torneo);

        TorneoDTO savedTorneoDTO = mapper.toDTO(torneo);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedTorneoDTO.id()).toUri();

        return ResponseEntity.created(location).body(savedTorneoDTO);
    }
    // 4. ACTUALIZAR UN TORNEO
    @PutMapping("/{id}")
    public TorneoDTO replaceTorneo(@PathVariable long id, @RequestBody TorneoBasicDTO newTorneoDTO) {
        if (torneoService.findById(id).isPresent()) {
            Torneo newTorneo = mapper.toDomain(newTorneoDTO);
            newTorneo.setId(id); // Mantenemos el ID original para que actualice y no cree uno nuevo
            torneoService.save(newTorneo);
            
            return mapper.toDTO(newTorneo);
        } else {
            throw new NoSuchElementException();
        }
    }


    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<TorneoDTO> deleteTorneo(@PathVariable long id) {
        
        Optional<Torneo> torneo = torneoService.findById(id);

        if (torneo.isPresent()) {
            TorneoDTO torneoEliminado = mapper.toDTO(torneo.get());
            
            torneoService.delete(id); 
            
            return ResponseEntity.ok(torneoEliminado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 6. GENERAR CALENDARIO DEL TORNEO (Funcionalidad extra basada en el servicio)
    @PostMapping("/{id}/calendario")
    public ResponseEntity<TorneoDTO> generarCalendarioTorneo(@PathVariable long id) {
        Torneo torneo = torneoService.findById(id).orElseThrow();
        
        torneoService.generarCalendario(torneo);
        
        return ResponseEntity.ok(mapper.toDTO(torneo));
    }
}