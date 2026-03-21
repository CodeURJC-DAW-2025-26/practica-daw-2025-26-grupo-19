package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.daw.library.dto.PartidoBasicDTO;
import es.codeurjc.daw.library.dto.PartidoDTO;
import es.codeurjc.daw.library.dto.PartidoMapper;
import es.codeurjc.daw.library.model.Partido;
import es.codeurjc.daw.library.service.PartidoService;

@RestController
@RequestMapping("/api/v1/partidos")
public class PartidoRestController {
    @Autowired
    private PartidoService partidoService;

    @Autowired
    private PartidoMapper mapper;

    // 1. OBTAIN ALL MATCHES
    @GetMapping("/")
    public Page<PartidoDTO> getPartidos(Pageable pageable) {
        Page<Partido> partidosPage = partidoService.getPartidos(pageable);
        return partidosPage.map(mapper::toDTO);
    }

    // 2. OBTAIN A MATCH BY ID
    @GetMapping("/{id}")
    public PartidoDTO getPartidoById(@PathVariable long id) {
        Partido partido = partidoService.findById(id).orElseThrow();

        return mapper.toDTO(partido);
    }

    // 3. CREATE A MATCH
    @PostMapping("/")
    public ResponseEntity<PartidoDTO> createPartido(@RequestBody PartidoBasicDTO partidoDTO) {
        Partido partido = mapper.toDomain(partidoDTO);
        partidoService.save(partido);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(partido.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDTO(partido));
    }

    // 4. UPDATE A MATCH
    @PutMapping("/{id}")
    public PartidoDTO replacePartido(@PathVariable long id, @RequestBody PartidoBasicDTO newPartidoDTO) {
        Optional<Partido> partidoExistenteOpt = partidoService.findById(id);
        
        if (partidoExistenteOpt.isPresent()) {
            // 1. Obtenemos el partido original que está en la Base de Datos (con sus equipos intactos)
            Partido partidoAntiguo = partidoExistenteOpt.get();
            
            // 2. Mapeamos los datos nuevos (goles, etc.) que nos llegan en el JSON
            Partido partidoActualizado = mapper.toDomain(newPartidoDTO);
            partidoActualizado.setId(id); // Mantenemos el ID original
            
            // 3. MAGIA: Traspasamos las relaciones del partido antiguo al actualizado
            partidoActualizado.setTorneo(partidoAntiguo.getTorneo());
            partidoActualizado.setEquipoLocal(partidoAntiguo.getEquipoLocal());
            partidoActualizado.setEquipoVisitante(partidoAntiguo.getEquipoVisitante());
            
            // Por si acaso MapStruct tiene problemas convirtiendo la fecha del DTO, mantenemos la original si se pierde
            if (partidoActualizado.getFecha() == null) {
                partidoActualizado.setFecha(partidoAntiguo.getFecha());
            }

            // 4. Guardamos en la base de datos
            partidoService.save(partidoActualizado);
            
            return mapper.toDTO(partidoActualizado);
        } else {
            throw new NoSuchElementException();
        }
    }

    // 5. DELETE A MATCH
    @DeleteMapping("/{id}")
    public ResponseEntity<PartidoDTO> deletePartido(@PathVariable long id) {
        Optional<Partido> partido = partidoService.findById(id);

        if (partido.isPresent()) {
            PartidoDTO partidoEliminado = mapper.toDTO(partido.get());
            partidoService.deleteById(id);
            return ResponseEntity.ok(partidoEliminado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. SIMULATE A MATCH
    @PostMapping("/{id}/simular")
    public PartidoDTO simularPartidoRest(@PathVariable long id) {
        Partido partido = partidoService.findById(id).orElseThrow();
        
        partidoService.simularPartido(partido);
        
        return mapper.toDTO(partido);
    }
}
