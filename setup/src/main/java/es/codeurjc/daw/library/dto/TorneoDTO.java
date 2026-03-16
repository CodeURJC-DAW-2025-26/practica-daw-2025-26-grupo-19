package es.codeurjc.daw.library.dto;
import java.util.List;

public record TorneoDTO(
    Long id, 
    String nombre, 
    String tipo, 
    String estado, 
    int maxParticipantes, 
    boolean hasImagen,
    List<EquipoBasicDTO> equipos,
    List<PartidoBasicDTO> partidos
) {}
