package es.codeurjc.daw.library.dto;

public record MatchBasicDTO(
    Long id, 
    String formattedDate,
    int homeGoals, 
    int awayGoals, 
    boolean played,
    TeamBasicDTO homeTeam, 
    TeamBasicDTO awayTeam  
) {}
