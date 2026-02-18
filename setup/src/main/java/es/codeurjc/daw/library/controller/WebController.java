package es.codeurjc.daw.library.controller; // Ajusta el paquete si cambiaste el nombre del proyecto

import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    // Método global para saber si el usuario está logueado en cualquier página
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
    }


    @GetMapping("/")
    public String index(Model model) {

        // 1. DATOS DE LIGAS (Simulados)
        List<LeagueDTO> leagues = new ArrayList<>();

        // Liga 1: Liga Premium (Con tabla de clasificación)
        LeagueDTO liga1 = new LeagueDTO(1L, "Liga Premium", "En curso", "bg-success", true);
        liga1.getTopTeams().add(new TeamStats(1, "Águilas Rojas", 23));
        liga1.getTopTeams().add(new TeamStats(2, "Leones FC", 21));
        liga1.getTopTeams().add(new TeamStats(3, "Tigres Unidos", 20));
        liga1.getTopTeams().add(new TeamStats(4, "Halcones", 18));
        liga1.getTopTeams().add(new TeamStats(5, "Dragones FC", 16));
        leagues.add(liga1);

        // Liga 2: Regional Norte (Sin tabla, solo info)
        LeagueDTO liga2 = new LeagueDTO(2L, "Liga Regional Norte", "En curso", "bg-success", false);
        liga2.setDescription("Esta liga tiene 6 equipos participantes");
        liga2.setTeamsCount(6);
        leagues.add(liga2);

        // Liga 3: Amateur (Inscripciones abiertas)
        LeagueDTO liga3 = new LeagueDTO(3L, "Liga Amateur", "Inscripciones abiertas", "bg-info", false);
        liga3.setDescription("Esta liga aún no ha comenzado");
        liga3.setTeamsCount(0);
        leagues.add(liga3);

        model.addAttribute("leagues", leagues);

        // 2. DATOS DE TORNEOS
        List<TournamentDTO> tournaments = new ArrayList<>();
        tournaments.add(new TournamentDTO(1L, "Copa de Campeones", "En curso", "bg-success", "Eliminación directa", 8));
        tournaments.add(new TournamentDTO(2L, "Torneo Apertura 2026", "Inscripciones", "bg-info", "Fase de grupos", 0));

        model.addAttribute("tournaments", tournaments);

        // 3. ESTADÍSTICAS - GOLEADORES
        List<PlayerStats> scorers = new ArrayList<>();
        scorers.add(new PlayerStats(1, "Cristiano Ronaldo", 15));
        scorers.add(new PlayerStats(2, "Víctor Blade", 14));
        scorers.add(new PlayerStats(3, "Vini Jr", 13));
        scorers.add(new PlayerStats(4, "Axel Blaze", 12));
        scorers.add(new PlayerStats(5, "Shawn Froste", 11));

        model.addAttribute("topScorers", scorers);

        // 4. ESTADÍSTICAS - ASISTENTES
        List<PlayerStats> assisters = new ArrayList<>();
        assisters.add(new PlayerStats(1, "James Rodríguez", 13));
        assisters.add(new PlayerStats(2, "Willy Glass", 12));
        assisters.add(new PlayerStats(3, "Luka Modric", 11));
        assisters.add(new PlayerStats(4, "Toni Kroos", 10));
        assisters.add(new PlayerStats(5, "Andrés Iniesta", 9)); // Corregido el nombre ;)

        model.addAttribute("topAssisters", assisters);

        return "index"; // Esto carga el archivo index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/loginerror")
    public String loginerror(Model model) {
        model.addAttribute("error", true);
        return "login"; 
    }

    @GetMapping("/register")
    public String register() {
        return "register"; 
    }

    // --- CLASES AUXILIARES (DTOs) PARA TRANSPORTAR DATOS A LA VISTA ---
    // Puedes ponerlas en archivos separados en tu paquete 'model' si prefieres.

    public static class LeagueDTO {
        public Long id;
        public String name;
        public String status;
        public String badgeClass;
        public boolean hasStandings;
        public List<TeamStats> topTeams = new ArrayList<>();
        public String description;
        public int teamsCount;

        public LeagueDTO(Long id, String name, String status, String badgeClass, boolean hasStandings) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.badgeClass = badgeClass;
            this.hasStandings = hasStandings;
        }

        // Getters necesarios para Mustache si usas encapsulamiento,
        // pero con campos publicos funciona directo.
        public List<TeamStats> getTopTeams() {
            return topTeams;
        }

        public void setDescription(String d) {
            this.description = d;
        }

        public void setTeamsCount(int c) {
            this.teamsCount = c;
        }
    }

    public static class TeamStats {
        public int position;
        public String teamName;
        public int points;

        public TeamStats(int position, String teamName, int points) {
            this.position = position;
            this.teamName = teamName;
            this.points = points;
        }
    }

    public static class TournamentDTO {
        public Long id;
        public String name;
        public String status;
        public String badgeClass;
        public String type;
        public int teamsCount;

        public TournamentDTO(Long id, String name, String status, String badgeClass, String type, int teamsCount) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.badgeClass = badgeClass;
            this.type = type;
            this.teamsCount = teamsCount;
        }
    }

    public static class PlayerStats {
        public int position;
        public String playerName;
        public int goals; // Usamos este campo para goles
        public int assists; // O este para asistencias (Mustache usará el que llames)

        public PlayerStats(int position, String playerName, int statValue) {
            this.position = position;
            this.playerName = playerName;
            this.goals = statValue; // Truco: guardamos el valor en ambos para simplificar
            this.assists = statValue;
        }
    }
}