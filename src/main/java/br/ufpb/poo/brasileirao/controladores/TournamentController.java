package br.ufpb.poo.brasileirao.controladores;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.match.MatchSimulator;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable;

import java.util.*;
/**
 * Controls the tournament simulation including schedule generation,
 * match simulation, and statistics tracking.
 */
public class TournamentController {

    private static final int TOTAL_ROUNDS = 38;
    private static final int REQUIRED_TEAMS = 20;
    
    private String tournamentName;
    private List<Team> teams;
    private List<List<Match>> schedule; // List of rounds, each containing matches
    private LeagueStandings leagueStandings;
    private TopScorersTable topScorers;
    private int currentRound;
    private boolean tournamentStarted;
    private boolean tournamentFinished;
    
    /**
     * Creates a new tournament controller
     * @param tournamentName Name of the tournament
     */
    public TournamentController(String tournamentName) {
        this.tournamentName = tournamentName;
        this.teams = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.leagueStandings = new LeagueStandings(tournamentName);
        this.topScorers = new TopScorersTable();
        this.currentRound = 0;
        this.tournamentStarted = false;
        this.tournamentFinished = false;
    }
    
    /**
     * Adds a team to the tournament
     * @param team Team to add
     * @return true if added successfully, false if already exists or tournament started
     */
    public boolean addTeam(Team team) {
        if (tournamentStarted || team == null || teams.contains(team)) {
            return false;
        }
        
        if (teams.size() >= REQUIRED_TEAMS) {
            throw new IllegalStateException("Cannot add more than " + REQUIRED_TEAMS + " teams to the tournament");
        }
        
        teams.add(team);
        leagueStandings.addTeam(team);
        return true;
    }
    
    /**
     * Adds multiple teams to the tournament
     * @param teamList List of teams to add
     */
    public void addTeams(List<Team> teamList) {
        if (tournamentStarted) {
            throw new IllegalStateException("Cannot add teams after tournament has started");
        }
        
        for (Team team : teamList) {
            addTeam(team);
        }
    }
    
    /**
     * Generates the tournament schedule with all 38 rounds
     * @return True if schedule generated successfully
     */
    public boolean generateSchedule() {
        if (tournamentStarted) {
            throw new IllegalStateException("Cannot regenerate schedule after tournament has started");
        }
        
        if (teams.size() != REQUIRED_TEAMS) {
            throw new IllegalStateException(
                "Exactly " + REQUIRED_TEAMS + " teams required to generate schedule, but got " + teams.size());
        }
        
        // Clear any existing schedule
        schedule.clear();
        
        // Create first 19 rounds (each team plays against all others once)
        List<Team> teamsCopy = new ArrayList<>(teams);
        
        // Using circular rotation algorithm for round-robin schedule
        // One team stays fixed (first team), others rotate
        Team fixedTeam = teamsCopy.remove(0);
        
        for (int round = 0; round < teamsCopy.size() * 2; round++) {
            List<Match> roundMatches = new ArrayList<>();
            
            // Add matches for this round
            if (round < teamsCopy.size()) {
                // First half of tournament
                // Match fixed team with rotating team
                Team opponent = teamsCopy.get(round % teamsCopy.size());
                roundMatches.add(new Match(fixedTeam, opponent));
                
                // Match other teams
                for (int i = 0; i < (teamsCopy.size() - 1) / 2; i++) {
                    Team home = teamsCopy.get((round + i + 1) % teamsCopy.size());
                    Team away = teamsCopy.get((round + teamsCopy.size() - 1 - i) % teamsCopy.size());
                    roundMatches.add(new Match(home, away));
                }
                
                schedule.add(roundMatches);
            } else {
                // Second half (return matches - swap home/away)
                int firstRoundIdx = round - teamsCopy.size();
                List<Match> firstRound = schedule.get(firstRoundIdx);
                
                for (Match match : firstRound) {
                    // Swap home and away teams
                    roundMatches.add(new Match(match.getAwayTeam(), match.getHomeTeam()));
                }
                
                schedule.add(roundMatches);
            }
        }
        
        return true;
    }
    
