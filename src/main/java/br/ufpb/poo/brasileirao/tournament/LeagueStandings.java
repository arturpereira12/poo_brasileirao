package br.ufpb.poo.brasileirao.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


 //Classe que representa a tabela de classificação de um campeonato.
  
public class LeagueStandings {
    private Map<String, TeamStats> teamStatsMap;

    //Nova tabela de classificação.
    public LeagueStandings() {
        this.teamStatsMap = new HashMap<>();
    }

    //ADD time à tabela de classificação.

    public void addTeam(String teamName) {
        if (!teamStatsMap.containsKey(teamName)) {
            teamStatsMap.put(teamName, new TeamStats(teamName));
        }
    }

    
    public void addWin(String teamName, int goalsFor, int goalsAgainst) {
        TeamStats stats = teamStatsMap.get(teamName);
        if (stats != null) {
            stats.addWin(goalsFor, goalsAgainst);
        }
    }

    
    public void addDraw(String teamName, int goalsFor, int goalsAgainst) {
        TeamStats stats = teamStatsMap.get(teamName);
        if (stats != null) {
            stats.addDraw(goalsFor, goalsAgainst);
        }
    }
    
    public void addLoss(String teamName, int goalsFor, int goalsAgainst) {
        TeamStats stats = teamStatsMap.get(teamName);
        if (stats != null) {
            stats.addLoss(goalsFor, goalsAgainst);
        }
    }

    public List<TeamStats> getStandings() {
        List<TeamStats> standings = new ArrayList<>(teamStatsMap.values());
        
        Collections.sort(standings, Comparator
                .comparingInt(TeamStats::getPoints).reversed()
                .thenComparingInt(TeamStats::getGoalDifference).reversed()
                .thenComparingInt(TeamStats::getGoalsFor).reversed()
                .thenComparing(TeamStats::getTeamName));
        
        return standings;
    }

    public int getNumberOfTeams() {
        return teamStatsMap.size();
    }

    public static class TeamStats {
        private String teamName;
        private int played;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;
        private int points;

        public TeamStats(String teamName) {
            this.teamName = teamName;
            this.played = 0;
            this.wins = 0;
            this.draws = 0;
            this.losses = 0;
            this.goalsFor = 0;
            this.goalsAgainst = 0;
            this.points = 0;
        }

        // ADD VITÓRIAS, EMPATES E DERROTAS ÀS ESTATÍSTICAS DO TIME.

        public void addWin(int goalsFor, int goalsAgainst) {
            this.played++;
            this.wins++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
            this.points += 3;
        }

        public void addDraw(int goalsFor, int goalsAgainst) {
            this.played++;
            this.draws++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
            this.points += 1;
        }

        public void addLoss(int goalsFor, int goalsAgainst) {
            this.played++;
            this.losses++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
        }

        public String getTeamName() {
            return teamName;
        }

        public int getPlayed() {
            return played;
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


        public int getPoints() {
            return points;
        }

        @Override
        public String toString() {
            return String.format("%s - %dP | %dJ | %dV | %dE | %dD | %d:%d | SG: %d",
                    teamName, points, played, wins, draws, losses, goalsFor, goalsAgainst, getGoalDifference());
        }
    }
} 