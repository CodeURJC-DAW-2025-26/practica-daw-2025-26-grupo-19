package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.dto.JugadorBasicDTO;
import es.codeurjc.daw.library.dto.JugadorMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estadisticas")
public class EstadisticasRestController {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private JugadorMapper jugadorMapper;

    @GetMapping("/goleadores")
    public ResponseEntity<List<JugadorBasicDTO>> obtenerGoleadores() {
        
        List<Jugador> topGoleadores = jugadorRepository.findTop5ByOrderByGolesDesc();
        List<JugadorBasicDTO> goleadoresDTO = jugadorMapper.toDTOs(topGoleadores);
        
        return ResponseEntity.ok(goleadoresDTO);
    }

    @GetMapping("/asistentes")
    public ResponseEntity<List<JugadorBasicDTO>> obtenerAsistentes() {
        
        List<Jugador> topAsistentes = jugadorRepository.findTop5ByOrderByAsistenciasDesc();
        List<JugadorBasicDTO> asistentesDTO = jugadorMapper.toDTOs(topAsistentes);
        
        return ResponseEntity.ok(asistentesDTO);
    }
}