package br.ufpb.poo.brasileirao.service;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.GoalProbabilityCalculatorFactory;
import br.ufpb.poo.brasileirao.model.Player;
import br.ufpb.poo.brasileirao.model.Position;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
public class TournamentManager {
    @Getter @Setter
    private String tournamentName;
    
    @Getter
    private List<Team> teams;
    
    @Getter
    private List<Match> scheduledMatches;
    
    @Getter
    private List<Match> simulatedMatches;
    
    @Getter
    private LeagueStandings leagueStandings;
    
    @Getter
    private TopScorersTable topScorers;
    
    @Getter
    private int currentRound;
    
    @Getter
    private int totalRounds;
    
    private Random random;
    
    @Getter
    private boolean isActive;

    public TournamentManager() {
        this("Campeonato Brasileiro");
    }

    public TournamentManager(String tournamentName) {
        this.tournamentName = tournamentName;
        this.teams = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
        this.simulatedMatches = new ArrayList<>();
        this.leagueStandings = new LeagueStandings();
        this.topScorers = new TopScorersTable();
        this.currentRound = 0;
        this.totalRounds = 0;
        this.random = new Random();
        this.isActive = false;
    }

    public void addTeams(List<Team> teams) {
        this.teams.clear();
        this.teams.addAll(teams);
        
        // Limpa e reinicializa a tabela de classificação
        this.leagueStandings = new LeagueStandings();
        for (Team team : teams) {
            this.leagueStandings.addTeam(team.getName());
        }
    }

    public void startTournament() {
        reset();
        generateSchedule();
        this.totalRounds = calculateTotalRounds();
        this.isActive = true;
    }

    public void reset() {
        this.scheduledMatches.clear();
        this.simulatedMatches.clear();
        this.currentRound = 0;
        this.isActive = false;
        this.topScorers = new TopScorersTable();
        
        // Reinicializa a tabela de classificação
        this.leagueStandings = new LeagueStandings();
        for (Team team : teams) {
            this.leagueStandings.addTeam(team.getName());
        }
    }

    private void generateSchedule() {
        int numberOfTeams = teams.size();
        
        // Se o número de times for ímpar, adiciona um time de folga
        boolean needDummy = numberOfTeams % 2 != 0;
        int totalTeams = needDummy ? numberOfTeams + 1 : numberOfTeams;
        
        int totalRounds = totalTeams - 1;
        int matchesPerRound = totalTeams / 2;
        
        List<Integer> teamIndices = new ArrayList<>();
        for (int i = 0; i < totalTeams; i++) {
            teamIndices.add(i);
        }
        
        LocalDate startDate = LocalDate.now();
        
        for (int round = 0; round < totalRounds; round++) {
            LocalDate roundDate = startDate.plusDays(round * 7); // Uma rodada por semana
            
            for (int match = 0; match < matchesPerRound; match++) {
                int homeIndex = teamIndices.get(match);
                int awayIndex = teamIndices.get(totalTeams - 1 - match);
                
                // Evitar partidas contra o time fictício (caso ímpar)
                if (homeIndex < teams.size() && awayIndex < teams.size()) {
                    Team homeTeam = teams.get(homeIndex);
                    Team awayTeam = teams.get(awayIndex);
                    
                    // Criar partida de ida
                    Match matchA = new Match(homeTeam, awayTeam, roundDate, round + 1);
                    scheduledMatches.add(matchA);
                    
                    // Criar partida de volta (turno de volta)
                    LocalDate returnDate = roundDate.plusDays(totalRounds * 7); // O returno começa após todas as rodadas de ida
                    Match matchB = new Match(awayTeam, homeTeam, returnDate, round + totalRounds + 1);
                    scheduledMatches.add(matchB);
                }
            }
            
            // Rotacionar times para a próxima rodada (algoritmo de berger)
            teamIndices.add(1, teamIndices.remove(teamIndices.size() - 1));
        }
    }

    /**
     * Calcula o número total de rodadas.
     * 
     * @return o número total de rodadas
     */
    private int calculateTotalRounds() {
        if (teams.size() <= 1) return 0;
        return (teams.size() - 1) * 2; // Turno e returno
    }

