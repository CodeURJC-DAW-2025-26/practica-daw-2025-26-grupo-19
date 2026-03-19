package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.TorneoRepository;
import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Partido;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TorneoService {

@Autowired
    private TorneoRepository repository;

    public Optional<Torneo> findById(long id) {
        return repository.findById(id);
    }

    public List<Torneo> findAll() {
        return repository.findAll();
    }

    public void save(Torneo torneo) {
        repository.save(torneo);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

	public void generarCalendario(Torneo torneo) {
        List<Equipo> equipos = torneo.getEquipos();
        
        // If there arent at least 2 teams we cant generate the matches
        if (equipos.size() < 2) return;

        // If there are already matches we void duplicates
        if (!torneo.getPartidos().isEmpty()) return;

        LocalDateTime fechaPartido = LocalDateTime.now().plusDays(1); // El torneo empieza mañana

        // Double loop to generate the first and second leg (Basic Round Robin)
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = 0; j < equipos.size(); j++) {
                if (i != j) { // A team cant play against himself
                    Equipo local = equipos.get(i);
                    Equipo visitante = equipos.get(j);
                    
                    // We create the first leg match (or second leg depending on the iteration)
                    Partido partido = new Partido(torneo, local, visitante, fechaPartido);
                    torneo.getPartidos().add(partido);
                    
                    // We add a day so the matches are staggered in the schedule
                    fechaPartido = fechaPartido.plusDays(1);
                }
            }
        }
        
        // Save the changes to the database
        repository.save(torneo);
    }

    public Page<Torneo> getTorneos(Pageable pageable) {
        return repository.findAll(pageable); 
    }

}

 