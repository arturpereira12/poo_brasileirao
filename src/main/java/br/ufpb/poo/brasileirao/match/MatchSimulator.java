package br.ufpb.poo.brasileirao.match;

import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.model.Player;
import br.ufpb.poo.brasileirao.model.Position;
import br.ufpb.poo.brasileirao.model.Forward;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * Class responsible for simulating a football match between two teams
 */
public class MatchSimulator {
    private Random random;
    
    // Factors that influence the simulation
    private static final int HOME_ADVANTAGE_BASE = 10;
    private static final int MAX_POSSIBLE_GOALS = 7; // Now using this value instead of hardcoded 5
    private static final double STAR_PLAYER_CHANCE = 0.3; // 30% chance of a player standing out
    private static final double WEATHER_EFFECT_CHANCE = 0.2; // 20% chance of weather affecting match
    private static final double RED_CARD_CHANCE = 0.2; // 20% chance of a red card
    private static final double LAST_MINUTE_GOAL_CHANCE = 0.1; // 10% chance of last minute goal
    private static final double LAST_MINUTE_AWAY_GOAL_CHANCE = 0.05; // 5% chance of away last minute goal
    
    /**
     * Default constructor
     */
    public MatchSimulator() {
        this.random = new Random();
    }
    
    /**
     * Simulates a match between two teams and returns the result
     * @param homeTeam Team playing at home
     * @param awayTeam Visiting team
     * @return Match object with the game result
     */
    /*public Match simulate(Team homeTeam, Team awayTeam) {
        Match match = new Match(homeTeam, awayTeam);
        
        // Calculate goals based on team strength
        //int homeTeamStrength = homeTeam.calcularForcaTime();
        //int awayTeamStrength = awayTeam.calcularForcaTime();
        
        // Random home advantage (between 5 and 15)
        int homeAdvantage = HOME_ADVANTAGE_BASE + random.nextInt(11) - 5;
        
        // Simulation of random events
        applyRandomEvents(homeTeam, awayTeam, homeTeamStrength, awayTeamStrength);
        
        // Number of goals is based on team strength + random factor
        int homeTeamGoals = calculateGoals(homeTeamStrength + homeAdvantage);
        int awayTeamGoals = calculateGoals(awayTeamStrength);
        
        // Debug log
        System.out.println("DEBUG - Home Team Strength: " + homeTeamStrength + 
                         ", Away Team Strength: " + awayTeamStrength + 
                         ", Home Advantage: " + homeAdvantage);
        System.out.println("DEBUG - Calculated goals: Home " + homeTeamGoals + 
                         ", Away: " + awayTeamGoals);
        
        // Distribute goals at different moments of the match
        distributeGoalsInMatch(match, homeTeam, awayTeam, homeTeamGoals, awayTeamGoals);
        
        // Apply last minute goal chance (dramatic finish)
        applyLastMinuteGoals(match, homeTeam, awayTeam);
        
        return match;
    }
    
    /**
     * Apply random events that can affect the match
     */
    private void applyRandomEvents(Team homeTeam, Team awayTeam, int homeTeamStrength, int awayTeamStrength) {
        // Simulation of outstanding player performance
        if (random.nextDouble() < STAR_PLAYER_CHANCE) {
            // A home team player might have exceptional performance
            System.out.println("A player from " + homeTeam.getName() + 
                             " is having an outstanding day!");
            
            // Give a small boost to the home team (reflected in goal calculation)
            homeTeamStrength += 5;
        }
        
        // Weather effect simulation
        if (random.nextDouble() < WEATHER_EFFECT_CHANCE) {
            String[] weatherConditions = {"heavy rain", "extreme heat", "strong wind"};
            String currentWeather = weatherConditions[random.nextInt(weatherConditions.length)];
            System.out.println("The match is being affected by " + currentWeather);
            
            // Weather conditions may favor technically better teams or more physical ones
            if (currentWeather.equals("heavy rain") && homeTeamStrength > awayTeamStrength) {
                System.out.println("The " + currentWeather + " seems to favor the home team!");
            }
        }
        
        // Red card simulation
        if (random.nextDouble() < RED_CARD_CHANCE) {
            // Decide which team receives a red card (more likely for the away team)
            boolean cardForHomeTeam = random.nextDouble() > 0.6;
            
            if (cardForHomeTeam) {
                System.out.println("A player from " + homeTeam.getName() + 
                                 " received a red card!");
                // Reduce home team strength
                homeTeamStrength -= 10;
            } else {
                System.out.println("A player from " + awayTeam.getName() + 
                                 " received a red card!");
                // Reduce away team strength
                awayTeamStrength -= 10;
            }
        }
    }
    
