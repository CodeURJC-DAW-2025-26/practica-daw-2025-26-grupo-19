package es.codeurjc.daw.library.dto;

public record PartidoRequestDTO (
    String fechaFormateada,
    int golesLocal, 
    int golesVisitante, 
    boolean jugado,
    Long torneoId,
    Long equipoLocalId,
    Long equipoVisitanteId
) {}
