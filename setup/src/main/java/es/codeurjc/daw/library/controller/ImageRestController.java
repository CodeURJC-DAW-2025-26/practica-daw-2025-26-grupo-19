package es.codeurjc.daw.library.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob; // Ojo, necesitas este importimport org.springframework.beans.factory.annotation.Autowired;

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
import es.codeurjc.daw.library.service.EquipoService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping("/equipos/{id}/image")
    public ResponseEntity<Resource> downloadEquipoImage(@PathVariable long id) throws SQLException {
        Optional<Equipo> op = equipoService.findById(id);

        if (op.isPresent() && op.get().getImagen() != null) {
            Blob image = op.get().getImagen();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG); // Por defecto JPEG [cite: 238]

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
            Equipo equipo = equipoOptional.get();
            
            Blob imageBlob = new SerialBlob(imageFile.getBytes());
            
            equipo.setImagen(imageBlob);
            equipo.setHasImagen(true);
            equipoService.save(equipo); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/equipos/{id}/image")
    public ResponseEntity<Object> deleteEquipoImage(@PathVariable long id) {
        Optional<Equipo> equipoOptional = equipoService.findById(id);

        if (equipoOptional.isPresent()) {
            Equipo equipo = equipoOptional.get();
            
            equipo.setImagen(null);
            equipo.setHasImagen(false);
            equipoService.save(equipo);

            // Devolvemos 204 No Content al borrar con éxito
            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

}