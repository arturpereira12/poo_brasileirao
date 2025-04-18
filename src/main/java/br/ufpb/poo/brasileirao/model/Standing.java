package br.ufpb.poo.brasileirao.model;

/**
 * Classe que representa a posição de um time na tabela de classificação.
 * @deprecated Use br.ufpb.poo.brasileirao.tournament.LeagueStandings.TeamStats em vez desta classe.
 */
@Deprecated
public class Standing {
    private Team team;
    private int points;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;

    public Standing(Team team) {
        this.team = team;
        this.points = 0;
        this.played = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.goalDifference = 0;
    }

    // Getters e Setters
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public int getPlayed() { return played; }
    public void setPlayed(int played) { this.played = played; }
    
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    
    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }
    
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
    
    public int getGoalsFor() { return goalsFor; }
    public void setGoalsFor(int goalsFor) { 
        this.goalsFor = goalsFor;
        updateGoalDifference();
    }
    
    public int getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { 
        this.goalsAgainst = goalsAgainst;
        updateGoalDifference();
    }
    
    public int getGoalDifference() { return goalDifference; }
    
    private void updateGoalDifference() {
        this.goalDifference = this.goalsFor - this.goalsAgainst;
    }

    public String getName() {
        return team.getName();
    }
} 