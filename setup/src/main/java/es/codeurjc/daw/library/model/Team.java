package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;


@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @Column(unique = true, nullable = false)
    private String username; 
    
    @Column(nullable = false)
    private String EncodedPassword;

    @Column(unique = true, nullable = false)
    private String email;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>(); 

    
    @Column(unique = true, nullable = false)
    private String teamName;
    

 
    // A team has many players
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players = new ArrayList<>();

    // A team can participate in multiple tournaments (and a tournament has multiple teams)
    @ManyToMany(mappedBy = "teams")
    private List<Tournament> tournaments = new ArrayList<>();

    // Matches where this team plays as home
    @OneToMany(mappedBy = "homeTeam")
    private List<Match> homeMatches = new ArrayList<>();

    // Matches where this team plays as away
    @OneToMany(mappedBy = "awayTeam")
    private List<Match> awayMatches = new ArrayList<>();

    
    public Team() {}

    public Team(String username, String email, String EncodedPassword, String teamName, Role... roles) {
        this.username = username;
        this.EncodedPassword = EncodedPassword;
        this.email = email;
        this.teamName = teamName;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public List<Match> getHomeMatches() {
        return homeMatches;
    }

    public void setHomeMatches(List<Match> homeMatches) {
        this.homeMatches = homeMatches;
    }

    public List<Match> getAwayMatches() {
        return awayMatches;
    }

    public void setAwayMatches(List<Match> awayMatches) {
        this.awayMatches = awayMatches;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
