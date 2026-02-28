package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.repository.JugadorRepository;



@Service
public class JugadorService {
	@Autowired
	private JugadorRepository jugadorRepository;

	public List<Jugador> findAll() {
		return jugadorRepository.findAll();
	}

	public Optional<Jugador> findById(Long id) {
		return jugadorRepository.findById(id);
	}
}