    /**
     * Simula a próxima rodada do torneio.
     * 
     * @return true se a rodada foi simulada com sucesso, false caso o torneio já tenha terminado
     */
    public boolean simulateNextRound() {
        if (currentRound >= totalRounds) {
            return false; // Torneio já concluído
        }
        
        currentRound++;
        List<Match> roundMatches = getMatchesForRound(currentRound);
        
        for (Match match : roundMatches) {
            simulateMatch(match);
            simulatedMatches.add(match);
        }
        
        return true;
    }
    public void simulateAllRemainingRounds() {
        while (simulateNextRound()) {
            // Continua simulando até o fim do torneio
        }
    }

    /**
     * Obtém as partidas agendadas para uma determinada rodada.
     * 
     * @param round a rodada desejada
     * @return lista de partidas da rodada
     */
    public List<Match> getMatchesForRound(int round) {
        List<Match> roundMatches = new ArrayList<>();
        for (Match match : scheduledMatches) {
            if (match.getRound() == round) {
                roundMatches.add(match);
            }
        }
        return roundMatches;
    }

    /**
     * Simula uma partida de futebol, gerando o resultado.
     * 
     * @param match a partida a ser simulada
     */
    private void simulateMatch(Match match) {
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        
        // Calcular força relativa dos times
        int homeStrength = homeTeam.getStrength() + 10; // Vantagem do time da casa
        int awayStrength = awayTeam.getStrength();
        
        // Fator de aleatoriedade
        double homeLuckFactor = 0.8 + (random.nextDouble() * 0.4);
        double awayLuckFactor = 0.8 + (random.nextDouble() * 0.4);
        
        // Ajustar forças finais
        double finalHomeStrength = homeStrength * homeLuckFactor;
        double finalAwayStrength = awayStrength * awayLuckFactor;
        
        // Calcular probabilidades de gol
        double homeGoalChance = finalHomeStrength / 400.0;
        double awayGoalChance = finalAwayStrength / 400.0;
        
        int homeGoals = 0;
        int awayGoals = 0;
        
        // Simular 6 chances de gol por time
        for (int i = 0; i < 6; i++) {
            if (random.nextDouble() < homeGoalChance) {
                homeGoals++;
            }
            if (random.nextDouble() < awayGoalChance) {
                awayGoals++;
            }
        }
        
        // Limitando o número máximo de gols para tornar mais realista
        homeGoals = Math.min(homeGoals, 5);
        awayGoals = Math.min(awayGoals, 4);
        
        // Definir resultado da partida
        match.setResult(homeGoals, awayGoals);
        
        // Simular os artilheiros dos gols
        simulateGoalScorers(match, homeTeam, homeGoals, true);
        simulateGoalScorers(match, awayTeam, awayGoals, false);
        
        // Atualizar a tabela de classificação
        updateStandings(match);
    }

    private void simulateGoalScorers(Match match, Team team, int goals, boolean isHome) {
        
        // Mapear jogadores por posição
        Map<Position, List<Player>> playersByPosition = new HashMap<>();
        for (Position position : Position.values()) {
            playersByPosition.put(position, new ArrayList<>());
        }
        
        // Classificar jogadores por posição
        for (Player player : team.getPlayers()) {
            if (player.getPosition() != null) {
                playersByPosition.get(player.getPosition()).add(player);
            }
        }
        
        for (int i = 0; i < goals; i++) {
            // Usar nossa factory para determinar qual posição deve marcar o gol
            double randomValue = random.nextDouble();
            Position scorerPosition = GoalProbabilityCalculatorFactory.getPositionForGoal(randomValue);
            String scorer = null;
            
            // Tentar encontrar um jogador na posição determinada usando ponderação por força
            List<Player> eligiblePlayers = playersByPosition.get(scorerPosition);
            if (!eligiblePlayers.isEmpty()) {
                // Escolher jogador com base na força (weighted random selection)
                scorer = selectPlayerWeightedByStrength(eligiblePlayers);
            } else {
                // Fallback: se não houver jogadores na posição selecionada
                // Tente cada posição na ordem de probabilidade (FORWARD, MIDFIELDER, DEFENDER)
                Position[] fallbackOrder = {Position.FORWARD, Position.MIDFIELDER, Position.DEFENDER};
                
                for (Position fallbackPosition : fallbackOrder) {
                    List<Player> fallbackPlayers = playersByPosition.get(fallbackPosition);
                    if (!fallbackPlayers.isEmpty()) {
                        // Escolher jogador com base na força (weighted random selection)
                        scorer = selectPlayerWeightedByStrength(fallbackPlayers);
                        break;
                    }
                }
                
                // Caso extremo: nenhum jogador elegível encontrado nas posições de campo
                if (scorer == null) {
                    List<Player> anyPlayers = playersByPosition.get(Position.GOALKEEPER);
                    if (!anyPlayers.isEmpty()) {
                        // Mesmo para goleiros, usar seleção ponderada
                        scorer = selectPlayerWeightedByStrength(anyPlayers);
                    } else {
                        scorer = "Gol Contra";
                    }
                }
            }
            
            // Registrar gol
            match.addGoal(team.getName(), scorer);
            topScorers.addGoal(scorer, team.getName());
        }
    }
    