    /**
     * Starts the tournament
     * @return True if started successfully
     */
    public boolean startTournament() {
        if (tournamentStarted) {
            return false;
        }
        
        if (teams.size() != REQUIRED_TEAMS) {
            throw new IllegalStateException(
                "Exactly " + REQUIRED_TEAMS + " teams required to start tournament, but got " + teams.size());
        }
        
        if (schedule.isEmpty()) {
            generateSchedule();
        }
        
        tournamentStarted = true;
        currentRound = 0;
        
        return true;
    }
    
    /**
     * Simulates the next round of matches
     * @return List of simulated matches or null if tournament finished
     */
    public List<Match> simulateNextRound() {
        if (!tournamentStarted) {
            throw new IllegalStateException("Tournament has not been started");
        }
        
        if (currentRound >= TOTAL_ROUNDS) {
            tournamentFinished = true;
            return null;
        }
        
        List<Match> roundMatches = schedule.get(currentRound);
        List<Match> simulatedMatches = new ArrayList<>();
        
        for (Match match : roundMatches) {
            MatchSimulator.simulateMatch(match);
            
            // Update league standings
            leagueStandings.recordMatchResult(match);
            
            // Update top scorers
            for (String scorer : match.getGoalScorers()) {
                // Extract player name from scorer string (format is "Name (Team)")
                String playerName = scorer.substring(0, scorer.indexOf(" ("));
                topScorers.recordGoal(playerName, extractTeamFromScorer(scorer));
            }
            
            simulatedMatches.add(match);
        }
        
        currentRound++;
        leagueStandings.setCurrentRound(currentRound);
        
        if (currentRound >= TOTAL_ROUNDS) {
            tournamentFinished = true;
        }
        
        return simulatedMatches;
    }
    
    /**
     * Extracts team name from a goal scorer string with format "Player Name (Team Name)"
     */
    private String extractTeamFromScorer(String scorer) {
        int start = scorer.indexOf("(") + 1;
        int end = scorer.indexOf(")");
        return scorer.substring(start, end);
    }
    
    /**
     * Simulates all remaining rounds of the tournament at once
     * @return True if simulation completed successfully
     */
    public boolean simulateAllRemainingRounds() {
        if (!tournamentStarted) {
            throw new IllegalStateException("Tournament has not been started");
        }
        
        while (!tournamentFinished) {
            simulateNextRound();
        }
        
        return true;
    }
    
    /**
     * Gets a specific round's matches
     * @param roundNumber Round number (1-based)
     * @return List of matches for that round
     */
    public List<Match> getRoundMatches(int roundNumber) {
        if (roundNumber < 1 || roundNumber > TOTAL_ROUNDS) {
            throw new IllegalArgumentException("Round number must be between 1 and " + TOTAL_ROUNDS);
        }
        
        return schedule.get(roundNumber - 1);
    }
    
    /**
     * Gets the current league standings
     * @return League standings
     */
    public LeagueStandings getLeagueStandings() {
        return leagueStandings;
    }
    
    /**
     * Gets the top scorers table
     * @return Top scorers
     */
    public TopScorersTable getTopScorers() {
        return topScorers;
    }
    
    /**
     * Gets the current round number (1-based)
     * @return Current round number
     */
    public int getCurrentRound() {
        return currentRound + 1;
    }
    
    /**
     * Checks if tournament has finished
     * @return True if tournament is finished
     */
    public boolean isTournamentFinished() {
        return tournamentFinished;
    }
    
    /**
     * Gets all matches that have been simulated so far
     * @return List of all simulated matches
     */
    public List<Match> getAllSimulatedMatches() {
        List<Match> allMatches = new ArrayList<>();
        
        for (int i = 0; i < currentRound; i++) {
            allMatches.addAll(schedule.get(i));
        }
        
        return allMatches;
    }
    
    /**
     * Gets the tournament name
     * @return Tournament name
     */
    public String getTournamentName() {
        return tournamentName;
    }
}
