package es.codeurjc.daw.library.dto;

public record PartidoBasicDTO(
    Long id, 
    String fechaFormateada,
    int golesLocal, 
    int golesVisitante, 
    boolean jugado
) {}
