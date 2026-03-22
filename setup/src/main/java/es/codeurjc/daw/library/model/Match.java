package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "game_match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    
    private int homeGoals;
    private int awayGoals;
    
    private boolean played; // true if already finished, false if pending

   
    // A match belongs to a specific tournament
    @ManyToOne
    private Tournament tournament;

    // Home team
    @ManyToOne
    private Team homeTeam;

    // Away team
    @ManyToOne
    private Team awayTeam;

    public Match() {}

    public Match(Tournament tournament, Team homeTeam, Team awayTeam, LocalDateTime date) {
        this.tournament = tournament;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.played = false; 
        this.homeGoals = 0;
        this.awayGoals = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getFormattedDate() {
        if (this.date == null) return "Sin fecha";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.date.format(formatter);
    }

}
