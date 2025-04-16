package br.ufpb.poo.brasileirao.tournament;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracks the top goal scorers in the tournament
 */
public class TopScorersTable {

    private Map<String, ScorerStats> scorersMap;
    
    /**
     * Creates a new top scorers table
     */
    public TopScorersTable() {
        this.scorersMap = new HashMap<>();
    }
    
    /**
     * Records a goal for a player
     * @param playerName Name of the player who scored
     * @param teamName Name of the player's team
     */
    public void recordGoal(String playerName, String teamName) {
        if (playerName == null || teamName == null) {
            throw new IllegalArgumentException("Player name and team name cannot be null");
        }
        
        String key = playerName + " (" + teamName + ")";
        ScorerStats stats = scorersMap.getOrDefault(key, new ScorerStats(playerName, teamName));
        stats.addGoal();
        scorersMap.put(key, stats);
    }
    
    /**
     * Gets the top goal scorers sorted by goals (descending)
     * @param limit Maximum number of scorers to return (0 for all)
     * @return List of top scorers
     */
    public List<ScorerStats> getTopScorers(int limit) {
        List<ScorerStats> sortedScorers = scorersMap.values().stream()
            .sorted(Comparator.comparingInt(ScorerStats::getGoals).reversed())
            .collect(Collectors.toList());
        
        if (limit > 0 && limit < sortedScorers.size()) {
            return sortedScorers.subList(0, limit);
        }
        
        return sortedScorers;
    }
    
    /**
     * Gets all scorers
     * @return List of all scorer stats
     */
    public List<ScorerStats> getAllScorers() {
        return getTopScorers(0);
    }
    
    /**
     * Gets total number of goals scored in the tournament
     * @return Total goals
     */
    public int getTotalGoals() {
        return scorersMap.values().stream()
            .mapToInt(ScorerStats::getGoals)
            .sum();
    }
    
    /**
     * Gets total number of players who scored at least one goal
     * @return Number of scorers
     */
    public int getTotalScorers() {
        return scorersMap.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Top Scorers:\n");
        sb.append(String.format("%-25s %-20s %s\n", "Player", "Team", "Goals"));
        sb.append("-".repeat(50)).append("\n");
        
        int rank = 1;
        for (ScorerStats scorer : getTopScorers(10)) {
            sb.append(String.format("%2d. %-22s %-20s %d\n", 
                    rank++, scorer.getPlayerName(), scorer.getTeamName(), scorer.getGoals()));
        }
        
        return sb.toString();
    }
    
    /**
     * Class to track a player's scoring statistics
     */
    public static class ScorerStats {
        private String playerName;
        private String teamName;
        private int goals;
        
        /**
         * Creates new scorer stats
         * @param playerName Player name
         * @param teamName Team name
         */
        public ScorerStats(String playerName, String teamName) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.goals = 0;
        }
        
        /**
         * Adds a goal to this player's tally
         */
        public void addGoal() {
            goals++;
        }
        
        /**
         * Gets player name
         * @return Player name
         */
        public String getPlayerName() {
            return playerName;
        }
        
        /**
         * Gets team name
         * @return Team name
         */
        public String getTeamName() {
            return teamName;
        }
        
        /**
         * Gets total goals scored
         * @return Goals scored
         */
        public int getGoals() {
            return goals;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%s): %d goals", playerName, teamName, goals);
        }
    }
}
