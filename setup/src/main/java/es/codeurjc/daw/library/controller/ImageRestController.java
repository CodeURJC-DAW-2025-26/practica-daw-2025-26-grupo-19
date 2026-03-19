package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.service.EquipoService;
import es.codeurjc.daw.library.service.JugadorService;
import es.codeurjc.daw.library.service.TorneoService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/tournament/{id}/image")
    public ResponseEntity<Resource> downloadTournamentImage(@PathVariable long id) throws SQLException {
        Optional<Torneo> op = torneoService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG); 

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
   
    @PostMapping("/tournament/{id}/image")
    public ResponseEntity<Object> uploadTournamentImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Torneo> tournamentOptional = torneoService.findById(id);

        if (tournamentOptional.isPresent()) {
            torneoService.saveImage(id, imageFile); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/tournament/{id}/image")
    public ResponseEntity<Object> replaceTournamentImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Torneo> tournamentOptional = torneoService.findById(id);

        if (tournamentOptional.isPresent()) {
            torneoService.saveImage(id, imageFile); 

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/tournament/{id}/image")
    public ResponseEntity<Object> deleteTournamentImage(@PathVariable long id) {
        Optional<Torneo> tournamentOptional = torneoService.findById(id);

        if (tournamentOptional.isPresent()) {
            torneoService.deleteImage(id);

            return ResponseEntity.noContent().build(); 
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
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/jugador/{id}/image")
    public ResponseEntity<Object> uploadPlayerImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Jugador> playerOptional = jugadorService.findById(id);

        if (playerOptional.isPresent()) {
            jugadorService.saveImage(id, imageFile); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/jugador/{id}/image")
    public ResponseEntity<Object> replacePlayerImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Jugador> playerOptional = jugadorService.findById(id);

        if (playerOptional.isPresent()) {
            jugadorService.saveImage(id, imageFile); 

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/jugador/{id}/image")
    public ResponseEntity<Object> deletePlayerImage(@PathVariable long id) {
        Optional<Jugador> playerOptional = jugadorService.findById(id);

        if (playerOptional.isPresent()) {
            jugadorService.deleteImage(id);

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/equipos/{id}/image")
    public ResponseEntity<Resource> downloadEquipoImage(@PathVariable long id) throws SQLException {
        Optional<Equipo> op = equipoService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG); 

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/equipos/{id}/image")
    public ResponseEntity<Object> uploadEquipoImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Equipo> equipoOptional = equipoService.findById(id);

        if (equipoOptional.isPresent()) {
            equipoService.saveImage(id, imageFile); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/equipos/{id}/image")
    public ResponseEntity<Object> replaceEquipoImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Equipo> equipoOptional = equipoService.findById(id);

        if (equipoOptional.isPresent()) {
            equipoService.saveImage(id, imageFile); 

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/equipos/{id}/image")
    public ResponseEntity<Object> deleteEquipoImage(@PathVariable long id) {
        Optional<Equipo> equipoOptional = equipoService.findById(id);

        if (equipoOptional.isPresent()) {
            equipoService.deleteImage(id);

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

}