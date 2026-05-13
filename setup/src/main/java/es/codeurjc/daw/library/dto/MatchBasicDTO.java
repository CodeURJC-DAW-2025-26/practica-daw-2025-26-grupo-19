package es.codeurjc.daw.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MatchBasicDTO(
    Long id, 

    @NotBlank(message = "La fecha es obligatoria")
    String formattedDate,

    @Min(value = 0, message = "Los goles locales no pueden ser negativos")
    int homeGoals, 

    @Min(value = 0, message = "Los goles visitantes no pueden ser negativos")
    int awayGoals, 

    boolean played,
    TeamBasicDTO homeTeam, 
    TeamBasicDTO awayTeam  
) {}
