package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.sql.Blob;


@Entity
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String posicion; 
    private int dorsal;
    
    // Estadísticas
    private int goles;
    private int asistencias;

    
    // Muchos jugadores pertenecen a un equipo
    @ManyToOne
    private Equipo equipo;

    public Jugador() {}

    public Jugador(String nombre, String posicion, int dorsal, Equipo equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.dorsal = dorsal;
        this.equipo = equipo;
        this.goles = 0;
        this.asistencias = 0;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        this.goles = goles;
    }

    public int getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(int asistencias) {
        this.asistencias = asistencias;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

}