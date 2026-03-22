package es.codeurjc.daw.library.dto;

public record PlayerDTO(
    Long id, 
    String name, 
    String position, 
    int jerseyNumber, 
    int goals, 
    int assists, 
    boolean hasImage,
    TeamBasicDTO team // Relationship using the basic DTO
) {}
