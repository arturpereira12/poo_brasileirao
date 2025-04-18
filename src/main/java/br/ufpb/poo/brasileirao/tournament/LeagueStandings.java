package br.ufpb.poo.brasileirao.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que representa a tabela de classificação de um campeonato.
 */
public class LeagueStandings {
    private Map<String, TeamStats> teamStatsMap;

    /**
     * Cria uma nova tabela de classificação vazia.
     */
    public LeagueStandings() {
        this.teamStatsMap = new HashMap<>();
    }

    /**
     * Adiciona um time à tabela de classificação.
     * 
     * @param teamName o nome do time
     */
    public void addTeam(String teamName) {
        if (!teamStatsMap.containsKey(teamName)) {
            teamStatsMap.put(teamName, new TeamStats(teamName));
        }
    }

    /**
     * Adiciona uma vitória para um time.
     * 
     * @param teamName o nome do time
     * @param goalsFor gols marcados
     * @param goalsAgainst gols sofridos
     */
    public void addWin(String teamName, int goalsFor, int goalsAgainst) {
        TeamStats stats = teamStatsMap.get(teamName);
        if (stats != null) {
            stats.addWin(goalsFor, goalsAgainst);
        }
    }

    /**
     * Adiciona um empate para um time.
     * 
     * @param teamName o nome do time
     * @param goalsFor gols marcados
     * @param goalsAgainst gols sofridos
     */
    public void addDraw(String teamName, int goalsFor, int goalsAgainst) {
        TeamStats stats = teamStatsMap.get(teamName);
        if (stats != null) {
            stats.addDraw(goalsFor, goalsAgainst);
        }
    }

    /**
     * Adiciona uma derrota para um time.
     * 
     * @param teamName o nome do time
     * @param goalsFor gols marcados
     * @param goalsAgainst gols sofridos
     */
    public void addLoss(String teamName, int goalsFor, int goalsAgainst) {
        TeamStats stats = teamStatsMap.get(teamName);
        if (stats != null) {
            stats.addLoss(goalsFor, goalsAgainst);
        }
    }

    /**
     * Obtém a tabela de classificação ordenada por pontos, saldo de gols e gols marcados.
     * 
     * @return a lista ordenada de estatísticas dos times
     */
    public List<TeamStats> getStandings() {
        List<TeamStats> standings = new ArrayList<>(teamStatsMap.values());
        
        Collections.sort(standings, Comparator
                .comparingInt(TeamStats::getPoints).reversed()
                .thenComparingInt(TeamStats::getGoalDifference).reversed()
                .thenComparingInt(TeamStats::getGoalsFor).reversed()
                .thenComparing(TeamStats::getTeamName));
        
        return standings;
    }

    /**
     * Obtém o número de times na tabela.
     * 
     * @return o número de times
     */
    public int getNumberOfTeams() {
        return teamStatsMap.size();
    }

    /**
     * Classe interna que representa as estatísticas de um time na tabela.
     */
    public static class TeamStats {
        private String teamName;
        private int played;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;
        private int points;

        /**
         * Cria um objeto de estatísticas para um time.
         * 
         * @param teamName o nome do time
         */
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

        /**
         * Adiciona uma vitória às estatísticas.
         * 
         * @param goalsFor gols marcados
         * @param goalsAgainst gols sofridos
         */
        public void addWin(int goalsFor, int goalsAgainst) {
            this.played++;
            this.wins++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
            this.points += 3;
        }

        /**
         * Adiciona um empate às estatísticas.
         * 
         * @param goalsFor gols marcados
         * @param goalsAgainst gols sofridos
         */
        public void addDraw(int goalsFor, int goalsAgainst) {
            this.played++;
            this.draws++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
            this.points += 1;
        }

        /**
         * Adiciona uma derrota às estatísticas.
         * 
         * @param goalsFor gols marcados
         * @param goalsAgainst gols sofridos
         */
        public void addLoss(int goalsFor, int goalsAgainst) {
            this.played++;
            this.losses++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
        }

        /**
         * Obtém o nome do time.
         * 
         * @return o nome do time
         */
        public String getTeamName() {
            return teamName;
        }

        /**
         * Obtém o número de jogos disputados.
         * 
         * @return o número de jogos
         */
        public int getPlayed() {
            return played;
        }

        /**
         * Obtém o número de vitórias.
         * 
         * @return o número de vitórias
         */
        public int getWins() {
            return wins;
        }

        /**
         * Obtém o número de empates.
         * 
         * @return o número de empates
         */
        public int getDraws() {
            return draws;
        }

        /**
         * Obtém o número de derrotas.
         * 
         * @return o número de derrotas
         */
        public int getLosses() {
            return losses;
        }

        /**
         * Obtém o número de gols marcados.
         * 
         * @return o número de gols marcados
         */
        public int getGoalsFor() {
            return goalsFor;
        }

        /**
         * Obtém o número de gols sofridos.
         * 
         * @return o número de gols sofridos
         */
        public int getGoalsAgainst() {
            return goalsAgainst;
        }

        /**
         * Calcula e retorna o saldo de gols.
         * 
         * @return o saldo de gols
         */
        public int getGoalDifference() {
            return goalsFor - goalsAgainst;
        }

        /**
         * Obtém o número de pontos.
         * 
         * @return o número de pontos
         */
        public int getPoints() {
            return points;
        }

        /**
         * Retorna uma representação em string das estatísticas do time.
         * 
         * @return a string que representa as estatísticas
         */
        @Override
        public String toString() {
            return String.format("%s - %dP | %dJ | %dV | %dE | %dD | %d:%d | SG: %d",
                    teamName, points, played, wins, draws, losses, goalsFor, goalsAgainst, getGoalDifference());
        }
    }
} 