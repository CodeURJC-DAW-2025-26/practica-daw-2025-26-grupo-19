package es.codeurjc.daw.library.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.io.InputStream;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Match;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.repository.TeamRepository;
import es.codeurjc.daw.library.repository.PlayerRepository;
import es.codeurjc.daw.library.repository.MatchRepository;
import es.codeurjc.daw.library.repository.TournamentRepository;

@Service
public class DatabaseInitializer {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // Security check: If there are already teams in the database,
        // We don't insert anything to avoid duplicates when restarting the server.
        if (teamRepository.count() > 0) {
            return;
        }

        // Load the default images from our files
        Blob defaultTeamBlob = createBlobFromResource("static/assets/images/equipo.jpg");
        Blob defaultLeagueBlob = createBlobFromResource("static/assets/images/liga.jpg");
        Blob defaultPlayerBlob = createBlobFromResource("static/assets/images/jugador.jpg");

        Team admin = new Team("admin", "admin@futbolmanager.com", passwordEncoder.encode("adminpass"), "Admin FC", "USER", "ADMIN");
        Team aguilas = new Team("user1", "user1@futbolmanager.com", passwordEncoder.encode("pass"), "Águilas Rojas", "USER");
        Team leones = new Team("user2", "user2@futbolmanager.com", passwordEncoder.encode("pass"), "Leones FC", "USER");
        Team tigres = new Team("user3", "user3@futbolmanager.com", passwordEncoder.encode("pass"), "Tigres Unidos", "USER");
        Team halcones = new Team("user4", "user4@futbolmanager.com", passwordEncoder.encode("pass"), "Halcones del Norte", "USER");
        Team dragones = new Team("user5", "user5@futbolmanager.com", passwordEncoder.encode("pass"), "Dragones FC", "USER");

        // Assign the image to all generated teams
        for (Team t : Arrays.asList(admin, aguilas, leones, tigres, halcones, dragones)) {
            if (defaultTeamBlob != null) {
                t.setImage(defaultTeamBlob);
                t.setHasImage(true);
            }
        }
        teamRepository.saveAll(Arrays.asList(admin, aguilas, leones, tigres, halcones, dragones));


        Tournament ligaPremium = new Tournament("Liga Premium", "LIGA", "En curso", 20);
        ligaPremium.getTeams().addAll(Arrays.asList(aguilas, leones, tigres, halcones, dragones));
        Tournament ligaNorte = new Tournament("Liga Regional Norte", "LIGA", "En curso",20);
        Tournament ligaAmateur = new Tournament("Liga Amateur", "LIGA", "Inscripciones abiertas", 20);
        Tournament copaCampeones = new Tournament("Copa de Campeones", "ELIMINATORIA", "En curso", 10);
        copaCampeones.getTeams().addAll(Arrays.asList(aguilas, leones, tigres, halcones));
        Tournament tournamentApertura = new Tournament("Torneo Apertura 2026", "LIGA", "Inscripciones abiertas",10);

        // Assign image to leagues/tournaments
        for (Tournament tor : Arrays.asList(ligaPremium, ligaNorte, ligaAmateur, copaCampeones, tournamentApertura)) {
            if (defaultLeagueBlob != null) {
                tor.setImage(defaultLeagueBlob);
                tor.setHasImage(true);
            }
        }
        tournamentRepository.saveAll(Arrays.asList(ligaPremium, ligaNorte, ligaAmateur, copaCampeones, tournamentApertura));


        // Top scorers
        Player cr7 = new Player("Cristiano Ronaldo", "Delantero", 7, aguilas); 
        cr7.setGoals(15);
        Player victor = new Player("Víctor Blade", "Delantero", 10, leones); 
        victor.setGoals(14);
        Player vini = new Player("Vini Jr", "Delantero", 11, tigres); 
        vini.setGoals(13);
        Player axel = new Player("Axel Blaze", "Delantero", 10, halcones); 
        axel.setGoals(12);
        Player shawn = new Player("Shawn Froste", "Delantero", 9, dragones); 
        shawn.setGoals(11);

        // Top assist providers
        Player james = new Player("James Rodríguez", "Centrocampista", 10, aguilas); 
        james.setAssists(13);
        Player willy = new Player("Willy Glass", "Centrocampista", 8, leones); 
        willy.setAssists(12);
        Player modric = new Player("Luka Modric", "Centrocampista", 10, tigres); 
        modric.setAssists(11);
        Player kroos = new Player("Toni Kroos", "Centrocampista", 8, halcones); 
        kroos.setAssists(10);
        Player iniesta = new Player("Andrés Iniesta", "Centrocampista", 8, dragones); 
        iniesta.setAssists(9);

        // Assign image to players
        for (Player p : Arrays.asList(cr7, victor, vini, axel, shawn, james, willy, modric, kroos, iniesta)) {
            if (defaultPlayerBlob != null) {
                p.setImage(defaultPlayerBlob);
                p.setHasImage(true);
            }
        }
        playerRepository.saveAll(Arrays.asList(cr7, victor, vini, axel, shawn, james, willy, modric, kroos, iniesta));


        Match p1 = new Match(ligaPremium, aguilas, leones, LocalDateTime.now().minusDays(2));
        p1.setPlayed(true); p1.setHomeGoals(2); p1.setAwayGoals(1);
        Match p2 = new Match(ligaPremium, tigres, halcones, LocalDateTime.now().minusDays(1));
        p2.setPlayed(true); p2.setHomeGoals(0); p2.setAwayGoals(0);
        Match p3 = new Match(copaCampeones, dragones, aguilas, LocalDateTime.now().plusDays(3));

        matchRepository.saveAll(Arrays.asList(p1, p2, p3));
    }

    // Helper method for loading images to MySQL blob format
    private Blob createBlobFromResource(String imagePath) {
        try {
            Resource resource = new ClassPathResource(imagePath);
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            return new SerialBlob(bytes);
        } catch (Exception e) {
            System.out.println("⚠️ WARNING: Could not load default image: " + imagePath);
            return null;
        }
    }
}