package es.codeurjc.daw.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MatchRequestDTO (

    @NotBlank(message = "La fecha es obligatoria")
    String formattedDate,

    @Min(value = 0, message = "Los goles locales no pueden ser negativos")
    int homeGoals, 

    @Min(value = 0, message = "Los goles visitantes no pueden ser negativos")
    int awayGoals, 

    boolean played,

    @NotNull(message = "Debes indicar el ID del torneo")
    Long tournamentId,

    @NotNull(message = "Debes indicar el ID del equipo local")
    Long homeTeamId,

    @NotNull(message = "Debes indicar el ID del equipo visitante")
    Long awayTeamId
) {}
