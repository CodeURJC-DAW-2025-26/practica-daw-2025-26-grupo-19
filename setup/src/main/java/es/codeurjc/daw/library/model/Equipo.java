package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;


@Entity
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @Column(unique = true, nullable = false)
    private String username; 
    
    @Column(nullable = false)
    private String EncodedPassword;

    private String email;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>(); 

    
    @Column(unique = true, nullable = false)
    private String nombreEquipo;
    

 
    // Un equipo tiene muchos jugadores
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> jugadores = new ArrayList<>();

    // Un equipo puede participar en varios torneos (y un torneo tiene varios equipos)
    @ManyToMany(mappedBy = "equipos")
    private List<Torneo> torneos = new ArrayList<>();

    // Partidos donde juega como local
    @OneToMany(mappedBy = "equipoLocal")
    private List<Partido> partidosLocal = new ArrayList<>();

    // Partidos donde juega como visitante
    @OneToMany(mappedBy = "equipoVisitante")
    private List<Partido> partidosVisitante = new ArrayList<>();

    
    public Equipo() {}

    public Equipo(String username, String email, String EncodedPassword, String nombreEquipo, String... roles) {
        this.username = username;
        this.EncodedPassword = EncodedPassword;
        this.email = email;
        this.nombreEquipo = nombreEquipo;
        this.roles = List.of(roles);
    }

    @Column(name = "reset_password_token")
    private String resetPasswordToken;
    
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    @Lob
    private Blob imagen;

    private boolean hasImagen;
    
    public boolean isHasImagen() {
        return hasImagen;
    }

    public void setHasImagen(boolean hasImagen) {
        this.hasImagen = hasImagen;
    }

    public Blob getImagen() {
        return imagen;
    }

    public void setImagen(Blob imagen) {
        this.imagen = imagen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncodedPassword() {
        return EncodedPassword;
    }

    public void setEncodedPassword(String EncodedPassword) {
        this.EncodedPassword = EncodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<Torneo> torneos) {
        this.torneos = torneos;
    }

    public List<Partido> getPartidosLocal() {
        return partidosLocal;
    }

    public void setPartidosLocal(List<Partido> partidosLocal) {
        this.partidosLocal = partidosLocal;
    }

    public List<Partido> getPartidosVisitante() {
        return partidosVisitante;
    }

    public void setPartidosVisitante(List<Partido> partidosVisitante) {
        this.partidosVisitante = partidosVisitante;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}