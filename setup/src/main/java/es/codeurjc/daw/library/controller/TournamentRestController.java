package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.model.TournamentStatus;
import es.codeurjc.daw.library.dto.TournamentDTO;
import es.codeurjc.daw.library.dto.TournamentBasicDTO;
import es.codeurjc.daw.library.dto.TournamentMapper;
import es.codeurjc.daw.library.service.TeamService;
import es.codeurjc.daw.library.service.TournamentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentRestController {
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentMapper mapper;

    // 1. GET ALL TOURNAMENTS (Returns Basic DTOs without extensive lists for efficiency)
    @GetMapping("/")
    public Page<TournamentBasicDTO> getTournaments(Pageable pageable) {
        
        // 1. Get a page of Tournament entities from the service
        Page<Tournament> tournamentsPage = tournamentService.getTournaments(pageable);
        
        // 2. Convert the Tournament page to TournamentBasicDTO page using the mapper
        return tournamentsPage.map(mapper::toBasicDTO);    
    }

    // 2. GET A TOURNAMENT BY ID (Returns Full DTO with enrolled teams and matches)
    @GetMapping("/{id}")
    public TournamentDTO getTournamentById(@PathVariable long id) {
        Tournament tournament = tournamentService.findById(id).orElseThrow();
        return mapper.toDTO(tournament);
    }

    // 3. CREATE A TOURNAMENT
    @PostMapping("/")
    public ResponseEntity<TournamentDTO> createTournament(@Valid @RequestBody TournamentBasicDTO tournamentDTO) {
        try {
            Tournament tournament = mapper.toDomain(tournamentDTO);
            
            tournamentService.save(tournament);

            TournamentDTO savedTournamentDTO = mapper.toDTO(tournament);

            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedTournamentDTO.id()).toUri();

            return ResponseEntity.created(location).body(savedTournamentDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Error en los datos: El estado o tipo de torneo no es válido. " +
                "Valores permitidos para estado: [INSCRIPCIONES_ABIERTAS, EN_CURSO, FINALIZADO]. " +
                "Valores permitidos para tipo: [LIGA, ELIMINATORIA]."
            );
        }
            
    }
    
    // 4. UPDATE A TOURNAMENT
    @PutMapping("/{id}")
    public TournamentDTO replaceTournament(@PathVariable long id, @Valid @RequestBody TournamentBasicDTO newTournamentDTO) {
        Tournament existingTournament = tournamentService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneo no encontrado"));

        try {
            Tournament updatedTournament = mapper.toDomain(newTournamentDTO);
            updatedTournament.setId(id);
            
            updatedTournament.setTeams(existingTournament.getTeams());
            updatedTournament.setMatches(existingTournament.getMatches());
            updatedTournament.setImage(existingTournament.getImage());
            updatedTournament.setHasImage(existingTournament.isHasImage());

            tournamentService.save(updatedTournament);
            return mapper.toDTO(updatedTournament);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Error en los datos: El estado o tipo de torneo no es válido. " +
                "Valores permitidos para estado: [INSCRIPCIONES_ABIERTAS, EN_CURSO, FINALIZADO]. " +
                "Valores permitidos para tipo: [LIGA, ELIMINATORIA]."
            );
        }
    }


    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<TournamentDTO> deleteTournament(@PathVariable long id) {
        
        Optional<Tournament> tournament = tournamentService.findById(id);

        if (tournament.isPresent()) {
            TournamentDTO deletedTournament = mapper.toDTO(tournament.get());
            
            tournamentService.delete(id); 
            
            return ResponseEntity.ok(deletedTournament);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 6. GENERATE TOURNAMENT SCHEDULE
    @PostMapping("/{id}/schedule")
    public ResponseEntity<TournamentDTO> generateTournamentSchedule(@PathVariable long id) {
        Tournament tournament = tournamentService.findById(id).orElseThrow();
        
        tournamentService.generateSchedule(tournament);
        
        return ResponseEntity.ok(mapper.toDTO(tournament));
    }

    // 7. ENROLL A TEAM IN A TOURNAMENT
    @PostMapping("/{id}/enroll")
    public ResponseEntity<TournamentDTO> enrollTeam(@PathVariable long id, HttpServletRequest request) {
        
        Optional<Tournament> tournamentOpt = tournamentService.findById(id);
        if (tournamentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Tournament tournament = tournamentOpt.get();

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = principal.getName();
        Optional<Team> teamOpt = teamService.findByUsername(username);
        
        if (teamOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Team team = teamOpt.get();

        if (tournament.getStatus() != TournamentStatus.INSCRIPCIONES_ABIERTAS || 
            tournament.getTeams().contains(team) || 
            (tournament.getMaxParticipants() > 0 && tournament.getTeams().size() >= tournament.getMaxParticipants())) {
            
            return ResponseEntity.badRequest().build();
        }

        tournament.getTeams().add(team);
        tournamentService.save(tournament);

        return ResponseEntity.ok(mapper.toDTO(tournament));
    }
}
