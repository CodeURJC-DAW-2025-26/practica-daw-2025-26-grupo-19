package es.codeurjc.daw.library.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Torneo;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {
        List<Torneo> findByTipo(String tipo);
}
