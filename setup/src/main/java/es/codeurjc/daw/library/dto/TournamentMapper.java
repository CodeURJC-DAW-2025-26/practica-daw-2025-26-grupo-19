package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Tournament;


@Mapper(componentModel = "spring")
public interface TournamentMapper {

    TournamentDTO toDTO(Tournament tournament);
    
    List<TournamentBasicDTO> toDTOs(Collection<Tournament> tournaments);

    TournamentBasicDTO toBasicDTO(Tournament tournament);

    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "matches", ignore = true)
    Tournament toDomain(TournamentBasicDTO tournamentDTO);
}
