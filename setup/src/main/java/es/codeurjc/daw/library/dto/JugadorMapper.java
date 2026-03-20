package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Jugador;


@Mapper(componentModel = "spring")
public interface JugadorMapper {

    JugadorDTO toDTO(Jugador jugador);

    JugadorBasicDTO toBasicDTO(Jugador Jugador);


    List<JugadorBasicDTO> toDTOs(Collection<Jugador> jugadores);

    @Mapping(target = "equipo", ignore = true)
    Jugador toDomain(JugadorBasicDTO jugadorDTO);
}