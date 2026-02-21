package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    
    private int golesLocal;
    private int golesVisitante;
    
    private boolean jugado; // true si ya terminó, false si está pendiente

   
    // Un partido pertenece a un torneo concreto
    @ManyToOne
    private Torneo torneo;

    // Equipo que juega en casa
    @ManyToOne
    private Equipo equipoLocal;

    // Equipo que juega fuera
    @ManyToOne
    private Equipo equipoVisitante;

    public Partido() {}

    public Partido(Torneo torneo, Equipo equipoLocal, Equipo equipoVisitante, LocalDateTime fecha) {
        this.torneo = torneo;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fecha = fecha;
        this.jugado = false; 
        this.golesLocal = 0;
        this.golesVisitante = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public boolean isJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

}