package es.codeurjc.daw.library.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;
import es.codeurjc.daw.library.model.Partido;

@Mapper(componentModel = "spring")
public interface PartidoMapper {

    PartidoDTO toDTO(Partido partido);
    
    List<PartidoBasicDTO> toDTOs(Collection<Partido> partidos);

    @Mapping(target = "torneo", ignore = true)
    @Mapping(target = "equipoLocal", ignore = true)
    @Mapping(target = "equipoVisitante", ignore = true)
    @Mapping(target = "fecha", source = "fechaFormateada", dateFormat = "dd/MM/yyyy HH:mm")
    Partido toDomain(PartidoBasicDTO partidoDTO);
}