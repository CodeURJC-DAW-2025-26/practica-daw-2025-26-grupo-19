package es.codeurjc.daw.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TeamBasicDTO(
    Long id, 

    @NotBlank(message = "El nombre de usuario es obligatorio")
    String username, 

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    String email, 

    @NotBlank(message = "El nombre del equipo es obligatorio")
    String teamName, 
    boolean hasImage
) {}
