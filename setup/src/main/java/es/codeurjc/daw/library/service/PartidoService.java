package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Partido;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.repository.PartidoRepository;

@Service
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    // 1. OBTAIN ALL
    public Page<Partido> getPartidos(Pageable pageable) {
        return partidoRepository.findAll(pageable);
    }

    // 2. OBTAIN BY ID
    public Optional<Partido> findById(Long id) {
        return partidoRepository.findById(id);
    }

    // 3. SAVE/ UPDATE
    public void save(Partido partido) {
        partidoRepository.save(partido);
    }

    // 4. DELETE
    public void deleteById(Long id) {
        partidoRepository.deleteById(id);
    }

    // 5. SIMULATE MATCH
    public void simularPartido(Partido partido) {
        if (!partido.isJugado()) {
            int golesLocal = (int) (Math.random() * 6);
            int golesVisitante = (int) (Math.random() * 6);

            partido.setGolesLocal(golesLocal);
            partido.setGolesVisitante(golesVisitante);
            partido.setJugado(true);

            partidoRepository.save(partido);

            repartirGolesYAsistencias(partido.getEquipoLocal(), golesLocal);
            repartirGolesYAsistencias(partido.getEquipoVisitante(), golesVisitante);
        }
    }

    private void repartirGolesYAsistencias(Equipo equipo, int golesTotales) {
        List<Jugador> plantilla = equipo.getJugadores();
        
        if (plantilla == null || plantilla.isEmpty() || golesTotales == 0) {
            return;
        }

        for (int i = 0; i < golesTotales; i++) {
            int indiceGoleador = (int) (Math.random() * plantilla.size());
            Jugador goleador = plantilla.get(indiceGoleador);
            goleador.setGoles(goleador.getGoles() + 1);
            jugadorRepository.save(goleador);

            if (Math.random() > 0.4) {
                int indiceAsistente = (int) (Math.random() * plantilla.size());
                Jugador asistente = plantilla.get(indiceAsistente);
                
                if (!asistente.getId().equals(goleador.getId())) {
                    asistente.setAsistencias(asistente.getAsistencias() + 1);
                    jugadorRepository.save(asistente);
                }
            }
        }
    }
}