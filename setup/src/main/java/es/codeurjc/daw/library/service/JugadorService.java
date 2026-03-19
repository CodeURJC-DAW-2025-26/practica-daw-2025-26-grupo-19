package es.codeurjc.daw.library.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

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
	public List<Map<String, Object>> getTop5GoleadoresConPorcentaje() {
        List<Jugador> topGoleadores = jugadorRepository.findTop5ByOrderByGolesDesc();
        List<Map<String, Object>> resultado = new ArrayList<>();

        if (!topGoleadores.isEmpty()) {
            int maxGoles = topGoleadores.get(0).getGoles();
            if (maxGoles == 0) maxGoles = 1; // Prevenir división por cero si nadie ha marcado

            for (Jugador j : topGoleadores) {
                Map<String, Object> map = new HashMap<>();
                map.put("nombre", j.getNombre());
                map.put("nombreEquipo", j.getEquipo() != null ? j.getEquipo().getNombreEquipo() : "Sin equipo");
                map.put("goles", j.getGoles());
                map.put("porcentaje", (j.getGoles() * 100) / maxGoles);
                resultado.add(map);
            }
        }
        return resultado;
    }

    public void save(Jugador jugador) {
        jugadorRepository.save(jugador);
    }

    public List<Map<String, Object>> getTop5AsistentesConPorcentaje() {
        List<Jugador> topAsistentes = jugadorRepository.findTop5ByOrderByAsistenciasDesc();
        List<Map<String, Object>> resultado = new ArrayList<>();

        if (!topAsistentes.isEmpty()) {
            int maxAsistencias = topAsistentes.get(0).getAsistencias();
            if (maxAsistencias == 0) maxAsistencias = 1;

            for (Jugador j : topAsistentes) {
                Map<String, Object> map = new HashMap<>();
                map.put("nombre", j.getNombre());
                map.put("nombreEquipo", j.getEquipo() != null ? j.getEquipo().getNombreEquipo() : "Sin equipo");
                map.put("asistencias", j.getAsistencias());
                map.put("porcentaje", (j.getAsistencias() * 100) / maxAsistencias);
                resultado.add(map);
            }
        }
        return resultado;
    }

    public void deleteById(Long id) {
        jugadorRepository.deleteById(id);
    }

    public void saveImage(long id, MultipartFile imageFile) throws IOException, SQLException {
        Jugador jugador = this.findById(id).orElseThrow(); // Asume que el equipo existe
        
        Blob imageBlob = new SerialBlob(imageFile.getBytes());
        jugador.setImagen(imageBlob);
        jugador.setHasImagen(true);
        
        this.save(jugador); 
    }

    public void deleteImage(long id) {
        Jugador jugador = this.findById(id).orElseThrow();
        
        jugador.setImagen(null);
        jugador.setHasImagen(false);
        
        this.save(jugador);
    }

}
