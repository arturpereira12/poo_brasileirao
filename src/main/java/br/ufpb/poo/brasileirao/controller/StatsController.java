package br.ufpb.poo.brasileirao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Player;
import br.ufpb.poo.brasileirao.model.Position;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.service.TournamentManager;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable.PlayerStats;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller para gerenciar a exibição das estatísticas do campeonato.
 */
@Controller
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private TournamentManager tournamentManager;

    /**
     * Exibe as estatísticas gerais do campeonato.
     * 
     * @param model Model para passar dados para a view
     * @return nome da view a ser renderizada
     */
    @GetMapping
    public String showStats(Model model) {
        if (!tournamentManager.isActive()) {
            return "redirect:/";
        }

        // Dados básicos do campeonato
        Map<String, Object> basicStats = tournamentManager.getTournamentStats();
        model.addAttribute("basicStats", basicStats);
        model.addAttribute("currentRound", tournamentManager.getCurrentRound());
        model.addAttribute("totalRounds", tournamentManager.getTotalRounds());
        
        // Artilharia
        TopScorersTable topScorers = tournamentManager.getTopScorers();
        List<PlayerStats> topScorersList = topScorers.getTopScorers(10); // Top 10 artilheiros
        model.addAttribute("topScorers", topScorersList);
        
        // Estatísticas dos times
        LeagueStandings standings = tournamentManager.getLeagueStandings();
        List<LeagueStandings.TeamStats> teamStats = standings.getStandings();
        model.addAttribute("teamStats", teamStats);
        
        // Encontrar times com melhores estatísticas
        if (!teamStats.isEmpty()) {
            // Time com mais vitórias
            LeagueStandings.TeamStats mostWins = Collections.max(teamStats, 
                Comparator.comparing(LeagueStandings.TeamStats::getWins));
            model.addAttribute("mostWinsTeam", mostWins);
            
            // Time com melhor ataque
            LeagueStandings.TeamStats bestAttack = Collections.max(teamStats, 
                Comparator.comparing(LeagueStandings.TeamStats::getGoalsFor));
            model.addAttribute("bestAttackTeam", bestAttack);
            
            // Time com melhor defesa (menos gols sofridos)
            LeagueStandings.TeamStats bestDefense = Collections.min(teamStats, 
                Comparator.comparing(LeagueStandings.TeamStats::getGoalsAgainst));
            model.addAttribute("bestDefenseTeam", bestDefense);
        }
        
        // Análise de todos os jogos
        List<Match> allMatches = tournamentManager.getAllSimulatedMatches();
        
        if (!allMatches.isEmpty()) {
            // Total de jogos em casa/fora/empates
            int homeWins = 0;
            int awayWins = 0;
            int draws = 0;
            int totalGoals = 0;
            int highScoringGames = 0; // Jogos com 3+ gols
            int lowScoringGames = 0;  // Jogos com 0-1 gols
            
            // Para encontrar a maior goleada
            int biggestGoalDiff = 0;
            Match biggestWin = null;
            
            // Mapa de placares mais comuns
            Map<String, Integer> scoreFrequency = new HashMap<>();
            
            // Mapa para contar gols por posição (REAL baseado nos artilheiros)
            Map<String, Integer> goalsByPosition = new HashMap<>();
            goalsByPosition.put("Atacante", 0);
            goalsByPosition.put("Meio-Campista", 0);
            goalsByPosition.put("Zagueiro", 0);
            goalsByPosition.put("Goleiro", 0);
            
            // Calcular gols por posição com base nos jogadores que marcaram
            // Primeiro, obtenha todos os times para conseguir a posição de cada jogador
            List<Team> teams = tournamentManager.getTeams();
            Map<String, Player> playersByName = new HashMap<>();
            
            // Criar um mapa de todos os jogadores para fácil acesso por nome
            for (Team team : teams) {
                for (Player player : team.getPlayers()) {
                    playersByName.put(player.getName(), player);
                }
            }
            
            // Contar gols por posição usando os artilheiros reais
            List<PlayerStats> allScorers = topScorers.getTopScorers();
            for (PlayerStats scorer : allScorers) {
                String playerName = scorer.getPlayerName();
                int goals = scorer.getGoals();
                
                // Encontrar o jogador pelo nome
                Player player = playersByName.get(playerName);
                if (player != null) {
                    // Converter Position enum para string legível
                    String positionStr;
                    Position position = player.getPosition();
                    
                    if (position == Position.FORWARD) {
                        positionStr = "Atacante";
                    } else if (position == Position.MIDFIELDER) {
                        positionStr = "Meio-Campista";
                    } else if (position == Position.DEFENDER) {
                        positionStr = "Zagueiro";
                    } else if (position == Position.GOALKEEPER) {
                        positionStr = "Goleiro";
                    } else {
                        // Posição desconhecida ou nula, use atacante como padrão
                        positionStr = "Atacante";
                    }
                    
                    // Adicionar gols à posição correspondente
                    goalsByPosition.put(positionStr, goalsByPosition.get(positionStr) + goals);
                }
            }
            
            // Análise detalhada de cada jogo
            for (Match match : allMatches) {
                int homeGoals = match.getHomeScore();
                int awayGoals = match.getAwayScore();
                int totalMatchGoals = homeGoals + awayGoals;
                totalGoals += totalMatchGoals;
                
                // Vitórias em casa/fora/empates
                if (homeGoals > awayGoals) {
                    homeWins++;
                } else if (homeGoals < awayGoals) {
                    awayWins++;
                } else {
                    draws++;
                }
                
                // Jogos com muitos/poucos gols
                if (totalMatchGoals >= 3) {
                    highScoringGames++;
                } else if (totalMatchGoals <= 1) {
                    lowScoringGames++;
                }
                
                // Verificar maior goleada
                int goalDiff = Math.abs(homeGoals - awayGoals);
                if (goalDiff > biggestGoalDiff) {
                    biggestGoalDiff = goalDiff;
                    biggestWin = match;
                }
                
                // Frequência de placares
                String scoreKey = homeGoals + "x" + awayGoals;
                scoreFrequency.put(scoreKey, scoreFrequency.getOrDefault(scoreKey, 0) + 1);
            }
            
            // Encontrar o placar mais frequente
            String mostCommonScore = Collections.max(scoreFrequency.entrySet(), 
                Map.Entry.comparingByValue()).getKey();
            
            // Adicionar estatísticas ao modelo
            model.addAttribute("homeWins", homeWins);
            model.addAttribute("awayWins", awayWins);
            model.addAttribute("draws", draws);
            model.addAttribute("totalGoals", totalGoals);
            model.addAttribute("highScoringGames", highScoringGames);
            model.addAttribute("lowScoringGames", lowScoringGames);
            model.addAttribute("biggestWin", biggestWin);
            model.addAttribute("biggestGoalDiff", biggestGoalDiff);
            model.addAttribute("mostCommonScore", mostCommonScore);
            model.addAttribute("mostCommonScoreCount", scoreFrequency.get(mostCommonScore));
            model.addAttribute("goalsByPosition", goalsByPosition);
            
            // Criar gráfico de distribuição de placares (top 5 mais comuns)
            List<Map.Entry<String, Integer>> topScores = scoreFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
            
            model.addAttribute("topScores", topScores);
            
            // Estatísticas de mandante vs visitante
            double homeWinPercentage = (double) homeWins / allMatches.size() * 100;
            double awayWinPercentage = (double) awayWins / allMatches.size() * 100;
            double drawPercentage = (double) draws / allMatches.size() * 100;
            
            model.addAttribute("homeWinPercentage", Math.round(homeWinPercentage * 10) / 10.0);
            model.addAttribute("awayWinPercentage", Math.round(awayWinPercentage * 10) / 10.0);
            model.addAttribute("drawPercentage", Math.round(drawPercentage * 10) / 10.0);
        }
        
        return "stats";
    }
}