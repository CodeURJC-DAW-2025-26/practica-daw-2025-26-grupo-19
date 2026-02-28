package es.codeurjc.daw.library.model;

public class EstadisticasEquipo {
    private Equipo equipo;
    private int puntos = 0;
    private int jugados = 0;
    private int victorias = 0;
    private int empates = 0;
    private int derrotas = 0;
    private int golesFavor = 0;
    private int golesContra = 0;

    public EstadisticasEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getJugados() {
        return jugados;
    }

    public int getVictorias() {
        return victorias;
    }

    public int getEmpates() {
        return empates;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getGolesFavor() {
        return golesFavor;
    }

    public int getGolesContra() {
        return golesContra;
    }

    public int getDiferenciaGoles() {
        return golesFavor - golesContra;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setJugados(int jugados) {
        this.jugados = jugados;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public void setGolesFavor(int golesFavor) {
        this.golesFavor = golesFavor;
    }

    public void setGolesContra(int golesContra) {
        this.golesContra = golesContra;
    }
}