package es.codeurjc.daw.library.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import es.codeurjc.daw.library.model.Role;
import es.codeurjc.daw.library.model.Player;
import es.codeurjc.daw.library.model.Match;
import es.codeurjc.daw.library.model.Tournament;
import es.codeurjc.daw.library.model.TournamentStatus;
import es.codeurjc.daw.library.model.TournamentType;
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

        // --- TEAMS/ USERS ---
        Team admin = new Team("admin", "admin@futbolmanager.com", passwordEncoder.encode("adminpass"), "Admin FC", Role.USER, Role.ADMIN);
        Team aguilas = new Team("user1", "user1@futbolmanager.com", passwordEncoder.encode("pass"), "Águilas Rojas", Role.USER);
        Team leones = new Team("user2", "user2@futbolmanager.com", passwordEncoder.encode("pass"), "Leones FC", Role.USER);
        Team tigres = new Team("user3", "user3@futbolmanager.com", passwordEncoder.encode("pass"), "Tigres Unidos", Role.USER);
        Team halcones = new Team("user4", "user4@futbolmanager.com", passwordEncoder.encode("pass"), "Halcones del Norte", Role.USER);
        Team dragones = new Team("user5", "user5@futbolmanager.com", passwordEncoder.encode("pass"), "Dragones FC", Role.USER);
        Team pumas = new Team("user6", "user6@futbolmanager.com", passwordEncoder.encode("pass"), "Pumas Plateados", Role.USER);
        Team lobos = new Team("user7", "user7@futbolmanager.com", passwordEncoder.encode("pass"), "Lobos Negros", Role.USER);
        Team hormigas = new Team("user8", "user8@futbolmanager.com", passwordEncoder.encode("pass"), "Hormigas Rojas", Role.USER);
        Team tiburones = new Team("user9", "user9@futbolmanager.com", passwordEncoder.encode("pass"), "Tiburones del Sur", Role.USER);
        Team osos = new Team("user10", "user10@futbolmanager.com", passwordEncoder.encode("pass"), "Osos Grises", Role.USER);
        Team panteras = new Team("user11", "user11@futbolmanager.com", passwordEncoder.encode("pass"), "Panteras Rosas", Role.USER);

        // Assign the image to all generated teams
        for (Team t : Arrays.asList(admin, aguilas, leones, tigres, halcones, dragones, pumas, lobos, hormigas, tiburones, osos, panteras)) {
            if (defaultTeamBlob != null) {
                t.setImage(defaultTeamBlob);
                t.setHasImage(true);
            }
        }
        teamRepository.saveAll(Arrays.asList(admin, aguilas, leones, tigres, halcones, dragones, pumas, lobos, hormigas, tiburones, osos, panteras));

        // --- TOURNAMENTS ---
        Tournament ligaPremium = new Tournament("Liga Premium", TournamentType.LIGA, TournamentStatus.EN_CURSO, 20);
        Tournament ligaNorte = new Tournament("Liga Regional Norte", TournamentType.LIGA, TournamentStatus.EN_CURSO,20);
        Tournament ligaAmateur = new Tournament("Liga Amateur", TournamentType.LIGA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 20);
        Tournament copaCampeones = new Tournament("Copa de Campeones", TournamentType.ELIMINATORIA, TournamentStatus.EN_CURSO, 10);
        Tournament tournamentApertura = new Tournament("Torneo Apertura 2026", TournamentType.LIGA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 10);
        Tournament ligaSur = new Tournament("Liga Regional Sur", TournamentType.LIGA, TournamentStatus.EN_CURSO, 20);
        Tournament copaRey = new Tournament("Copa del Rey", TournamentType.ELIMINATORIA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 16);
        Tournament torneoClausura = new Tournament("Torneo Clausura 2026", TournamentType.LIGA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 10);
        Tournament supercopa = new Tournament("Supercopa Nacional", TournamentType.ELIMINATORIA, TournamentStatus.FINALIZADO, 4);
        Tournament ligaInvierno = new Tournament("Liga de Invierno", TournamentType.LIGA, TournamentStatus.FINALIZADO, 12);
        Tournament torneoVerano = new Tournament("Torneo de Verano", TournamentType.LIGA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 16);
        Tournament ligaPromesas = new Tournament("Liga Promesas", TournamentType.LIGA, TournamentStatus.EN_CURSO, 10);
        Tournament copaIntercontinental = new Tournament("Copa Intercontinental", TournamentType.ELIMINATORIA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 8);
        Tournament torneoDistrito = new Tournament("Torneo de Distrito", TournamentType.ELIMINATORIA, TournamentStatus.INSCRIPCIONES_ABIERTAS, 16);

        // --- ENROLL TEAMS IN TOURNAMENTS ---
        // EN_CURSO with matches
        ligaPremium.getTeams().addAll(Arrays.asList(aguilas, leones, tigres, halcones, dragones, pumas, lobos));
        ligaNorte.getTeams().addAll(Arrays.asList(hormigas, tiburones, osos, panteras));
        copaCampeones.getTeams().addAll(Arrays.asList(aguilas, leones, tigres, halcones, dragones));
        ligaSur.getTeams().addAll(Arrays.asList(osos, panteras, pumas, tigres));
        
        // EN_CURSO with 10 teams and NO matches yet
        ligaPromesas.getTeams().addAll(Arrays.asList(aguilas, leones, tigres, halcones, dragones, pumas, lobos, hormigas, tiburones, osos));

        // FINALIZADO with teams and played matches
        supercopa.getTeams().addAll(Arrays.asList(admin, aguilas, leones, tigres));
        ligaInvierno.getTeams().addAll(Arrays.asList(pumas, lobos, hormigas, tiburones));
        
        // INSCRIPCIONES_ABIERTAS with some teams joining but no matches
        ligaAmateur.getTeams().addAll(Arrays.asList(admin, aguilas, leones));
        torneoVerano.getTeams().addAll(Arrays.asList(osos, panteras));

        // Assign image to leagues/tournaments
        for (Tournament tor : Arrays.asList(ligaPremium, ligaNorte, ligaAmateur, copaCampeones, tournamentApertura, 
                                            ligaSur, copaRey, torneoClausura, supercopa, ligaInvierno, torneoVerano, 
                                            copaIntercontinental, torneoDistrito, ligaPromesas)) {
            if (defaultLeagueBlob != null) {
                tor.setImage(defaultLeagueBlob);
                tor.setHasImage(true);
            }
        }
        tournamentRepository.saveAll(Arrays.asList(ligaPremium, ligaNorte, ligaAmateur, copaCampeones, tournamentApertura,
                                                   ligaSur, copaRey, torneoClausura, supercopa, ligaInvierno, torneoVerano, 
                                                   copaIntercontinental, torneoDistrito, ligaPromesas));


        // --- PLAYERS ---
        // PLAYERS OF Aguilas Rojas / user1
        Player cr7 = new Player("Cristiano Ronaldo", "Delantero", 7, aguilas); cr7.setGoals(15);
        Player james = new Player("James Rodríguez", "Centrocampista", 10, aguilas); james.setAssists(13);
        Player iker = new Player("Iker Casillas", "Portero", 1, aguilas);
        Player carva = new Player("Dani Carvajal", "Defensa", 2, aguilas);
        Player pepe = new Player("Pepe", "Defensa", 3, aguilas);
        Player ramos = new Player("Sergio Ramos", "Defensa", 4, aguilas);
        Player marcelo = new Player("Marcelo", "Defensa", 12, aguilas);
        Player xabi = new Player("Xabi Alonso", "Centrocampista", 14, aguilas);
        Player dimaria = new Player("Ángel Di María", "Centrocampista", 22, aguilas);
        Player bale = new Player("Gareth Bale", "Delantero", 11, aguilas);
        Player benzema = new Player("Karim Benzema", "Delantero", 9, aguilas);

        // PLAYERS OF Admin FC / admin
        Player benji = new Player("Benji Price", "Portero", 1, admin);
        Player harper = new Player("Bruce Harper", "Defensa", 4, admin);
        Player paul = new Player("Paul Diamond", "Defensa", 5, admin);
        Player yuma = new Player("Clifford Yuma", "Defensa", 6, admin);
        Player callahan = new Player("Philip Callahan", "Centrocampista", 8, admin);
        Player baker = new Player("Tom Baker", "Centrocampista", 11, admin);
        Player atom = new Player("Oliver Atom", "Centrocampista", 10, admin); atom.setGoals(20); atom.setAssists(10);
        Player mellow = new Player("Danny Mellow", "Centrocampista", 15, admin);
        Player lenders = new Player("Mark Lenders", "Delantero", 9, admin); lenders.setGoals(22); lenders.setAssists(8);
        Player ross = new Player("Julian Ross", "Delantero", 14, admin);
        Player warner = new Player("Ed Warner", "Portero", 22, admin);

        // OTHER TEAMS PLAYERS
        Player victor = new Player("Víctor Blade", "Delantero", 10, leones); victor.setGoals(14);
        Player vini = new Player("Vini Jr", "Delantero", 11, tigres); vini.setGoals(13);
        Player axel = new Player("Axel Blaze", "Delantero", 10, halcones); axel.setGoals(12);
        Player shawn = new Player("Shawn Froste", "Delantero", 9, dragones); shawn.setGoals(11);
        Player willy = new Player("Willy Glass", "Centrocampista", 8, leones); willy.setAssists(12);
        Player modric = new Player("Luka Modric", "Centrocampista", 10, tigres); modric.setAssists(11);
        Player kroos = new Player("Toni Kroos", "Centrocampista", 8, halcones); kroos.setAssists(10);
        Player iniesta = new Player("Andrés Iniesta", "Centrocampista", 8, dragones); iniesta.setAssists(9);
        Player messi = new Player("Lionel Messi", "Delantero", 10, pumas); messi.setGoals(10); messi.setAssists(15);
        Player mbappe = new Player("Kylian Mbappé", "Delantero", 9, lobos); mbappe.setGoals(12);
        Player pedri = new Player("Pedri González", "Centrocampista", 8, hormigas); pedri.setAssists(8);
        Player courtois = new Player("Thibaut Courtois", "Portero", 1, tiburones);
        Player rudiger = new Player("Antonio Rüdiger", "Defensa", 22, osos);
        Player mark = new Player("Mark Evans", "Portero", 1, panteras);
        Player jude = new Player("Jude Sharp", "Centrocampista", 14, pumas); jude.setAssists(7);

        List<Player> allPlayers = new ArrayList<>(Arrays.asList(
            cr7, james, iker, carva, pepe, ramos, marcelo, xabi, dimaria, bale, benzema,
            benji, harper, paul, yuma, callahan, baker, atom, mellow, lenders, ross, warner,
            victor, vini, axel, shawn, willy, modric, kroos, iniesta,
            messi, mbappe, pedri, courtois, rudiger, mark, jude
        ));

        // Assign image to players
        for (Player p : allPlayers) {
            if (defaultPlayerBlob != null) {
                p.setImage(defaultPlayerBlob);
                p.setHasImage(true);
            }
        }
        playerRepository.saveAll(allPlayers);

        
        // --- MATCHES ---
        
        // Liga Premium (EN_CURSO)
        Match p1 = new Match(ligaPremium, aguilas, leones, LocalDateTime.now().minusDays(2));
        p1.setPlayed(true); p1.setHomeGoals(2); p1.setAwayGoals(1);
        Match p2 = new Match(ligaPremium, tigres, halcones, LocalDateTime.now().minusDays(1));
        p2.setPlayed(true); p2.setHomeGoals(0); p2.setAwayGoals(0);
        Match p4 = new Match(ligaPremium, pumas, lobos, LocalDateTime.now().plusDays(3)); // Aún por jugar
        
        // Copa Campeones (EN_CURSO)
        Match p3 = new Match(copaCampeones, dragones, aguilas, LocalDateTime.now().plusDays(3));
        
        // Liga Norte (EN_CURSO)
        Match p5 = new Match(ligaNorte, hormigas, tiburones, LocalDateTime.now().plusDays(2)); // Movido aquí (antes en Amateur)
        
        // Liga Sur (EN_CURSO)
        Match p6 = new Match(ligaSur, osos, panteras, LocalDateTime.now().minusDays(5));
        p6.setPlayed(true); p6.setHomeGoals(1); p6.setAwayGoals(2);
        Match p7 = new Match(ligaSur, pumas, tigres, LocalDateTime.now().plusDays(7)); // Movido aquí (antes en Verano)

        // Supercopa (FINALIZADO) - All played
        Match s1 = new Match(supercopa, admin, aguilas, LocalDateTime.now().minusDays(10));
        s1.setPlayed(true); s1.setHomeGoals(3); s1.setAwayGoals(0);
        Match s2 = new Match(supercopa, leones, tigres, LocalDateTime.now().minusDays(10));
        s2.setPlayed(true); s2.setHomeGoals(1); s2.setAwayGoals(2);
        Match s3 = new Match(supercopa, admin, tigres, LocalDateTime.now().minusDays(7));
        s3.setPlayed(true); s3.setHomeGoals(2); s3.setAwayGoals(1);

        // Liga de Invierno (FINALIZADO) - All played
        Match i1 = new Match(ligaInvierno, pumas, lobos, LocalDateTime.now().minusDays(30));
        i1.setPlayed(true); i1.setHomeGoals(2); i1.setAwayGoals(2);
        Match i2 = new Match(ligaInvierno, hormigas, tiburones, LocalDateTime.now().minusDays(28));
        i2.setPlayed(true); i2.setHomeGoals(0); i2.setAwayGoals(3);

        matchRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, s1, s2, s3, i1, i2));
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