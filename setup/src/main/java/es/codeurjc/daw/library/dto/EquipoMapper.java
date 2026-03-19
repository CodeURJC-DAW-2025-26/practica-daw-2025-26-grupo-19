package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Equipo;


@Mapper(componentModel = "spring")
public interface EquipoMapper {

    EquipoDTO toDTO(Equipo equipo);

    EquipoBasicDTO toBasicDTO(Equipo equipo);
    
    List<EquipoBasicDTO> toDTOs(Collection<Equipo> equipos);

    // Ignoramos las listas al pasar a Dominio para evitar problemas de persistencia
    @Mapping(target = "jugadores", ignore = true)
    @Mapping(target = "torneos", ignore = true)
    @Mapping(target = "partidosLocal", ignore = true)
    @Mapping(target = "partidosVisitante", ignore = true)
    Equipo toDomain(EquipoBasicDTO equipoDTO);
}