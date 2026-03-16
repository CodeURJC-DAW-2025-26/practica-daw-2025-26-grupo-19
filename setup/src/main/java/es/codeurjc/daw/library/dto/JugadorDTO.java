package es.codeurjc.daw.library.dto;

public record JugadorDTO(
    Long id, 
    String nombre, 
    String posicion, 
    int dorsal, 
    int goles, 
    int asistencias, 
    boolean hasImagen,
    EquipoBasicDTO equipo // Relación usando el DTO básico
) {}