    /**
     * Seleciona um jogador com base na ponderação por força.
     * Jogadores com mais força têm maior probabilidade de serem selecionados.
     */
     
    private String selectPlayerWeightedByStrength(List<Player> players) {
        // Calcular a soma total da força
        int totalStrength = players.stream().mapToInt(Player::getStrength).sum();
        
        // Se todos os jogadores têm força zero, use seleção uniforme
        if (totalStrength <= 0) {
            return players.get(random.nextInt(players.size())).getName();
        }
        
        // Gerar um valor aleatório entre 0 e a soma total de força
        double randomValue = random.nextDouble() * totalStrength;
        
        // Seleção com base na ponderação da força
        double cumulativeStrength = 0;
        for (Player player : players) {
            cumulativeStrength += player.getStrength();
            if (randomValue < cumulativeStrength) {
                return player.getName();
            }
        }
        
        // Caso extremamente improvável - retornar o último jogador
        return players.get(players.size() - 1).getName();
    }

    /**
     * Atualiza a tabela de classificação com o resultado de uma partida.
    */
    private void updateStandings(Match match) {
        String homeTeam = match.getHomeTeam().getName();
        String awayTeam = match.getAwayTeam().getName();
        int homeGoals = match.getHomeScore();
        int awayGoals = match.getAwayScore();
        
        if (homeGoals > awayGoals) {
            // Vitória do time da casa
            leagueStandings.addWin(homeTeam, homeGoals, awayGoals);
            leagueStandings.addLoss(awayTeam, awayGoals, homeGoals);
        } else if (homeGoals < awayGoals) {
            // Vitória do time visitante
            leagueStandings.addLoss(homeTeam, homeGoals, awayGoals);
            leagueStandings.addWin(awayTeam, awayGoals, homeGoals);
        } else {
            // Empate
            leagueStandings.addDraw(homeTeam, homeGoals, awayGoals);
            leagueStandings.addDraw(awayTeam, awayGoals, homeGoals);
        }
    }

    /**
     * Obtém a tabela de classificação do torneio.
     */

    public LeagueStandings getLeagueStandings() {
        return leagueStandings;
    }

    /**
     * Obtém a tabela de artilheiros do torneio.
     */

    public TopScorersTable getTopScorers() {
        return topScorers;
    }

    /**
     * Obtém todas as partidas simuladas até o momento.
     */

    public List<Match> getAllSimulatedMatches() {
        return new ArrayList<>(simulatedMatches);
    }

    /**
     * Obtém todas as partidas agendadas.
     * 
     */

    public List<Match> getAllScheduledMatches() {
        return new ArrayList<>(scheduledMatches);
    }

    /**
     * Obtém a rodada atual do torneio.
     */

    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Obtém o número total de rodadas do torneio.
     */

    public int getTotalRounds() {
        return totalRounds;
    }

    /**
     * Verifica se o torneio já foi concluído.
     */

    public boolean isTournamentCompleted() {
        return currentRound >= totalRounds;
    }

    /**
     * Verifica se o torneio está ativo.
     * 
    */

    public boolean isActive() {
        return isActive;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
    
    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }
    
    public java.util.Map<String, Object> getTournamentStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        int totalMatches = simulatedMatches.size();
        int totalGoals = simulatedMatches.stream()
                .mapToInt(m -> m.getHomeScore() + m.getAwayScore())
                .sum();
        
        double averageGoals = totalMatches > 0 ? 
                              (double) totalGoals / totalMatches : 0;
        
        // Formatar média de gols com 2 casas decimais
        averageGoals = Math.round(averageGoals * 100.0) / 100.0;
        
        stats.put("totalMatches", totalMatches);
        stats.put("totalGoals", totalGoals);
        stats.put("averageGoals", averageGoals);
        
        return stats;
    }
}