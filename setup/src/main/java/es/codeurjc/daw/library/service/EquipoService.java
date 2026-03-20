package es.codeurjc.daw.library.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Blob;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public void updateResetPasswordToken(String token, String email) throws Exception {
    Optional<Equipo> equipoOpt = equipoRepository.findByEmail(email);
    if (equipoOpt.isPresent()) {
        Equipo equipo = equipoOpt.get();
        equipo.setResetPasswordToken(token);
        equipoRepository.save(equipo);
    } else {
        throw new Exception("No se encontró ningún equipo con el email " + email);
    }
}

public Optional<Equipo> getByResetPasswordToken(String token) {
    return equipoRepository.findByResetPasswordToken(token);
}

public void updatePassword(Equipo equipo, String newPassword) {
    // Recuerda inyectar PasswordEncoder en el servicio o pasarlo desde el controlador
    equipo.setEncodedPassword(newPassword);
    equipo.setResetPasswordToken(null);
    equipoRepository.save(equipo);
}

public Page<Equipo> getEquipos(Pageable pageable) {
		return equipoRepository.findAll(pageable);
}

public void saveImage(long id, MultipartFile imageFile) throws IOException, SQLException {
        Equipo equipo = this.findById(id).orElseThrow(); // Asume que el equipo existe
        
        Blob imageBlob = new SerialBlob(imageFile.getBytes());
        equipo.setImagen(imageBlob);
        equipo.setHasImagen(true);
        
        this.save(equipo); 
    }

    public void deleteImage(long id) {
        Equipo equipo = this.findById(id).orElseThrow();
        
        equipo.setImagen(null);
        equipo.setHasImagen(false);
        
        this.save(equipo);
    }

}
