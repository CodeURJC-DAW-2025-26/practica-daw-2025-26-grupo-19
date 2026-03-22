package es.codeurjc.daw.library.dto;

public record PlayerBasicDTO(
    Long id, 
    String name, 
    String position, 
    int jerseyNumber, 
    int goals, 
    int assists, 
    boolean hasImage
) {}
