package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.dto.TournamentDTO;
import es.codeurjc.daw.library.dto.TournamentBasicDTO;
import es.codeurjc.daw.library.dto.TournamentMapper;
import es.codeurjc.daw.library.service.TournamentService;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentRestController {

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
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody TournamentBasicDTO tournamentDTO) {

        Tournament tournament = mapper.toDomain(tournamentDTO);
        
        tournamentService.save(tournament);

        TournamentDTO savedTournamentDTO = mapper.toDTO(tournament);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedTournamentDTO.id()).toUri();

        return ResponseEntity.created(location).body(savedTournamentDTO);
    }
    // 4. UPDATE A TOURNAMENT
    @PutMapping("/{id}")
    public TournamentDTO replaceTournament(@PathVariable long id, @RequestBody TournamentBasicDTO newTournamentDTO) {
        if (tournamentService.findById(id).isPresent()) {
            Tournament newTournament = mapper.toDomain(newTournamentDTO);
            newTournament.setId(id); // Keep the original ID so it updates instead of creating a new one
            tournamentService.save(newTournament);
            
            return mapper.toDTO(newTournament);
        } else {
            throw new NoSuchElementException();
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
}
