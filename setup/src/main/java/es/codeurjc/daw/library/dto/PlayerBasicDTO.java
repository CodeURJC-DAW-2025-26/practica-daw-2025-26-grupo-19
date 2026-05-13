package es.codeurjc.daw.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlayerBasicDTO(
    Long id, 

    @NotBlank(message = "El nombre del jugador es obligatorio")
    String name, 

    @NotBlank(message = "La posición es obligatoria")
    String position, 

    @Min(value = 1, message = "El número de camiseta debe ser mayor que 0")
    int jerseyNumber, 
    
    int goals, 
    int assists, 
    boolean hasImage
) {}
