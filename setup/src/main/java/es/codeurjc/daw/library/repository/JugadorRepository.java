package es.codeurjc.daw.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Jugador;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    List<Jugador> findTop5ByOrderByGolesDesc();
    List<Jugador> findTop5ByOrderByAsistenciasDesc(); 
}
