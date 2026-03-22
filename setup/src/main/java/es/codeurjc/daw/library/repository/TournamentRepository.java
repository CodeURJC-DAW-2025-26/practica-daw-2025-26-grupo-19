package es.codeurjc.daw.library.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Tournament;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
        List<Tournament> findByType(String type);
}
