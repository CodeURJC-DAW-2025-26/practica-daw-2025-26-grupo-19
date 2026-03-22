package es.codeurjc.daw.library.dto;

public record TournamentBasicDTO(
    Long id, 
    String name, 
    String type, 
    String status, 
    int maxParticipants, 
    boolean hasImage
) {}
