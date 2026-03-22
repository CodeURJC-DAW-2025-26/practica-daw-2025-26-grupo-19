package es.codeurjc.daw.library.model;

public class TeamStatistics {
    private Team team;
    private int points = 0;
    private int matchesPlayed = 0;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;

    public TeamStatistics(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public int getPoints() {
        return points;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }
}
