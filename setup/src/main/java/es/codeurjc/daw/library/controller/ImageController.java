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

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.service.TeamService;
import es.codeurjc.daw.library.service.PlayerService;
import es.codeurjc.daw.library.service.TournamentService;

@Controller
public class ImageController {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @GetMapping("/tournament/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Tournament> op = tournamentService.findById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok().contentType(mediaType).body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/team/{id}/image")
    public ResponseEntity<Resource> downloadUserImage(@PathVariable long id) throws SQLException {
        Optional<Team> op = teamService.findById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
            Resource imageFile = new InputStreamResource(image.getBinaryStream());

            MediaType mediaType = MediaTypeFactory
                    .getMediaType(imageFile)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok().contentType(mediaType).body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/player/{id}/image")
    public ResponseEntity<Resource> downloadPlayerImage(@PathVariable long id) throws SQLException {
        Optional<Player> op = playerService.findById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
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