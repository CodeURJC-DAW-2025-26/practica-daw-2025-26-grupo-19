package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Player;


@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerDTO toDTO(Player player);

    PlayerBasicDTO toBasicDTO(Player player);


    List<PlayerBasicDTO> toDTOs(Collection<Player> players);

    @Mapping(target = "team", ignore = true)
    Player toDomain(PlayerBasicDTO playerDTO);
}
