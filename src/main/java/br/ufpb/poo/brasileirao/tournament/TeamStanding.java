package br.ufpb.poo.brasileirao.tournament;

public class TeamStanding implements Comparable<TeamStanding> {
    private final String teamName;
    private int points;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    public TeamStanding(String teamName) {
        this.teamName = teamName;
        this.points = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
    }

    // Getters
    public String getTeamName() {
        return teamName;
    }
    
    public int getPoints() {
        return points;
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
    
    public int getMatchesPlayed() {
        return wins + draws + losses;
    }
    
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
    

    public void updateClassification(int goalsFor, int goalsAgainst) {
        if (goalsFor < 0 || goalsAgainst < 0) {
            throw new IllegalArgumentException("Goals cannot be negative");
        }
        
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        
        if (goalsFor > goalsAgainst) {
            this.wins++;
            this.points += 3;
        } else if (goalsFor == goalsAgainst) {
            this.draws++;
            this.points += 1;
        } else {
            this.losses++;
        }
    }
    
    @Override
    public int compareTo(TeamStanding other) {
        // First criterion: points (descending)
        if (other.points != this.points) {
            return other.points - this.points;
        }
        
        // Second criterion: wins (descending)
        if (other.wins != this.wins) {
            return other.wins - this.wins;
        }
        
        // Third criterion: goal difference (descending)
        int thisGoalDiff = this.getGoalDifference();
        int otherGoalDiff = other.getGoalDifference();
        if (otherGoalDiff != thisGoalDiff) {
            return otherGoalDiff - thisGoalDiff;
        }
        
        // Fourth criterion: goals scored (descending)
        return other.goalsFor - this.goalsFor;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %d points, %dJ %dV %dE %dD, %d:%d (%+d)",
                teamName, points, 
                getMatchesPlayed(), wins, draws, losses,
                goalsFor, goalsAgainst, getGoalDifference());
    }
}
