package es.codeurjc.daw.library.dto;


public record RegisterDTO(
    String username,
    String email,
    String password,
    String teamName
) {}