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


import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.repository.TournamentRepository;
import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Match;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository repository;

    public Optional<Tournament> findById(long id) {
        return repository.findById(id);
    }

    public List<Tournament> findAll() {
        return repository.findAll();
    }

    public void save(Tournament tournament) {
        repository.save(tournament);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

	public void generateSchedule(Tournament tournament) {
        List<Team> teams = tournament.getTeams();
        
        // If there aren't at least 2 teams we can't generate the matches
        if (teams.size() < 2) return;

        // If there are already matches we avoid duplicates
        if (!tournament.getMatches().isEmpty()) return;

        LocalDateTime matchDate = LocalDateTime.now().plusDays(1); // Tournament starts tomorrow

        // Double loop to generate the first and second leg (Basic Round Robin)
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < teams.size(); j++) {
                if (i != j) { // A team can't play against itself
                    Team home = teams.get(i);
                    Team away = teams.get(j);
                    
                    // Create the match
                    Match match = new Match(tournament, home, away, matchDate);
                    tournament.getMatches().add(match);
                    
                    // Add a day so matches are staggered
                    matchDate = matchDate.plusDays(1);
                }
            }
        }
        
        // Save the changes to the database
        repository.save(tournament);
    }

    // AJAX
    public List<Tournament> getTournaments(int from, int to) {
        List<Tournament> tournaments = repository.findAll();

        // If they ask for a number that doesn't exist
        if(from >= tournaments.size()) {
            return List.of();
        }

        // If they ask for more than the limit
        if(to > tournaments.size()) {
            to = tournaments.size();
        }

        tournaments = tournaments.subList(from, to);

        return tournaments;
    }

    public Page<Tournament> getTournaments(Pageable pageable) {
        return repository.findAll(pageable); 
    }

    public void saveImage(long id, MultipartFile imageFile) throws IOException, SQLException {
        Tournament tournament = this.findById(id).orElseThrow();
        
        Blob imageBlob = new SerialBlob(imageFile.getBytes());
        tournament.setImage(imageBlob);
        tournament.setHasImage(true);
        
        this.save(tournament); 
    }

    public void deleteImage(long id) {
        Tournament tournament = this.findById(id).orElseThrow();
        
        tournament.setImage(null);
        tournament.setHasImage(false);
        
        this.save(tournament);
    }

}

 
