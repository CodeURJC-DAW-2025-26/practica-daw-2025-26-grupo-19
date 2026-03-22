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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.dto.PlayerBasicDTO;
import es.codeurjc.daw.library.dto.PlayerDTO;
import es.codeurjc.daw.library.dto.PlayerMapper;
import es.codeurjc.daw.library.dto.TournamentBasicDTO;
import es.codeurjc.daw.library.service.PlayerService;
import es.codeurjc.daw.library.repository.TeamRepository;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerRestController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerMapper mapper;

    // 1. GET ALL (Returns DTOs)
@GetMapping("/")
    public Page<PlayerDTO> getPlayers(Pageable pageable) {
        
        Page<Player> playerPage = playerService.getPlayers(pageable);
        
        return playerPage.map(mapper::toDTO);    
}

    // 2. GET ONE BY ID
    @GetMapping("/{id}")
    public PlayerDTO getPlayerByID(@PathVariable long id) {
        Player player = playerService.findById(id).orElseThrow();
        return mapper.toDTO(player);
    }

    // 3. CREATE A PLAYER
@PostMapping("/")
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerBasicDTO playerDTO, Principal principal) {
        
        // Check if there is a logged-in user
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        // Get the team making the request
        Team loggedInTeam = teamRepository.findByUsername(principal.getName()).orElseThrow();

        // Convert DTO to Entity
        Player player = mapper.toDomain(playerDTO);
        
        player.setTeam(loggedInTeam); 
        
        // Save to database
        playerService.save(player);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(player.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(mapper.toDTO(player));
    }

    
    // 4. UPDATE A PLAYER
@PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> replacePlayer(@PathVariable long id, @RequestBody PlayerBasicDTO newPlayerDTO, Principal principal) {
        // Find the original player
        Player existingPlayer = playerService.findById(id).orElseThrow(() -> new NoSuchElementException());
        
        // Access control
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401: Not logged in
        }
        
        Team loggedInTeam = teamRepository.findByUsername(principal.getName()).orElseThrow();
        boolean isOwner = existingPlayer.getTeam() != null && existingPlayer.getTeam().getId().equals(loggedInTeam.getId());
        boolean isAdmin = loggedInTeam.getRoles().contains("ADMIN");

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403: No permission
        }

        
        Player newPlayer = mapper.toDomain(newPlayerDTO);
        newPlayer.setId(id); 
        newPlayer.setTeam(existingPlayer.getTeam()); // Keep the original team
        playerService.save(newPlayer);
        
        return ResponseEntity.ok(mapper.toDTO(newPlayer));
    }
 
     // 5. DELETE A PLAYER
@DeleteMapping("/{id}")
    public ResponseEntity<PlayerDTO> deletePlayer(@PathVariable long id, Principal principal) {
        // Find the original player
        Player player = playerService.findById(id).orElseThrow(() -> new NoSuchElementException());

        // Access control
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401: Not logged in
        }

        Team loggedInTeam = teamRepository.findByUsername(principal.getName()).orElseThrow();
        boolean isOwner = player.getTeam() != null && player.getTeam().getId().equals(loggedInTeam.getId());
        boolean isAdmin = loggedInTeam.getRoles().contains("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, 
            "Acceso denegado: No tienes permiso para modificar o borrar un jugador que pertenece a otro equipo."
            );
}

        playerService.deleteById(id);
        return ResponseEntity.ok(mapper.toDTO(player));
    }

}
