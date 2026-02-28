package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;

@Entity
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    private String tipo; // "LIGA" o "ELIMINATORIA"
    
    private String estado; // "INSCRIPCIONES", "EN_CURSO", "FINALIZADO"

    private int maxParticipantes;

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

    public int getMaxParticipantes() {
        return maxParticipantes;
    }

    public void setMaxParticipantes(int maxParticipantes) {
        this.maxParticipantes = maxParticipantes;
    }

    // Un torneo tiene muchos equipos, y un equipo puede jugar muchos torneos
    @ManyToMany
    @JoinTable(
        name = "torneo_equipo", 
        joinColumns = @JoinColumn(name = "torneo_id"),
        inverseJoinColumns = @JoinColumn(name = "equipo_id")
    )
    private List<Equipo> equipos = new ArrayList<>();

    // Un torneo está compuesto por muchos partidos
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL)
    private List<Partido> partidos = new ArrayList<>();

    public Torneo() {}

    public Torneo(String nombre, String tipo, String estado, int maxParticipantes) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.maxParticipantes = maxParticipantes;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }

    public List<EstadisticasEquipo> getClasificacion() {
        List<EstadisticasEquipo> clasificacion = new ArrayList<>();
        
        if (this.equipos == null) return clasificacion;

        for (Equipo equipo : this.equipos) {
            EstadisticasEquipo stats = new EstadisticasEquipo(equipo);
            
            if (this.partidos != null) {
                for (Partido partido : this.partidos) {
                    if (partido.isJugado()) {
                        boolean esLocal = partido.getEquipoLocal().getId().equals(equipo.getId());
                        boolean esVisitante = partido.getEquipoVisitante().getId().equals(equipo.getId());

                        if (esLocal || esVisitante) {
                            stats.setJugados(stats.getJugados() + 1);
                            int golesFavor = esLocal ? partido.getGolesLocal() : partido.getGolesVisitante();
                            int golesContra = esLocal ? partido.getGolesVisitante() : partido.getGolesLocal();

                            stats.setGolesFavor(stats.getGolesFavor() + golesFavor);
                            stats.setGolesContra(stats.getGolesContra() + golesContra);

                            if (golesFavor > golesContra) {
                                stats.setVictorias(stats.getVictorias() + 1);
                                stats.setPuntos(stats.getPuntos() + 3);
                            } else if (golesFavor == golesContra) {
                                stats.setEmpates(stats.getEmpates() + 1);
                                stats.setPuntos(stats.getPuntos() + 1);
                            } else {
                                stats.setDerrotas(stats.getDerrotas() + 1);
                            }
                        }
                    }
                }
            }
            clasificacion.add(stats);
        }

        // Ordenar por puntos y diferencia de goles
        clasificacion.sort((a, b) -> {
            if (a.getPuntos() != b.getPuntos()) {
                return Integer.compare(b.getPuntos(), a.getPuntos());
            } else {
                return Integer.compare(b.getDiferenciaGoles(), a.getDiferenciaGoles());
            }
        });

        return clasificacion;
    }

}