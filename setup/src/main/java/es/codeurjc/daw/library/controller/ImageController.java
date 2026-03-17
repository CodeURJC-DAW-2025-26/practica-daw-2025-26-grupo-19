package es.codeurjc.daw.library.controller;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.service.EquipoService;
import es.codeurjc.daw.library.service.JugadorService;
import es.codeurjc.daw.library.service.TorneoService;

@Controller
public class ImageController {
    @Autowired
    private TorneoService torneoService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorService jugadorService;

    @GetMapping("/torneo/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Torneo> op = torneoService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok().contentType(mediaType).body(imageFile);
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

            return ResponseEntity.ok().contentType(mediaType).body(imageFile);
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

            return ResponseEntity.ok().contentType(mediaType).body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}