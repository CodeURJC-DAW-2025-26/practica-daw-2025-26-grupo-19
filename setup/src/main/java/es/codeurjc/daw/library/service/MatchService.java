package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Match;
import es.codeurjc.daw.library.repository.PlayerRepository;
import es.codeurjc.daw.library.repository.MatchRepository;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    // 1. OBTAIN ALL
    public Page<Match> getMatches(Pageable pageable) {
        return matchRepository.findAll(pageable);
    }

    // 2. OBTAIN BY ID
    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    // 3. SAVE / UPDATE
    public void save(Match match) {
        matchRepository.save(match);
    }

    // 4. DELETE
    public void deleteById(Long id) {
        matchRepository.deleteById(id);
    }

    // 5. SIMULATE MATCH
    public void simulateMatch(Match match) {
        if (!match.isPlayed()) {
            int homeGoals = (int) (Math.random() * 6);
            int awayGoals = (int) (Math.random() * 6);

            match.setHomeGoals(homeGoals);
            match.setAwayGoals(awayGoals);
            match.setPlayed(true);

            matchRepository.save(match);

            distributeGoalsAndAssists(match.getHomeTeam(), homeGoals);
            distributeGoalsAndAssists(match.getAwayTeam(), awayGoals);
        }
    }

    private void distributeGoalsAndAssists(Team team, int totalGoals) {
        List<Player> squad = team.getPlayers();
        
        if (squad == null || squad.isEmpty() || totalGoals == 0) {
            return;
        }

        for (int i = 0; i < totalGoals; i++) {
            int scorerIndex = (int) (Math.random() * squad.size());
            Player scorer = squad.get(scorerIndex);
            scorer.setGoals(scorer.getGoals() + 1);
            playerRepository.save(scorer);

            if (Math.random() > 0.4) {
                int assisterIndex = (int) (Math.random() * squad.size());
                Player assister = squad.get(assisterIndex);
                
                if (!assister.getId().equals(scorer.getId())) {
                    assister.setAssists(assister.getAssists() + 1);
                    playerRepository.save(assister);
                }
            }
        }
    }
}
