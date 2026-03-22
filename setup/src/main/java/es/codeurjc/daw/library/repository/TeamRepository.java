package es.codeurjc.daw.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.daw.library.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

        Optional<Team> findByEmail(String email);

        Optional<Team> findByUsername(String username);

        Optional<Team> findByResetPasswordToken(String resetPasswordToken);
}
