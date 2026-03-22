package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Team;


@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamDTO toDTO(Team team);

    TeamBasicDTO toBasicDTO(Team team);
    
    List<TeamBasicDTO> toDTOs(Collection<Team> teams);

    // Ignore lists when mapping to Domain to avoid persistence issues
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "tournaments", ignore = true)
    @Mapping(target = "homeMatches", ignore = true)
    @Mapping(target = "awayMatches", ignore = true)
    Team toDomain(TeamBasicDTO teamDTO);
}