    /**
     * Apply potential last-minute goals for dramatic match endings
     */
    private void applyLastMinuteGoals(Match match, Team homeTeam, Team awayTeam) {
        // Check for a dramatic last-minute home team goal
        if (random.nextDouble() < LAST_MINUTE_GOAL_CHANCE) {
            Player scorer = selectRandomPlayer(homeTeam);
            System.out.println("DRAMATIC FINISH! " + scorer.getName() + " scores in the last minute!");
            match.addHomeTeamGoal(scorer.getName() + " at 90+2 minutes (Last minute goal)");
        }
        
        // Check for a dramatic last-minute away team goal (less likely)
        if (random.nextDouble() < LAST_MINUTE_AWAY_GOAL_CHANCE) {
            Player scorer = selectRandomPlayer(awayTeam);
            System.out.println("INCREDIBLE! " + scorer.getName() + " scores an away goal in the last minute!");
            match.addAwayTeamGoal(scorer.getName() + " at 90+3 minutes (Last minute goal)");
        }
    }
    
    /**
     * Distribute goals throughout the match in a realistic way
     */
    private void distributeGoalsInMatch(Match match, Team homeTeam, Team awayTeam, 
                                      int homeTeamGoals, int awayTeamGoals) {
        // List to track when goals are scored
        List<Integer> homeGoalMinutes = new ArrayList<>();
        List<Integer> awayGoalMinutes = new ArrayList<>();
        
        // Generate random moments for goals (1-90 minutes)
        for (int i = 0; i < homeTeamGoals; i++) {
            // Minutes 1-90 with higher probability for the second half
            int minute = random.nextInt(100) < 60 ? 
                       random.nextInt(45) + 45 : // 60% chance second half
                       random.nextInt(45) + 1;  // 40% chance first half
            homeGoalMinutes.add(minute);
        }
        
        for (int i = 0; i < awayTeamGoals; i++) {
            int minute = random.nextInt(100) < 60 ? 
                       random.nextInt(45) + 45 : 
                       random.nextInt(45) + 1;
            awayGoalMinutes.add(minute);
        }
        
        // Sort the minutes to add them chronologically
        homeGoalMinutes.sort(null);
        awayGoalMinutes.sort(null);
        
        // Add the goals to the match in chronological order
        int homeIndex = 0, awayIndex = 0;
        
        while (homeIndex < homeGoalMinutes.size() || awayIndex < awayGoalMinutes.size()) {
            // Decide which team scores the next goal
            boolean homeScoresNext = false;
            
            if (homeIndex < homeGoalMinutes.size() && awayIndex < awayGoalMinutes.size()) {
                homeScoresNext = homeGoalMinutes.get(homeIndex) < awayGoalMinutes.get(awayIndex);
            } else {
                homeScoresNext = homeIndex < homeGoalMinutes.size();
            }
            
            if (homeScoresNext) {
                Player scorer = selectRandomPlayer(homeTeam);
                String goalMoment = " at " + homeGoalMinutes.get(homeIndex) + " minutes";
                match.addHomeTeamGoal(scorer.getName() + goalMoment);
                homeIndex++;
            } else {
                Player scorer = selectRandomPlayer(awayTeam);
                String goalMoment = " at " + awayGoalMinutes.get(awayIndex) + " minutes";
                match.addAwayTeamGoal(scorer.getName() + goalMoment);
                awayIndex++;
            }
        }
    }
    
    /**
     * Calculate how many goals a team will score based on their strength
     * @param strength Team strength
     * @return Number of goals
     */
    private int calculateGoals(int strength) {
        // Simple formula: the higher the strength, the higher the probability of scoring
        double baseProbability = Math.min(0.9, strength / 100.0);
        
        // Add an element of luck - some games have more goals than normal
        boolean highScoringGame = random.nextDouble() < 0.15; // 15% chance of high scoring game
        
        // Now using the MAX_POSSIBLE_GOALS constant instead of hardcoded value
        int maxGoals = highScoringGame ? MAX_POSSIBLE_GOALS : 5;
        
        int goals = 0;
        
        // For each possible goal, check if the team is able to score
        for (int i = 0; i < maxGoals; i++) {
            // Probability decreases with each additional goal
            double probability = baseProbability / (i + 1);
            if (random.nextDouble() < probability) {
                goals++;
            }
        }
        
        return goals;
    }
    
    /**
     * Select a random player from a team
     * @param team Team from which to select the player
     * @return Selected player
     */
    private Player selectRandomPlayer(Team team) {
        // Make sure we consistently use getPlayers() rather than mixing getPlayer()
        List<Player> players = team.getPlayers();
        
        if (players == null || players.isEmpty()) {
            // If there are no players, we need to handle this specially
            // Instead of creating a generic Player (which is abstract),
            // we'll return a Forward with minimum strength
            try {
                return null; //FIXME new Forward("Unknown Player", 0, Position.FORWARD);
            } catch (Exception e) {
                // If creation fails, handle it gracefully
                System.out.println("Error creating default player: " + e.getMessage());
                // Try a simpler approach if the constructor throws an exception
                return null;
            }
        }
        
        // Choose a random player from the team
        int randomIndex = random.nextInt(players.size());
        return players.get(randomIndex);
    }
}
