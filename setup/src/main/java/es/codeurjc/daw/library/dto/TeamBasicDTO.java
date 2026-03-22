package es.codeurjc.daw.library.dto;

public record TeamBasicDTO(
    Long id, 
    String username, 
    String email, 
    String teamName, 
    boolean hasImage
) {}
