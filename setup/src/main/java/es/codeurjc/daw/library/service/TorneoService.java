package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;


import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.TorneoRepository;
import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
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

    // AJAX
    public List<Torneo> getTorneos(int from, int to) {
        List<Torneo> torneos = repository.findAll();

        // If they ask us for a number that doesnt exist
        if(from >= torneos.size()) {
            return List.of();
        }

        // If they ask us for more than the limit
        if(to > torneos.size()) {
            to = torneos.size();
        }

        torneos = torneos.subList(from, to);

        return torneos;
    }

    public Page<Torneo> getTorneos(Pageable pageable) {
        return repository.findAll(pageable); 
    }

    public void saveImage(long id, MultipartFile imageFile) throws IOException, SQLException {
        Torneo torneo  = this.findById(id).orElseThrow(); // Asume que el equipo existe
        
        Blob imageBlob = new SerialBlob(imageFile.getBytes());
        torneo.setImagen(imageBlob);
        torneo.setHasImagen(true);
        
        this.save(torneo); 
    }

    public void deleteImage(long id) {
        Torneo torneo = this.findById(id).orElseThrow();
        
        torneo.setImagen(null);
        torneo.setHasImagen(false);
        
        this.save(torneo);
    }

}

 