package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.repository.PlayerRepository;
import es.codeurjc.daw.library.dto.PlayerBasicDTO;
import es.codeurjc.daw.library.dto.PlayerMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsRestController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @GetMapping("/scorers")
    public ResponseEntity<List<PlayerBasicDTO>> getTopScorers() {
        
        List<Player> topScorers = playerRepository.findTop5ByOrderByGoalsDesc();
        List<PlayerBasicDTO> scorersDTO = playerMapper.toDTOs(topScorers);
        
        return ResponseEntity.ok(scorersDTO);
    }

    @GetMapping("/assisters")
    public ResponseEntity<List<PlayerBasicDTO>> getTopAssisters() {
        
        List<Player> topAssisters = playerRepository.findTop5ByOrderByAssistsDesc();
        List<PlayerBasicDTO> assistersDTO = playerMapper.toDTOs(topAssisters);
        
        return ResponseEntity.ok(assistersDTO);
    }
}
