package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Match;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    MatchDTO toDTO(Match match);
    
    List<MatchBasicDTO> toDTOs(Collection<Match> matches);

    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "homeTeam", ignore = true)
    @Mapping(target = "awayTeam", ignore = true)
    @Mapping(target = "date", source = "formattedDate", dateFormat = "dd/MM/yyyy HH:mm")
    Match toDomain(MatchBasicDTO matchDTO);

    // Method to convert Request DTO to Entity (with date format rule)
    @Mapping(target = "tournament", ignore = true)
    @Mapping(target = "homeTeam", ignore = true)
    @Mapping(target = "awayTeam", ignore = true)
    @Mapping(target = "date", source = "formattedDate", dateFormat = "dd/MM/yyyy HH:mm")
    Match requestToDomain(MatchRequestDTO requestDTO);
}
