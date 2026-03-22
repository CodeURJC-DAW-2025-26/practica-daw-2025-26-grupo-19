package es.codeurjc.daw.library.dto;
import java.util.List;

public record TeamDTO(
    Long id, 
    String username, 
    String email, 
    String teamName, 
    boolean hasImage,
    List<PlayerBasicDTO> players,
    List<TournamentBasicDTO> tournaments
) {}
