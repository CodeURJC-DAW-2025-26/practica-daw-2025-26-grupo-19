package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.dto.EquipoDTO;
import es.codeurjc.daw.library.dto.EquipoBasicDTO;
import es.codeurjc.daw.library.dto.EquipoMapper;
import es.codeurjc.daw.library.dto.RegisterDTO;
import es.codeurjc.daw.library.service.EquipoService;

@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoRestController {

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private EquipoMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. OBTENER TODOS (Devuelve DTOs Básicos sin listas para ser eficiente)
    @GetMapping("/")
    public Collection<EquipoBasicDTO> getAllEquipos() {
        return mapper.toDTOs(equipoService.findAll());
    }

    // 2. OBTENER UNO POR ID (Devuelve DTO Completo con jugadores y torneos)
    @GetMapping("/{id}")
    public EquipoDTO getEquipoById(@PathVariable long id) {
        Equipo equipo = equipoService.findById(id).orElseThrow();
        return mapper.toDTO(equipo);
    }

    // 3. CREAR UN EQUIPO
    @PostMapping("/")
    public ResponseEntity<EquipoDTO> createEquipo(@RequestBody EquipoBasicDTO equipoDTO) {
        // Convertimos el DTO a Entidad
        Equipo equipo = mapper.toDomain(equipoDTO);
        
        // (Nota: Aquí encriptarías la contraseña si viaja en el DTO antes de guardar)
        
        // Guardamos usando el servicio (que trabaja con entidades)
        equipoService.save(equipo);
        
        // Generamos la URL del nuevo recurso creado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(equipo.getId())
                .toUri();
        
        // Devolvemos 201 Created con el DTO del equipo creado
        return ResponseEntity.created(location).body(mapper.toDTO(equipo));
    }

    // 4. ACTUALIZAR UN EQUIPO
    @PutMapping("/{id}")
    public EquipoDTO replaceEquipo(@PathVariable long id, @RequestBody EquipoBasicDTO newEquipoDTO) {
        if (equipoService.findById(id).isPresent()) {
            Equipo newEquipo = mapper.toDomain(newEquipoDTO);
            newEquipo.setId(id); // Mantenemos el ID original
            equipoService.save(newEquipo);
            
            return mapper.toDTO(newEquipo);
        } else {
            throw new NoSuchElementException();
        }
    }

    /* // 5. BORRAR UN EQUIPO (Descomenta si añades deleteById a tu EquipoService)
    @DeleteMapping("/{id}")
    public EquipoDTO deleteEquipo(@PathVariable long id) {
        Equipo equipo = equipoService.findById(id).orElseThrow();
        equipoService.deleteById(id);
        return mapper.toDTO(equipo);
    }
    */

    @PostMapping("/register")
    public ResponseEntity<EquipoBasicDTO> registrarEquipo(@RequestBody RegisterDTO registroDTO) {

        // 1. Creamos la entidad Equipo usando el constructor que ya tienes en tu modelo
        // IMPORTANTE: Encriptamos la contraseña antes de guardarla y asignamos el rol "USER"
        Equipo equipo = new Equipo(
            registroDTO.username(),
            registroDTO.email(),
            passwordEncoder.encode(registroDTO.password()), 
            registroDTO.nombreEquipo(),
            "USER" 
        );

        // 2. Guardamos en la base de datos
        equipoService.save(equipo); // (Usa createEquipo(equipo) si lo cambiaste en el servicio)

        // 3. Pasamos la entidad a DTO para devolverla (y así no devolvemos la contraseña ni los roles)
        // Nota: Asegúrate de usar el método de tu mapper que devuelva el DTO básico
        EquipoBasicDTO savedEquipoDTO = mapper.toBasicDTO(equipo); 

        // 4. Creamos la URI del nuevo recurso
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedEquipoDTO.id()).toUri();

        // 5. Devolvemos el 201 Created con los datos del usuario recién registrado
        return ResponseEntity.created(location).body(savedEquipoDTO);
    }

}
