package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.sql.Blob;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position; 
    private int jerseyNumber;
    
    // Statistics
    private int goals;
    private int assists;

    
    // Many players belong to a team
    @ManyToOne
    private Team team;

    public Player() {}

    public Player(String name, String position, int jerseyNumber, Team team) {
        this.name = name;
        this.position = position;
        this.jerseyNumber = jerseyNumber;
        this.team = team;
        this.goals = 0;
        this.assists = 0;
    }
    
    @Lob
    private Blob image;

    private boolean hasImage;
    
    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
