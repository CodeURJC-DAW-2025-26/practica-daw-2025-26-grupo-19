package es.codeurjc.daw.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

        Optional<Equipo> findByEmail(String email);

        Optional<Equipo> findByUsername(String username);

}