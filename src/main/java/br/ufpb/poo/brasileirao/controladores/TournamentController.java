package br.ufpb.poo.brasileirao.controladores;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador para gerenciar um torneio de futebol.
 * Responsável por adicionar times, gerar o calendário de jogos e simular partidas.
 */
public class TournamentController {
    private String tournamentName;
    private List<Team> teams;
    private List<Match> scheduledMatches;
    private List<Match> simulatedMatches;
    private LeagueStandings leagueStandings;
    private TopScorersTable topScorers;
    private int currentRound;
    private int totalRounds;
    private Random random;

    /**
     * Cria um novo controlador de torneio.
     * 
     * @param tournamentName o nome do torneio
     */
    public TournamentController(String tournamentName) {
        this.tournamentName = tournamentName;
        this.teams = new ArrayList<>();
        this.scheduledMatches = new ArrayList<>();
        this.simulatedMatches = new ArrayList<>();
        this.leagueStandings = new LeagueStandings();
        this.topScorers = new TopScorersTable();
        this.currentRound = 0;
        this.random = new Random();
    }

    /**
     * Adiciona times ao torneio.
     * 
     * @param teams a lista de times a serem adicionados
     */
    public void addTeams(List<Team> teams) {
        this.teams.addAll(teams);
        for (Team team : teams) {
            this.leagueStandings.addTeam(team.getName());
        }
    }

    /**
     * Inicia o torneio gerando o calendário de jogos.
     */
    public void startTournament() {
        generateSchedule();
        this.currentRound = 0;
        this.totalRounds = calculateTotalRounds();
    }

    /**
     * Gera o calendário de jogos para o torneio em formato de pontos corridos.
     */
    private void generateSchedule() {
        int numberOfTeams = teams.size();
        
        // Se o número de times for ímpar, adiciona um time de folga
        if (numberOfTeams % 2 != 0) {
            numberOfTeams++;
        }
        
        int totalRounds = numberOfTeams - 1;
        int matchesPerRound = numberOfTeams / 2;
        
        List<Integer> teamIndices = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++) {
            teamIndices.add(i);
        }
        
        LocalDate startDate = LocalDate.now();
        
        for (int round = 0; round < totalRounds; round++) {
            LocalDate roundDate = startDate.plusDays(round * 7); // Uma rodada por semana
            
            for (int match = 0; match < matchesPerRound; match++) {
                int homeIndex = teamIndices.get(match);
                int awayIndex = teamIndices.get(numberOfTeams - 1 - match);
                
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
            
            // Atualizar tabela de classificação
            updateStandings(match);
            
            // Atualizar tabela de artilheiros
            updateScorers(match);
        }
        
        return true;
    }

    /**
     * Simula todas as rodadas restantes do torneio.
     */
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
    private List<Match> getMatchesForRound(int round) {
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
        int homeStrength = homeTeam.getStrength() + 10; // Vantagem de jogar em casa
        int awayStrength = awayTeam.getStrength();
        
        // Simular gols do time da casa
        int homeGoals = simulateGoals(homeStrength);
        
        // Simular gols do time visitante
        int awayGoals = simulateGoals(awayStrength);
        
        // Definir resultado da partida
        match.setResult(homeGoals, awayGoals);
        
        // Simular os artilheiros dos gols
        simulateGoalScorers(match, homeTeam, homeGoals, true);
        simulateGoalScorers(match, awayTeam, awayGoals, false);
    }

    /**
     * Simula os gols de um time com base em sua força.
     * 
     * @param strength a força do time
     * @return o número de gols marcados
     */
    private int simulateGoals(int strength) {
        // Base de gols média (pode ser ajustada)
        double baseGoals = strength / 20.0;
        
        // Adicionar aleatoriedade
        double randomFactor = 0.5 + random.nextDouble();
        
        // Calcular gols
        int goals = (int) Math.round(baseGoals * randomFactor);
        
        // Limitar número de gols a um valor máximo razoável
        return Math.min(goals, 7);
    }

    /**
     * Simula os jogadores que marcaram gols na partida.
     * 
     * @param match a partida
     * @param team o time que marcou os gols
     * @param goals o número de gols marcados
     * @param isHome true se é o time da casa, false se é o visitante
     */
    private void simulateGoalScorers(Match match, Team team, int goals, boolean isHome) {
        
        for (int i = 0; i < goals; i++) {
            // Probabilidades de gols por posição: 70% atacantes, 25% meio-campistas, 5% defensores
            String position;
            double rand = random.nextDouble();
            if (rand < 0.7) {
                position = "Atacante";
            } else if (rand < 0.95) {
                position = "Meio-Campista";
            } else {
                position = "Zagueiro";
            }
            
            // Filtrar jogadores da posição
            List<String> eligiblePlayers = new ArrayList<>();
            for (var player : team.getPlayers()) {
                if (player.getPosition().equals(position)) {
                    eligiblePlayers.add(player.getName());
                }
            }
            
            String scorer;
            if (!eligiblePlayers.isEmpty()) {
                // Selecionar jogador aleatório para marcar o gol
                scorer = eligiblePlayers.get(random.nextInt(eligiblePlayers.size()));
            } else {
                // Se não tiver jogadores na posição, selecionar qualquer jogador
                var players = team.getPlayers();
                scorer = players.get(random.nextInt(players.size())).getName();
            }
            
            // Registrar gol
            match.addGoal(team.getName(), scorer);
            topScorers.addGoal(scorer, team.getName());
        }
    }

    /**
     * Atualiza a tabela de classificação com o resultado de uma partida.
     * 
     * @param match a partida a ser considerada
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
     * Atualiza a tabela de artilheiros com os gols de uma partida.
     * Este método é chamado automaticamente após a simulação de uma partida.
     * 
     * @param match a partida cujos gols serão considerados
     */
    private void updateScorers(Match match) {
        // Os gols já foram registrados durante a simulação da partida,
        // na chamada de simulateGoalScorers() que adiciona os gols à 
        // tabela de artilheiros (topScorers) diretamente.
        // Este método está aqui para manter a consistência da API e
        // permitir futuras extensões se necessário.
    }

    /**
     * Obtém a tabela de classificação do torneio.
     * 
     * @return a tabela de classificação
     */
    public LeagueStandings getLeagueStandings() {
        return leagueStandings;
    }

    /**
     * Obtém a tabela de artilheiros do torneio.
     * 
     * @return a tabela de artilheiros
     */
    public TopScorersTable getTopScorers() {
        return topScorers;
    }

    /**
     * Obtém todas as partidas simuladas até o momento.
     * 
     * @return lista de partidas simuladas
     */
    public List<Match> getAllSimulatedMatches() {
        return new ArrayList<>(simulatedMatches);
    }

    /**
     * Obtém a rodada atual do torneio.
     * 
     * @return a rodada atual
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Obtém o número total de rodadas do torneio.
     * 
     * @return o número total de rodadas
     */
    public int getTotalRounds() {
        return totalRounds;
    }

    /**
     * Verifica se o torneio já foi concluído.
     * 
     * @return true se o torneio foi concluído, false caso contrário
     */
    public boolean isTournamentCompleted() {
        return currentRound >= totalRounds;
    }

    /**
     * Obtém o nome do torneio.
     * 
     * @return o nome do torneio
     */
    public String getTournamentName() {
        return tournamentName;
    }
} 