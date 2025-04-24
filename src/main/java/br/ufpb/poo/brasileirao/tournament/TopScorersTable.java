package br.ufpb.poo.brasileirao.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que representa a tabela de artilheiros de um campeonato.
 */
public class TopScorersTable {
    private Map<String, PlayerStats> playerStatsMap;

    public TopScorersTable() {
        this.playerStatsMap = new HashMap<>();
    }

    public void addGoal(String playerName, String teamName) {
        PlayerStats stats = playerStatsMap.getOrDefault(playerName, new PlayerStats(playerName, teamName));
        stats.addGoal();
        playerStatsMap.put(playerName, stats);
    }

    public List<PlayerStats> getTopScorers() {
        List<PlayerStats> topScorers = new ArrayList<>(playerStatsMap.values());
        
        Collections.sort(topScorers, Comparator
                .comparingInt(PlayerStats::getGoals).reversed()
                .thenComparing(PlayerStats::getPlayerName));
        
        return topScorers;
    }

    public List<PlayerStats> getTopScorers(int limit) {
        List<PlayerStats> topScorers = getTopScorers();
        
        if (topScorers.size() <= limit) {
            return topScorers;
        }
        
        return topScorers.subList(0, limit);
    }

    public int getNumberOfPlayers() {
        return playerStatsMap.size();
    }

    public static class PlayerStats {
        private String playerName;
        private String teamName;
        private int goals;

        public PlayerStats(String playerName, String teamName) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.goals = 0;
        }

        public void addGoal() {
            this.goals++;
        }
        //Getters e Setters

        public String getPlayerName() {
            return playerName;
        }

        public String getTeamName() {
            return teamName;
        }

        public int getGoals() {
            return goals;
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - %d gol%s", 
                    playerName, teamName, goals, (goals == 1 ? "" : "s"));
        }
    }
} 