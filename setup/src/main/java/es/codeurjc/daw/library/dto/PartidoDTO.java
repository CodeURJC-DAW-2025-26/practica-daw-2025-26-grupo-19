package es.codeurjc.daw.library.dto;

public record PartidoDTO(
    Long id, 
    String fechaFormateada, 
    int golesLocal, 
    int golesVisitante, 
    boolean jugado,
    TorneoBasicDTO torneo,
    EquipoBasicDTO equipoLocal,
    EquipoBasicDTO equipoVisitante
) {}
