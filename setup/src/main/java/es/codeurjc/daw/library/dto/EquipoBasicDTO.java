package es.codeurjc.daw.library.dto;

public record EquipoBasicDTO(
    Long id, 
    String username, 
    String email, 
    String nombreEquipo, 
    boolean hasImagen
) {}
