package es.codeurjc.daw.library.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Blob;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.repository.TeamRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public Optional<Team> findByUsername(String username){
        return teamRepository.findByUsername(username);
    }

    public boolean isDuplicateJerseyNumber(Team team, int jerseyNumber){
        for (Player player : team.getPlayers()) {
            if (player.getJerseyNumber() == jerseyNumber) {
                return true;
            }
        }
        return false;
    }

    public List<Player> getPlayersSortedByJerseyNumber(Team team) {
    // Get the list of players
    List<Player> players = team.getPlayers();
    
    // Bubble sort algorithm
    int n = players.size();
    
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - 1 - i; j++) {
            
            // Get current player and next player
            Player currentPlayer = players.get(j);
            Player nextPlayer = players.get(j + 1);
            
            // If the jersey number of current is greater than next, swap them
            if (currentPlayer.getJerseyNumber() > nextPlayer.getJerseyNumber()) {
                // Swap positions in the list
                players.set(j, nextPlayer);
                players.set(j + 1, currentPlayer);
            }
            
        }
    }
    
    // Return the sorted list
    return players;
}
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public void save(Team team) {
        teamRepository.save(team);
    }

    public void updateResetPasswordToken(String token, String email) throws Exception {
    Optional<Team> teamOpt = teamRepository.findByEmail(email);
    if (teamOpt.isPresent()) {
        Team team = teamOpt.get();
        team.setResetPasswordToken(token);
        teamRepository.save(team);
    } else {
        throw new Exception("No se encontró ningún equipo con el email " + email);
    }
}

public Optional<Team> getByResetPasswordToken(String token) {
    return teamRepository.findByResetPasswordToken(token);
}

public void updatePassword(Team team, String newPassword) {
    team.setEncodedPassword(newPassword);
    team.setResetPasswordToken(null);
    teamRepository.save(team);
}

public Page<Team> getTeams(Pageable pageable) {
		return teamRepository.findAll(pageable);
}

public void saveImage(long id, MultipartFile imageFile) throws IOException, SQLException {
        Team team = this.findById(id).orElseThrow();
        
        Blob imageBlob = new SerialBlob(imageFile.getBytes());
        team.setImage(imageBlob);
        team.setHasImage(true);
        
        this.save(team); 
    }

    public void deleteImage(long id) {
        Team team = this.findById(id).orElseThrow();
        
        team.setImage(null);
        team.setHasImage(false);
        
        this.save(team);
    }

    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

}
