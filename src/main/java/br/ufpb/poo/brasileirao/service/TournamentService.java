package br.ufpb.poo.brasileirao.service;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;
import org.springframework.stereotype.Service;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TournamentService {
    @Getter private List<Team> teams;
    @Getter private List<Match> matches;
    @Getter private List<LeagueStanding> standings;
    private Random random = new Random();
    
    // Construtor padrão para uso pelo Spring
    public TournamentService() {
        this("Campeonato Brasileiro");
    }

    public TournamentService(String name) {
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.standings = new ArrayList<>();
    }

    public void addTeams(List<Team> teams) {
        this.teams.addAll(teams);
        // Inicializa a classificação
        for (Team team : teams) {
            standings.add(new LeagueStanding(team.getName()));
        }
    }

    public void startTournament() {
        generateFixtures();
    }

    public void simulateAllRemainingRounds() {
        for (Match match : matches) {
            if (!match.isPlayed()) {
                simulateMatch(match);
            }
        }
    }

    private void generateFixtures() {
        // Implementação simples do algoritmo de geração de jogos
        int numberOfTeams = teams.size();
        
        // Para cada rodada no turno
        for (int round = 0; round < numberOfTeams - 1; round++) {
            // Para cada jogo na rodada
            for (int i = 0; i < numberOfTeams / 2; i++) {
                int homeIndex = (round + i) % (numberOfTeams - 1);
                int awayIndex = (numberOfTeams - 1 - i + round) % (numberOfTeams - 1);
                
                // O último time joga contra o time fixo
                if (i == 0) {
                    awayIndex = numberOfTeams - 1;
                }
                
                // Adiciona o jogo à lista de jogos
                matches.add(new Match(teams.get(homeIndex), teams.get(awayIndex), java.time.LocalDateTime.now().plusDays(round)));
            }
        }
        
        // Returno - inverte mandos de campo
        int matchesInFirstHalf = matches.size();
        for (int i = 0; i < matchesInFirstHalf; i++) {
            Match firstLegMatch = matches.get(i);
            matches.add(new Match(firstLegMatch.getAwayTeam(), firstLegMatch.getHomeTeam(), 
                         java.time.LocalDateTime.now().plusDays(matchesInFirstHalf + i)));
        }
    }

    private void simulateMatch(Match match) {
        // Simples simulação de jogo
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        
        int homeStrength = homeTeam.getStrength() != null ? homeTeam.getStrength() : 50;
        int awayStrength = awayTeam.getStrength() != null ? awayTeam.getStrength() : 50;
        
        // Fator de vantagem em casa
        double homeAdvantage = 1.2;
        
        // Calcular probabilidades
        double homeProb = homeStrength * homeAdvantage / (homeStrength * homeAdvantage + awayStrength);
        
        // Gerar gols com base na probabilidade
        int homeGoals = 0;
        int awayGoals = 0;
        
        // Simular 6 chances de gol por time
        for (int i = 0; i < 6; i++) {
            if (random.nextDouble() < homeProb) {
                homeGoals++;
            }
            if (random.nextDouble() < (1 - homeProb)) {
                awayGoals++;
            }
        }
        
        // Limitando o número máximo de gols para tornar mais realista
        homeGoals = Math.min(homeGoals, 5);
        awayGoals = Math.min(awayGoals, 4);
        
        // Atualizar o resultado da partida
        match.setHomeScore(homeGoals);
        match.setAwayScore(awayGoals);
        match.setPlayed(true);
        
        // Atualizar a classificação
        updateStandings(homeTeam.getName(), awayTeam.getName(), homeGoals, awayGoals);
    }
    
    private void updateStandings(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        // Atualizar estatísticas do time da casa
        LeagueStanding homeStanding = findStanding(homeTeam);
        homeStanding.incrementGamesPlayed();
        homeStanding.addGoalsFor(homeGoals);
        homeStanding.addGoalsAgainst(awayGoals);
        
        // Atualizar estatísticas do time visitante
        LeagueStanding awayStanding = findStanding(awayTeam);
        awayStanding.incrementGamesPlayed();
        awayStanding.addGoalsFor(awayGoals);
        awayStanding.addGoalsAgainst(homeGoals);
        
        // Atualizar pontos
        if (homeGoals > awayGoals) {
            homeStanding.addWin();
            awayStanding.addLoss();
        } else if (homeGoals < awayGoals) {
            homeStanding.addLoss();
            awayStanding.addWin();
        } else {
            homeStanding.addDraw();
            awayStanding.addDraw();
        }
    }
    
    private LeagueStanding findStanding(String teamName) {
        return standings.stream()
                .filter(s -> s.getTeamName().equals(teamName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamName));
    }
    
    public List<Match> getAllSimulatedMatches() {
        return matches;
    }
    
    public LeagueStandings getLeagueStandings() {
        return new LeagueStandings(standings);
    }
    
    public TopScorers getTopScorers() {
        // Implementação simplificada
        return new TopScorers();
    }
    
    // Classes internas para representar a classificação
    
    /**
     * @deprecated Use br.ufpb.poo.brasileirao.tournament.LeagueStandings em vez desta classe.
     */
    @Deprecated
    public static class LeagueStandings {
        private List<LeagueStanding> standings;
        
        public LeagueStandings(List<LeagueStanding> standings) {
            this.standings = new ArrayList<>(standings);
            sortStandings();
        }
        
        private void sortStandings() {
            standings.sort((a, b) -> {
                if (a.getPoints() != b.getPoints())
                    return b.getPoints() - a.getPoints();
                if (a.getGoalDifference() != b.getGoalDifference())
                    return b.getGoalDifference() - a.getGoalDifference();
                return b.getGoalsFor() - a.getGoalsFor();
            });
        }
        
        public List<LeagueStanding> getStandings() {
            return standings;
        }
        
        public int getNumberOfTeams() {
            return standings.size();
        }
    }
    
    /**
     * @deprecated Use br.ufpb.poo.brasileirao.tournament.LeagueStandings.TeamStats em vez desta classe.
     */
    @Deprecated
    public static class LeagueStanding {
        private String teamName;
        private int gamesPlayed;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;
        
        public LeagueStanding(String teamName) {
            this.teamName = teamName;
            this.gamesPlayed = 0;
            this.wins = 0;
            this.draws = 0;
            this.losses = 0;
            this.goalsFor = 0;
            this.goalsAgainst = 0;
        }
        
        public void incrementGamesPlayed() {
            this.gamesPlayed++;
        }
        
        public void addWin() {
            this.wins++;
        }
        
        public void addDraw() {
            this.draws++;
        }
        
        public void addLoss() {
            this.losses++;
        }
        
        public void addGoalsFor(int goals) {
            this.goalsFor += goals;
        }
        
        public void addGoalsAgainst(int goals) {
            this.goalsAgainst += goals;
        }
        
        public int getPoints() {
            return (wins * 3) + draws;
        }
        
        public int getGoalDifference() {
            return goalsFor - goalsAgainst;
        }
        
        public String getTeamName() {
            return teamName;
        }
        
        public int getGamesPlayed() {
            return gamesPlayed;
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
    }
    
    public static class TopScorers {
        private List<ScorerStats> scorers = new ArrayList<>();
        
        public List<ScorerStats> getTopScorers(int limit) {
            return scorers.stream()
                    .sorted((a, b) -> b.getGoals() - a.getGoals())
                    .limit(limit)
                    .toList();
        }
        
        public static class ScorerStats {
            private String playerName;
            private String teamName;
            private int goals;
            
            public ScorerStats(String playerName, String teamName) {
                this.playerName = playerName;
                this.teamName = teamName;
                this.goals = 0;
            }
            
            public void addGoal() {
                this.goals++;
            }
            
            public String getPlayerName() {
                return playerName;
            }
            
            public String getTeamName() {
                return teamName;
            }
            
            public int getGoals() {
                return goals;
            }
        }
    }
}