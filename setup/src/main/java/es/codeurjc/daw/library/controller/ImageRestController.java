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

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.service.TeamService;
import es.codeurjc.daw.library.service.PlayerService;
import es.codeurjc.daw.library.service.TournamentService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/tournament/{id}/image")
    public ResponseEntity<Resource> downloadTournamentImage(@PathVariable long id) throws SQLException {
        Optional<Tournament> op = tournamentService.findById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
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
        Optional<Tournament> tournamentOptional = tournamentService.findById(id);

        if (tournamentOptional.isPresent()) {
            tournamentService.saveImage(id, imageFile); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/tournament/{id}/image")
    public ResponseEntity<Object> replaceTournamentImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Tournament> tournamentOptional = tournamentService.findById(id);

        if (tournamentOptional.isPresent()) {
            tournamentService.saveImage(id, imageFile); 

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/tournament/{id}/image")
    public ResponseEntity<Object> deleteTournamentImage(@PathVariable long id) {
        Optional<Tournament> tournamentOptional = tournamentService.findById(id);

        if (tournamentOptional.isPresent()) {
            tournamentService.deleteImage(id);

            return ResponseEntity.noContent().build(); 
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
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageFile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/player/{id}/image")
    public ResponseEntity<Object> uploadPlayerImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Player> playerOptional = playerService.findById(id);

        if (playerOptional.isPresent()) {
            playerService.saveImage(id, imageFile); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/player/{id}/image")
    public ResponseEntity<Object> replacePlayerImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Player> playerOptional = playerService.findById(id);

        if (playerOptional.isPresent()) {
            playerService.saveImage(id, imageFile); 

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/player/{id}/image")
    public ResponseEntity<Object> deletePlayerImage(@PathVariable long id) {
        Optional<Player> playerOptional = playerService.findById(id);

        if (playerOptional.isPresent()) {
            playerService.deleteImage(id);

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/teams/{id}/image")
    public ResponseEntity<Resource> downloadTeamImage(@PathVariable long id) throws SQLException {
        Optional<Team> op = teamService.findById(id);

        if (op.isPresent() && op.get().getImage() != null) {
            Blob image = op.get().getImage();
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

    @PostMapping("/teams/{id}/image")
    public ResponseEntity<Object> uploadTeamImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Team> teamOptional = teamService.findById(id);

        if (teamOptional.isPresent()) {
            teamService.saveImage(id, imageFile); 

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/teams/{id}/image")
    public ResponseEntity<Object> replaceTeamImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException, SQLException {
        Optional<Team> teamOptional = teamService.findById(id);

        if (teamOptional.isPresent()) {
            teamService.saveImage(id, imageFile); 

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/teams/{id}/image")
    public ResponseEntity<Object> deleteTeamImage(@PathVariable long id) {
        Optional<Team> teamOptional = teamService.findById(id);

        if (teamOptional.isPresent()) {
            teamService.deleteImage(id);

            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

}