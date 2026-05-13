package es.codeurjc.daw.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TournamentBasicDTO(
    Long id, 

    @NotBlank(message = "El nombre del torneo es obligatorio")
    String name, 

    @NotBlank(message = "El tipo de torneo es obligatorio")
    String type, 

    @NotBlank(message = "El estado del torneo es obligatorio")
    String status, 

    @Min(value = 2, message = "El número máximo de participantes debe ser al menos 2")
    int maxParticipants, 
    boolean hasImage
) {}
