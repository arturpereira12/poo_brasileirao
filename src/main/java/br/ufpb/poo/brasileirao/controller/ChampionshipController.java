package br.ufpb.poo.brasileirao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ufpb.poo.brasileirao.controladores.TeamController;
import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Standing;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.service.TournamentManager;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/championship")
public class ChampionshipController {

    @Autowired
    private TeamController teamController;
    
    @Autowired
    private TournamentManager tournamentManager;

    /**
     * Lista de classificações no formato antigo (Standing).
     * @deprecated Mantida para compatibilidade com código legado.
     * Use os dados de LeagueStandings diretamente em novas implementações.
     */
    @Deprecated
    private List<Standing> legacyStandings;
    private Random random = new Random();

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
        
        // Manter a compatibilidade com o código legado que usa Standing
        updateLegacyStandings();
    }
    
    private void updateLegacyStandings() {
        if (legacyStandings == null) {
            legacyStandings = new ArrayList<>();
            for (Team team : tournamentManager.getTeams()) {
                legacyStandings.add(new Standing(team));
            }
        }
        
        // Atualizar os standings com os dados atuais (mantido para compatibilidade)
        LeagueStandings standings = tournamentManager.getLeagueStandings();
        for (LeagueStandings.TeamStats ts : standings.getStandings()) {
            Standing standing = legacyStandings.stream()
                    .filter(s -> s.getTeam().getName().equals(ts.getTeamName()))
                    .findFirst()
                    .orElse(null);
                    
            if (standing != null) {
                standing.setPlayed(ts.getPlayed());
                standing.setWins(ts.getWins());
                standing.setDraws(ts.getDraws());
                standing.setLosses(ts.getLosses());
                standing.setGoalsFor(ts.getGoalsFor());
                standing.setGoalsAgainst(ts.getGoalsAgainst());
                standing.setPoints(ts.getPoints());
            }
        }
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
        
        // Getters necessários para o template
        public String getTeamName() { return teamName; }
        public int getPlayed() { return played; }
        public int getWins() { return wins; }
        public int getDraws() { return draws; }
        public int getLosses() { return losses; }
        public int getGoalsFor() { return goalsFor; }
        public int getGoalsAgainst() { return goalsAgainst; }
        public int getPoints() { return points; }
        public int getGoalDifference() { return goalsFor - goalsAgainst; }
        public Team getTeam() { return new Team(teamName); } // Para compatibilidade com o template
    }
} 