package es.codeurjc.daw.library.dto;

public record TorneoBasicDTO(
    Long id, 
    String nombre, 
    String tipo, 
    String estado, 
    int maxParticipantes, 
    boolean hasImagen
) {}
