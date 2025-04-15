package br.ufpb.poo.brasileirao.match;

import br.ufpb.poo.brasileirao.model.Team;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class responsible for storing the results of a football match
 */
public class Match {
    private Team homeTeam;
    private Team awayTeam;
    private int homeTeamGoals;
    private int awayTeamGoals;
    private Date matchDate;
    private List<String> goalScorers;
    
    /**
     * Match class constructor
     * @param homeTeam Team playing at home
     * @param awayTeam Visiting team
     */
    public Match(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamGoals = 0;
        this.awayTeamGoals = 0;
        this.matchDate = new Date(); // Current date
        this.goalScorers = new ArrayList<>();
    }
    
    /**
     * Adds a goal for the home team and registers the player who scored
     * @param playerName Name of the player who scored the goal
     */
    public void addHomeTeamGoal(String playerName) {
        this.homeTeamGoals++;
        this.goalScorers.add(playerName + " (" + homeTeam.getName() + ")");
    }
    
    /**
     * Adds a goal for the away team and registers the player who scored
     * @param playerName Name of the player who scored the goal
     */
    public void addAwayTeamGoal(String playerName) {
        this.awayTeamGoals++;
        this.goalScorers.add(playerName + " (" + awayTeam.getName() + ")");
    }
    
    /**
     * Returns a string with the match score
     * @return Formatted string with the score
     */
    public String getScore() {
        return homeTeam.getName() + " " + homeTeamGoals + " x " + 
               awayTeamGoals + " " + awayTeam.getName();
    }
    
    /**
     * @return List with the names of players who scored goals
     */
    public List<String> getGoalScorers() {
        return goalScorers;
    }
    
    /**
     * @return Home team
     */
    public Team getHomeTeam() {
        return homeTeam;
    }
    
    /**
     * @return Away team
     */
    public Team getAwayTeam() {
        return awayTeam;
    }
    
    /**
     * @return Number of goals scored by home team
     */
    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }
    
    /**
     * @return Number of goals scored by away team
     */
    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }
    
    /**
     * @return Date when the match was played
     */
    public Date getMatchDate() {
        return matchDate;
    }
    
    /**
     * Sets the match date
     * @param matchDate New match date
     */
    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }
    
    /**
     * Checks if the match ended in a draw
     * @return true if it was a draw, false otherwise
     */
    public boolean isDraw() {
        return homeTeamGoals == awayTeamGoals;
    }
    
    /**
     * Returns the winning team
     * @return Winner team or null in case of a draw
     */
    public Team getWinner() {
        if (homeTeamGoals > awayTeamGoals) {
            return homeTeam;
        } else if (awayTeamGoals > homeTeamGoals) {
            return awayTeam;
        } else {
            return null; // Draw
        }
    }
}
