package es.codeurjc.daw.library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.TorneoRepository;

@Service
public class TorneoService {

@Autowired
    private TorneoRepository repository;

    public Optional<Torneo> findById(long id) {
        return repository.findById(id);
    }

    public List<Torneo> findAll() {
        return repository.findAll();
    }

    public void save(Torneo torneo) {
        repository.save(torneo);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

	
}

 