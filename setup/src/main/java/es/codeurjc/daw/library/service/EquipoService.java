package es.codeurjc.daw.library.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.repository.EquipoRepository;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    public Optional<Equipo> findById(Long id) {
        return equipoRepository.findById(id);
    }

    public Optional<Equipo> findByUsername(String username){
        return equipoRepository.findByUsername(username);
    }

    public boolean isDorsalRepetido(Equipo equipo, int dorsal){
        for (Jugador jugador : equipo.getJugadores()) {
            if (jugador.getDorsal() == dorsal) {
                return true;
            }
        }
        return false;
    }

    public List<Jugador> getJugadoresOrdenadosPorDorsal(Equipo equipo) {
    // Obtenemos la lista de jugadores
    List<Jugador> jugadores = equipo.getJugadores();
    
    // Algoritmo bubble sort
    int n = jugadores.size();
    
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - 1 - i; j++) {
            
            // Sacamos el jugador actual y el siguiente
            Jugador jugadorActual = jugadores.get(j);
            Jugador jugadorSiguiente = jugadores.get(j + 1);
            
            // Si el dorsal del actual es mayor que el del siguiente, los intercambiamos
            if (jugadorActual.getDorsal() > jugadorSiguiente.getDorsal()) {
                // Intercambio de posiciones en la lista
                jugadores.set(j, jugadorSiguiente);
                jugadores.set(j + 1, jugadorActual);
            }
            
        }
    }
    
    // Devolvemos la lista ya ordenada
    return jugadores;
}
    public List<Equipo> findAll() {
        return equipoRepository.findAll();
    }

    public void save(Equipo equipo) {
        equipoRepository.save(equipo);
    }
}
