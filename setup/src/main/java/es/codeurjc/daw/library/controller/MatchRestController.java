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

import es.codeurjc.daw.library.dto.MatchBasicDTO;
import es.codeurjc.daw.library.dto.MatchDTO;
import es.codeurjc.daw.library.dto.MatchMapper;
import es.codeurjc.daw.library.dto.MatchRequestDTO;
import es.codeurjc.daw.library.model.Match;
import es.codeurjc.daw.library.service.TeamService;
import es.codeurjc.daw.library.service.MatchService;
import es.codeurjc.daw.library.service.TournamentService;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchRestController {
    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private MatchMapper mapper;

    // 1. OBTAIN ALL MATCHES
    @GetMapping("/")
    public Page<MatchDTO> getMatches(Pageable pageable) {
        Page<Match> matchesPage = matchService.getMatches(pageable);
        return matchesPage.map(mapper::toDTO);
    }

    // 2. OBTAIN A MATCH BY ID
    @GetMapping("/{id}")
    public MatchDTO getMatchById(@PathVariable long id) {
        Match match = matchService.findById(id).orElseThrow();

        return mapper.toDTO(match);
    }

    // 3. CREATE A MATCH
    @PostMapping("/")
    public ResponseEntity<MatchDTO> createMatch(@RequestBody MatchRequestDTO requestDTO) {
        Match match = mapper.requestToDomain(requestDTO);
        
        if (requestDTO.tournamentId() != null) {
            tournamentService.findById(requestDTO.tournamentId()).ifPresent(match::setTournament);
        }
        if (requestDTO.homeTeamId() != null) {
            teamService.findById(requestDTO.homeTeamId()).ifPresent(match::setHomeTeam);
        }
        if (requestDTO.awayTeamId() != null) {
            teamService.findById(requestDTO.awayTeamId()).ifPresent(match::setAwayTeam);
        }

        matchService.save(match);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(match.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDTO(match));
    }

    // 4. UPDATE A MATCH
    @PutMapping("/{id}")
    public MatchDTO replaceMatch(@PathVariable long id, @RequestBody MatchBasicDTO newMatchDTO) {
        Optional<Match> existingMatchOpt = matchService.findById(id);
        
        if (existingMatchOpt.isPresent()) {
            Match oldMatch = existingMatchOpt.get();
            
            Match updatedMatch = mapper.toDomain(newMatchDTO);
            updatedMatch.setId(id);
            
            updatedMatch.setTournament(oldMatch.getTournament());
            updatedMatch.setHomeTeam(oldMatch.getHomeTeam());
            updatedMatch.setAwayTeam(oldMatch.getAwayTeam());
            
            if (updatedMatch.getDate() == null) {
                updatedMatch.setDate(oldMatch.getDate());
            }

            matchService.save(updatedMatch);
            
            return mapper.toDTO(updatedMatch);
        } else {
            throw new NoSuchElementException();
        }
    }

    // 5. DELETE A MATCH
    @DeleteMapping("/{id}")
    public ResponseEntity<MatchDTO> deleteMatch(@PathVariable long id) {
        Optional<Match> match = matchService.findById(id);

        if (match.isPresent()) {
            MatchDTO deletedMatch = mapper.toDTO(match.get());
            matchService.deleteById(id);
            return ResponseEntity.ok(deletedMatch);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. SIMULATE A MATCH
    @PostMapping("/{id}/simulate")
    public MatchDTO simulateMatchRest(@PathVariable long id) {
        Match match = matchService.findById(id).orElseThrow();
        
        matchService.simulateMatch(match);
        
        return mapper.toDTO(match);
    }
}
