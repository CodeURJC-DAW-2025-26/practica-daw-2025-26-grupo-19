package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findTop5ByOrderByGoalsDesc();
    List<Player> findTop5ByOrderByAssistsDesc(); 
}
