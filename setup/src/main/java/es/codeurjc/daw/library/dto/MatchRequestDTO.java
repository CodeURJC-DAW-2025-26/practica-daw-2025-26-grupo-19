package es.codeurjc.daw.library.dto;

public record MatchRequestDTO (
    String formattedDate,
    int homeGoals, 
    int awayGoals, 
    boolean played,
    Long tournamentId,
    Long homeTeamId,
    Long awayTeamId
) {}
