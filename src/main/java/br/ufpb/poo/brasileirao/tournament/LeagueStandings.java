package br.ufpb.poo.brasileirao.tournament;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LeagueStandings {
    public static final int TOTAL_ROUNDS = 38; 
    public static final int MAX_TEAMS = 20;
    
    private String tournamentName;
    private List<TeamStanding> standings;
    private Map<String, TeamStanding> teamClassificationMap;
    private List<Match> matches;
    private int currentRound;
    
    public LeagueStandings(String tournamentName) {
        this.tournamentName = tournamentName;
        this.standings = new ArrayList<>();
        this.teamClassificationMap = new HashMap<>();
        this.matches = new ArrayList<>();
        this.currentRound = 0;
    }

    public void addTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }
        
        if (standings.size() >= MAX_TEAMS) {
            throw new IllegalStateException("Cannot add more than " + MAX_TEAMS + " teams to the tournament");
        }
        
        String teamName = team.getName();
        if (!teamClassificationMap.containsKey(teamName)) {
            TeamStanding classification = new TeamStanding(teamName);
            standings.add(classification);
            teamClassificationMap.put(teamName, classification);
        }
    }
    
    public void recordMatchResult(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null");
        }
        
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        
        // Ensure both teams are in the table
        if (!teamClassificationMap.containsKey(homeTeam.getName())) {
            addTeam(homeTeam);
        }
        if (!teamClassificationMap.containsKey(awayTeam.getName())) {
            addTeam(awayTeam);
        }
        
        // Update classification for both teams
        TeamStanding homeClassification = teamClassificationMap.get(homeTeam.getName());
        TeamStanding awayClassification = teamClassificationMap.get(awayTeam.getName());
        
        homeClassification.updateClassification(match.getHomeTeamGoals(), match.getAwayTeamGoals());
        awayClassification.updateClassification(match.getAwayTeamGoals(), match.getHomeTeamGoals());
        
        matches.add(match);
        
        // Sort the standings after each match update
        sortStandings();
    }
    
    private void sortStandings() {
        Collections.sort(standings);
    }
    
    //Getters
    public int getTeamPosition(String teamName) {
        if (!teamClassificationMap.containsKey(teamName)) {
            return -1;
        }
        
        TeamStanding classification = teamClassificationMap.get(teamName);
        return standings.indexOf(classification) + 1; 
    }
    
    public List<TeamStanding> getStandings() {
        return new ArrayList<>(standings);
    }
    
    public TeamStanding getTeamClassification(String teamName) {
        return teamClassificationMap.get(teamName);
    }

    public int getNumberOfTeams() {
        return standings.size();
    }
    
    public String getTournamentName() {
        return tournamentName;
    }
    
    public void setCurrentRound(int round) {
        if (round < 0) {
            throw new IllegalArgumentException("Round cannot be negative");
        }
        this.currentRound = round;
    }
    
    public int getCurrentRound() {
        return currentRound;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tournamentName).append(" - Round ").append(currentRound).append("\n");
        sb.append(String.format("%-4s %-20s %-5s %-5s %-5s %-5s %-5s %-5s %-5s\n", 
                "Pos", "Team", "P", "W", "D", "L", "GF", "GA", "GD"));
        
        int position = 1;
        for (TeamStanding c : standings) {
            sb.append(String.format("%-4d %-20s %-5d %-5d %-5d %-5d %-5d %-5d %-5d\n", 
                    position++, 
                    c.getTeamName(), 
                    c.getPoints(), 
                    c.getWins(), 
                    c.getDraws(), 
                    c.getLosses(),
                    c.getGoalsFor(),
                    c.getGoalsAgainst(),
                    c.getGoalDifference()));
        }
        
        return sb.toString();
    }
}
