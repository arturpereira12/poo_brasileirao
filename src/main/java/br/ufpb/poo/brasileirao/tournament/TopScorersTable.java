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

    /**
     * Cria uma nova tabela de artilheiros vazia.
     */
    public TopScorersTable() {
        this.playerStatsMap = new HashMap<>();
    }

    /**
     * Adiciona um gol para um jogador. Se o jogador ainda não estiver na tabela,
     * ele será adicionado automaticamente.
     * 
     * @param playerName o nome do jogador
     * @param teamName o nome do time do jogador
     */
    public void addGoal(String playerName, String teamName) {
        PlayerStats stats = playerStatsMap.getOrDefault(playerName, new PlayerStats(playerName, teamName));
        stats.addGoal();
        playerStatsMap.put(playerName, stats);
    }

    /**
     * Obtém a lista de artilheiros ordenada pelo número de gols.
     * 
     * @return a lista ordenada de estatísticas dos jogadores
     */
    public List<PlayerStats> getTopScorers() {
        List<PlayerStats> topScorers = new ArrayList<>(playerStatsMap.values());
        
        Collections.sort(topScorers, Comparator
                .comparingInt(PlayerStats::getGoals).reversed()
                .thenComparing(PlayerStats::getPlayerName));
        
        return topScorers;
    }

    /**
     * Obtém a lista de artilheiros ordenada pelo número de gols, limitada a um número máximo de jogadores.
     * 
     * @param limit o número máximo de jogadores a retornar
     * @return a lista ordenada e limitada de estatísticas dos jogadores
     */
    public List<PlayerStats> getTopScorers(int limit) {
        List<PlayerStats> topScorers = getTopScorers();
        
        if (topScorers.size() <= limit) {
            return topScorers;
        }
        
        return topScorers.subList(0, limit);
    }

    /**
     * Obtém o número total de jogadores na tabela.
     * 
     * @return o número de jogadores
     */
    public int getNumberOfPlayers() {
        return playerStatsMap.size();
    }

    /**
     * Classe interna que representa as estatísticas de um jogador na tabela de artilheiros.
     */
    public static class PlayerStats {
        private String playerName;
        private String teamName;
        private int goals;

        /**
         * Cria um objeto de estatísticas para um jogador.
         * 
         * @param playerName o nome do jogador
         * @param teamName o nome do time
         */
        public PlayerStats(String playerName, String teamName) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.goals = 0;
        }

        /**
         * Adiciona um gol às estatísticas do jogador.
         */
        public void addGoal() {
            this.goals++;
        }

        /**
         * Obtém o nome do jogador.
         * 
         * @return o nome do jogador
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Obtém o nome do time do jogador.
         * 
         * @return o nome do time
         */
        public String getTeamName() {
            return teamName;
        }

        /**
         * Obtém o número de gols do jogador.
         * 
         * @return o número de gols
         */
        public int getGoals() {
            return goals;
        }

        /**
         * Retorna uma representação em string das estatísticas do jogador.
         * 
         * @return a string que representa as estatísticas
         */
        @Override
        public String toString() {
            return String.format("%s (%s) - %d gol%s", 
                    playerName, teamName, goals, (goals == 1 ? "" : "s"));
        }
    }
} 