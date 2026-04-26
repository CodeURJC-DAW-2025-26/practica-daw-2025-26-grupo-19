package es.codeurjc.daw.library.controller;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Role;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.dto.TeamDTO;
import es.codeurjc.daw.library.dto.TeamBasicDTO;
import es.codeurjc.daw.library.dto.TeamMapper;
import es.codeurjc.daw.library.dto.PlayerDTO;
import es.codeurjc.daw.library.dto.RegisterDTO;
import es.codeurjc.daw.library.service.TeamService;
import org.springframework.transaction.annotation.Transactional;


@RestController
@RequestMapping("/api/v1/teams")
public class TeamRestController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. GET ALL (Returns Basic DTOs without lists for efficiency)
@GetMapping("/")
    public Page<TeamDTO> getTeams(Pageable pageable) {
        
        Page<Team> teamPage = teamService.getTeams(pageable);
        
        return teamPage.map(mapper::toDTO);    
}

    // 2. GET ONE BY ID (Returns Full DTO with players and tournaments)
    @GetMapping("/{id}")
    public TeamDTO getTeamById(@PathVariable long id) {
        Team team = teamService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El equipo con ID " + id + " no existe"));
        return mapper.toDTO(team);
    }

    // 3. CREATE A TEAM
    @PostMapping("/")
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamBasicDTO teamDTO) {
        Team team = mapper.toDomain(teamDTO);
        
        
        teamService.save(team);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(team.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(mapper.toDTO(team));
    }

    // 4. UPDATE A TEAM
@PutMapping("/{id}")
    public ResponseEntity<TeamDTO> replaceTeam(@PathVariable long id, @RequestBody TeamBasicDTO newTeamDTO, Principal principal) {
        
        // 1. Retrieve the existing team from the database
        Team existingTeam = teamService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipo no encontrado"));

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión para editar un equipo");
        }


        Team loggedInTeam = teamService.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        // Verify if the team trying to edit is the owner of that ID or is Admin
        boolean isOwner = existingTeam.getId().equals(loggedInTeam.getId());
        boolean isAdmin = loggedInTeam.getRoles().contains(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, 
                "Acceso denegado: Solo puedes editar tu propio perfil de equipo."
            );
        }

        
        existingTeam.setUsername(newTeamDTO.username());
        existingTeam.setEmail(newTeamDTO.email());
        existingTeam.setTeamName(newTeamDTO.teamName());
        

        // 3. Save the changes
        teamService.save(existingTeam);
        
        // 4. Return the updated DTO
        return ResponseEntity.ok(mapper.toDTO(existingTeam));
    }
    @Transactional
    @DeleteMapping("/{id}")
    public TeamDTO deleteTeam(@PathVariable long id) {
        Team team = teamService.findById(id).orElseThrow();
        teamService.deleteById(id);
        return mapper.toDTO(team);
    }

    @PostMapping("/register")
    public ResponseEntity<TeamBasicDTO> registerTeam(@RequestBody RegisterDTO registerDTO) {

        Team team = new Team(
            registerDTO.username(),
            registerDTO.email(),
            passwordEncoder.encode(registerDTO.password()), 
            registerDTO.teamName(),
            Role.USER 
        );

        
        teamService.save(team); 

        TeamBasicDTO savedTeamDTO = mapper.toBasicDTO(team); 

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedTeamDTO.id()).toUri();

        return ResponseEntity.created(location).body(savedTeamDTO);
    }
    @GetMapping("/me")
    public ResponseEntity<TeamDTO> getLoggedTeam(Principal principal) {
        if (principal == null) {
            // Si no hay sesión/token, devuelve 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Buscamos el equipo usando el username del token
        Team team = teamService.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            
        return ResponseEntity.ok(mapper.toDTO(team));
    }
}
