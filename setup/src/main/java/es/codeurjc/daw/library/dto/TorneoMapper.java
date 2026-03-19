package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Torneo;


@Mapper(componentModel = "spring")
public interface TorneoMapper {

    TorneoDTO toDTO(Torneo torneo);
    
    List<TorneoBasicDTO> toDTOs(Collection<Torneo> torneos);

    TorneoBasicDTO toBasicDTO(Torneo torneo);

    @Mapping(target = "equipos", ignore = true)
    @Mapping(target = "partidos", ignore = true)
    Torneo toDomain(TorneoBasicDTO torneoDTO);
}
