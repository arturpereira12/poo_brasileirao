package br.ufpb.poo.brasileirao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.service.TournamentManager;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Getter;

@Controller
@RequestMapping("/championship")
public class ChampionshipController {

    @Autowired
    private TeamController teamController;
    
    @Autowired
    private TournamentManager tournamentManager;

    @GetMapping
    public String showChampionship(Model model) {
        if (!tournamentManager.isActive()) {
            initializeChampionship();
        }

        // Adiciona os dados ao modelo
        var stats = tournamentManager.getTournamentStats();
        model.addAttribute("totalMatches", stats.get("totalMatches"));
        model.addAttribute("totalGoals", stats.get("totalGoals"));
        model.addAttribute("averageGoals", stats.get("averageGoals"));
        model.addAttribute("round", tournamentManager.getCurrentRound());
        model.addAttribute("totalRounds", tournamentManager.getTotalRounds());
        model.addAttribute("canSimulateNextRound", !tournamentManager.isTournamentCompleted());
        
        // Adapta a tabela de classificação do formato LeagueStandings para o formato esperado pelo template
        List<StandingAdapter> adaptedStandings = adaptStandings(tournamentManager.getLeagueStandings());
        model.addAttribute("standings", adaptedStandings);
        
        // Próximos jogos (jogos da próxima rodada)
        List<Match> nextMatches = tournamentManager.getMatchesForRound(tournamentManager.getCurrentRound() + 1);
        model.addAttribute("nextMatches", nextMatches);
        
        // Últimos resultados (jogos da rodada atual)
        List<Match> recentMatches = tournamentManager.getMatchesForRound(tournamentManager.getCurrentRound());
        model.addAttribute("recentMatches", recentMatches);
        
        return "championship";
    }

    @PostMapping("/simulate-next-round")
    @ResponseBody
    public String simulateNextRound() {
        if (tournamentManager.isTournamentCompleted()) {
            return "{\"status\": \"error\", \"message\": \"O campeonato já terminou!\"}";
        }

        boolean success = tournamentManager.simulateNextRound();
        
        if (success) {
            return "{\"status\": \"success\", \"message\": \"Rodada " + tournamentManager.getCurrentRound() + " simulada com sucesso!\"}";
        } else {
            return "{\"status\": \"error\", \"message\": \"Não foi possível simular a próxima rodada!\"}";
        }
    }

    private void initializeChampionship() {
        List<Team> allTeams = teamController.leiaDoArquivo();
        
        // Seleciona apenas os 20 primeiros times
        List<Team> teams = allTeams.subList(0, Math.min(20, allTeams.size()));
        
        tournamentManager.addTeams(teams);
        tournamentManager.startTournament();
    }
    
    /**
     * Adapta os dados de LeagueStandings para o formato esperado pelo template.
     */
    private List<StandingAdapter> adaptStandings(LeagueStandings standings) {
        return standings.getStandings().stream()
                .map(ts -> new StandingAdapter(ts))
                .collect(Collectors.toList());
    }
    
    /**
     * Classe adaptadora para compatibilidade com o template.
     */
    @Getter
    public static class StandingAdapter {
        private String teamName;
        private int played;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;
        private int points;
        
        public StandingAdapter(LeagueStandings.TeamStats ts) {
            this.teamName = ts.getTeamName();
            this.played = ts.getPlayed();
            this.wins = ts.getWins();
            this.draws = ts.getDraws();
            this.losses = ts.getLosses();
            this.goalsFor = ts.getGoalsFor();
            this.goalsAgainst = ts.getGoalsAgainst();
            this.points = ts.getPoints();
        }
        
        public int getGoalDifference() { return goalsFor - goalsAgainst; }
        public Team getTeam() { return new Team(teamName); } // Para compatibilidade com o template
    }
    
    @PostMapping("/simulate-rounds")
    @ResponseBody
    public Map<String, String> simulateRounds(@RequestBody Map<String, Integer> request) {
        Map<String, String> response = new HashMap<>();
        
        if (tournamentManager.isTournamentCompleted()) {
            response.put("status", "error");
            response.put("message", "O campeonato já terminou!");
            return response;
        }
        
        int roundsToSimulate = request.get("rounds");
        int simulatedCount = 0;
        
        // Simular todas as rodadas restantes
        if (roundsToSimulate == -1) {
            tournamentManager.simulateAllRemainingRounds();
            response.put("status", "success");
            response.put("message", "Todas as rodadas restantes foram simuladas com sucesso!");
        } 
        // Simular um número específico de rodadas
        else {
            for (int i = 0; i < roundsToSimulate; i++) {
                boolean success = tournamentManager.simulateNextRound();
                if (success) {
                    simulatedCount++;
                } else {
                    break; // O torneio terminou
                }
            }
            
            String message;
            if (simulatedCount == 1) {
                message = "1 rodada simulada com sucesso!";
            } else {
                message = simulatedCount + " rodadas simuladas com sucesso!";
            }
            
            response.put("status", "success");
            response.put("message", message);
        }
        
        return response;
    }
}