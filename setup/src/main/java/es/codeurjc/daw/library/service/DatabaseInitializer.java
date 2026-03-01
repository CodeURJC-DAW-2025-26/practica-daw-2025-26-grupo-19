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

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.model.Jugador;
import es.codeurjc.daw.library.model.Partido;
import es.codeurjc.daw.library.model.Torneo;
import es.codeurjc.daw.library.repository.EquipoRepository;
import es.codeurjc.daw.library.repository.JugadorRepository;
import es.codeurjc.daw.library.repository.PartidoRepository;
import es.codeurjc.daw.library.repository.TorneoRepository;

@Service
public class DatabaseInitializer {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // Security check: If there are already teams in the database,
        // We dont insert anything to avoid duplicates when restarting the server.
        if (equipoRepository.count() > 0) {
            return;
        }

        // Load the images by default from our files
        Blob defaultEquipoBlob = createBlobFromResource("static/assets/images/equipo.jpg");
        Blob defaultLigaBlob = createBlobFromResource("static/assets/images/liga.jpg");
        Blob defaultJugadorBlob = createBlobFromResource("static/assets/images/jugador.jpg");

        Equipo admin = new Equipo("admin", "admin@futbolmanager.com", passwordEncoder.encode("adminpass"), "Admin FC", "USER", "ADMIN");
        Equipo aguilas = new Equipo("user1", "user1@futbolmanager.com", passwordEncoder.encode("pass"), "Águilas Rojas", "USER");
        Equipo leones = new Equipo("user2", "user2@futbolmanager.com", passwordEncoder.encode("pass"), "Leones FC", "USER");
        Equipo tigres = new Equipo("user3", "user3@futbolmanager.com", passwordEncoder.encode("pass"), "Tigres Unidos", "USER");
        Equipo halcones = new Equipo("user4", "user4@futbolmanager.com", passwordEncoder.encode("pass"), "Halcones del Norte", "USER");
        Equipo dragones = new Equipo("user5", "user5@futbolmanager.com", passwordEncoder.encode("pass"), "Dragones FC", "USER");

        // Assign the image to all generated teams
        for (Equipo eq : Arrays.asList(admin, aguilas, leones, tigres, halcones, dragones)) {
            if (defaultEquipoBlob != null) {
                eq.setImagen(defaultEquipoBlob);
                eq.setHasImagen(true);
            }
        }
        equipoRepository.saveAll(Arrays.asList(admin, aguilas, leones, tigres, halcones, dragones));


        Torneo ligaPremium = new Torneo("Liga Premium", "LIGA", "En curso", 20);
        ligaPremium.getEquipos().addAll(Arrays.asList(aguilas, leones, tigres, halcones, dragones));
        Torneo ligaNorte = new Torneo("Liga Regional Norte", "LIGA", "En curso",20);
        Torneo ligaAmateur = new Torneo("Liga Amateur", "LIGA", "Inscripciones abiertas", 20);
        Torneo copaCampeones = new Torneo("Copa de Campeones", "ELIMINATORIA", "En curso", 10);
        copaCampeones.getEquipos().addAll(Arrays.asList(aguilas, leones, tigres, halcones));
        Torneo torneoApertura = new Torneo("Torneo Apertura 2026", "LIGA", "Inscripciones abiertas",10);

        // Assign image to leagues/tournaments
        for (Torneo tor : Arrays.asList(ligaPremium, ligaNorte, ligaAmateur, copaCampeones, torneoApertura)) {
            if (defaultLigaBlob != null) {
                tor.setImagen(defaultLigaBlob);
                tor.setHasImagen(true);
            }
        }
        torneoRepository.saveAll(Arrays.asList(ligaPremium, ligaNorte, ligaAmateur, copaCampeones, torneoApertura));


        // Top scorers
        Jugador cr7 = new Jugador("Cristiano Ronaldo", "Delantero", 7, aguilas); 
        cr7.setGoles(15);
        Jugador victor = new Jugador("Víctor Blade", "Delantero", 10, leones); 
        victor.setGoles(14);
        Jugador vini = new Jugador("Vini Jr", "Delantero", 11, tigres); 
        vini.setGoles(13);
        Jugador axel = new Jugador("Axel Blaze", "Delantero", 10, halcones); 
        axel.setGoles(12);
        Jugador shawn = new Jugador("Shawn Froste", "Delantero", 9, dragones); 
        shawn.setGoles(11);

        // Top assist providers
        Jugador james = new Jugador("James Rodríguez", "Centrocampista", 10, aguilas); 
        james.setAsistencias(13);
        Jugador willy = new Jugador("Willy Glass", "Centrocampista", 8, leones); 
        willy.setAsistencias(12);
        Jugador modric = new Jugador("Luka Modric", "Centrocampista", 10, tigres); 
        modric.setAsistencias(11);
        Jugador kroos = new Jugador("Toni Kroos", "Centrocampista", 8, halcones); 
        kroos.setAsistencias(10);
        Jugador iniesta = new Jugador("Andrés Iniesta", "Centrocampista", 8, dragones); 
        iniesta.setAsistencias(9);

        // Assign image to players
        for (Jugador jug : Arrays.asList(cr7, victor, vini, axel, shawn, james, willy, modric, kroos, iniesta)) {
            if (defaultJugadorBlob != null) {
                jug.setImagen(defaultJugadorBlob);
                jug.setHasImagen(true);
            }
        }
        jugadorRepository.saveAll(Arrays.asList(cr7, victor, vini, axel, shawn, james, willy, modric, kroos, iniesta));


        Partido p1 = new Partido(ligaPremium, aguilas, leones, LocalDateTime.now().minusDays(2));
        p1.setJugado(true); p1.setGolesLocal(2); p1.setGolesVisitante(1);
        Partido p2 = new Partido(ligaPremium, tigres, halcones, LocalDateTime.now().minusDays(1));
        p2.setJugado(true); p2.setGolesLocal(0); p2.setGolesVisitante(0);
        Partido p3 = new Partido(copaCampeones, dragones, aguilas, LocalDateTime.now().plusDays(3));

        partidoRepository.saveAll(Arrays.asList(p1, p2, p3));
    }

    // Method tool for uploading images to mysql blob format
    private Blob createBlobFromResource(String imagePath) {
        try {
            Resource resource = new ClassPathResource(imagePath);
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            return new SerialBlob(bytes);
        } catch (Exception e) {
            System.out.println("⚠️ ATENCIÓN: No se pudo cargar la imagen por defecto: " + imagePath);
            return null;
        }
    }
}