package es.codeurjc.daw.library.dto;

public record MatchDTO(
    Long id, 
    String formattedDate, 
    int homeGoals, 
    int awayGoals, 
    boolean played,
    TournamentBasicDTO tournament,
    TeamBasicDTO homeTeam,
    TeamBasicDTO awayTeam
) {}
