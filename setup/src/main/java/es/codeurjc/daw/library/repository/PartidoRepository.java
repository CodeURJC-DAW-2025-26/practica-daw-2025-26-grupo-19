package es.codeurjc.daw.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Partido;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

}