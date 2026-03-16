package es.codeurjc.daw.library.dto;
import java.util.List;

public record EquipoDTO(
    Long id, 
    String username, 
    String email, 
    String nombreEquipo, 
    boolean hasImagen,
    List<JugadorBasicDTO> jugadores,
    List<TorneoBasicDTO> torneos
) {}
