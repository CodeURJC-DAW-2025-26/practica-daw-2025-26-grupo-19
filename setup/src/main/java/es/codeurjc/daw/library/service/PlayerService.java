package es.codeurjc.daw.library.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.repository.PlayerRepository;



@Service
public class PlayerService {
	@Autowired
	private PlayerRepository playerRepository;

	public List<Player> findAll() {
		return playerRepository.findAll();
	}

	public Optional<Player> findById(Long id) {
		return playerRepository.findById(id);
	}
	public List<Map<String, Object>> getTop5ScorersWithPercentage() {
        List<Player> topScorers = playerRepository.findTop5ByOrderByGoalsDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        if (!topScorers.isEmpty()) {
            int maxGoals = topScorers.get(0).getGoals();
            if (maxGoals == 0) maxGoals = 1; // Prevent division by zero if nobody scored

            for (Player p : topScorers) {
                Map<String, Object> map = new HashMap<>();
                map.put("nombre", p.getName());
                map.put("nombreEquipo", p.getTeam() != null ? p.getTeam().getTeamName() : "Sin equipo");
                map.put("goles", p.getGoals());
                map.put("porcentaje", (p.getGoals() * 100) / maxGoals);
                result.add(map);
            }
        }
        return result;
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public List<Map<String, Object>> getTop5AssistersWithPercentage() {
        List<Player> topAssisters = playerRepository.findTop5ByOrderByAssistsDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        if (!topAssisters.isEmpty()) {
            int maxAssists = topAssisters.get(0).getAssists();
            if (maxAssists == 0) maxAssists = 1;

            for (Player p : topAssisters) {
                Map<String, Object> map = new HashMap<>();
                map.put("nombre", p.getName());
                map.put("nombreEquipo", p.getTeam() != null ? p.getTeam().getTeamName() : "Sin equipo");
                map.put("asistencias", p.getAssists());
                map.put("porcentaje", (p.getAssists() * 100) / maxAssists);
                result.add(map);
            }
        }
        return result;
    }

    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    public Page<Player> getPlayers(Pageable pageable) {
		return playerRepository.findAll(pageable);
	}

    
    public void saveImage(long id, MultipartFile imageFile) throws IOException, SQLException {
        Player player = this.findById(id).orElseThrow();
        
        Blob imageBlob = new SerialBlob(imageFile.getBytes());
        player.setImage(imageBlob);
        player.setHasImage(true);
        
        this.save(player); 
    }

    public void deleteImage(long id) {
        Player player = this.findById(id).orElseThrow();
        
        player.setImage(null);
        player.setHasImage(false);
        
        this.save(player);
    }

}
