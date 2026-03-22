package es.codeurjc.daw.library.dto;
import java.util.List;

public record TournamentDTO(
    Long id, 
    String name, 
    String type, 
    String status, 
    int maxParticipants, 
    boolean hasImage,
    List<TeamBasicDTO> teams,
    List<MatchBasicDTO> matches
) {}
