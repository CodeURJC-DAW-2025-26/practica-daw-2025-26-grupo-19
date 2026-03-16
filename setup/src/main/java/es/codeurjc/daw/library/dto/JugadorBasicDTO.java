package es.codeurjc.daw.library.dto;

public record JugadorBasicDTO(
    Long id, 
    String nombre, 
    String posicion, 
    int dorsal, 
    int goles, 
    int asistencias, 
    boolean hasImagen
) {}
