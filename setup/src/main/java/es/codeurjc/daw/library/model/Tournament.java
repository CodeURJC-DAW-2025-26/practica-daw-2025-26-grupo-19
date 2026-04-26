package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Enumerated(EnumType.STRING)
    private TournamentType type; // "LIGA" or "ELIMINATORIA"
    
    @Enumerated(EnumType.STRING)
    private TournamentStatus status; // "INSCRIPCIONES", "EN_CURSO", "FINALIZADO"

    private int maxParticipants;

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

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    // A tournament has many teams, and a team can play in many tournaments
    @ManyToMany
    @JoinTable(
        name = "tournament_team", 
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();

    // A tournament is composed of many matches
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    public Tournament() {}

    public Tournament(String name, TournamentType type, TournamentStatus status, int maxParticipants) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.maxParticipants = maxParticipants;
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

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public List<TeamStatistics> getStandings() {
        // List where we will store the statistics for each team
        List<TeamStatistics> standings = new ArrayList<>();
        
        if (this.teams == null) return standings;

        for (Team team : this.teams) {
            TeamStatistics stats = new TeamStatistics(team);
            
            if (this.matches != null) {
                for (Match match : this.matches) {
                    // We only count the matches that have already been played
                    if (match.isPlayed()) {
                        boolean isHome = match.getHomeTeam().getId().equals(team.getId());
                        boolean isAway = match.getAwayTeam().getId().equals(team.getId());

                        // If the team participated in this match
                        if (isHome || isAway) {
                            stats.setMatchesPlayed(stats.getMatchesPlayed() + 1);

                            // Detect how many goals scored and conceded
                            int goalsFor = isHome ? match.getHomeGoals() : match.getAwayGoals();
                            int goalsAgainst = isHome ? match.getAwayGoals() : match.getHomeGoals();

                            stats.setGoalsFor(stats.getGoalsFor() + goalsFor);
                            stats.setGoalsAgainst(stats.getGoalsAgainst() + goalsAgainst);

                            // Calculate points and results (3 pts win, 1 pt draw)
                            if (goalsFor > goalsAgainst) {
                                stats.setWins(stats.getWins() + 1);
                                stats.setPoints(stats.getPoints() + 3);
                            } else if (goalsFor == goalsAgainst) {
                                stats.setDraws(stats.getDraws() + 1);
                                stats.setPoints(stats.getPoints() + 1);
                            } else {
                                stats.setLosses(stats.getLosses() + 1);
                            }
                        }
                    }
                }
            }
            standings.add(stats);
        }

        // Sort standings by points and goal difference
        standings.sort((a, b) -> {
            if (a.getPoints() != b.getPoints()) {
                return Integer.compare(b.getPoints(), a.getPoints());
            } else {
                return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            }
        });

        return standings;
    }

}
