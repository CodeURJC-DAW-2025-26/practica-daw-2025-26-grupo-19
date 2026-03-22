package es.codeurjc.daw.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

}
