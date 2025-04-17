// /*package br.ufpb.poo.brasileirao;

// import br.ufpb.poo.brasileirao.match.Match;
// import br.ufpb.poo.brasileirao.match.MatchSimulator;
// import br.ufpb.poo.brasileirao.model.Team;
// import br.ufpb.poo.brasileirao.controladores.TeamController;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;

// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.*;
// import java.util.stream.Collectors;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;

// @SpringBootApplication
// public class Main {
//     // Adicionar Random como estático para uso em métodos
//     private static final Random random = new Random();

//     public static void main(String[] args) {
//         SpringApplication.run(Main.class, args);
//     }
    
//     @Bean
//     public CommandLineRunner simulateChampionship() {
//         return args -> {
//             try {
//                 System.out.println("=== SIMULADOR DO CAMPEONATO BRASILEIRO ===");
                
//                 // Carregar todos os times do JSON
//                 TeamController teamController = new TeamController();
//                 List<Team> allTeams = teamController.leiaDoArquivo();
                
//                 if (allTeams == null || allTeams.isEmpty()) {
//                     System.out.println("Nenhum time encontrado para simular!");
//                     return;
//                 }
                
//                 System.out.println("Total de times disponíveis: " + allTeams.size());
                
//                 // Selecionar 20 times com base na forma (força)
//                 List<Team> selectedTeams = selectTeamsBasedOnForm(allTeams, 20);
//                 System.out.println("\nTimes selecionados para o campeonato:");
//                 for (Team team : selectedTeams) {
//                     // Garantir que form não seja nulo
//                     Integer form = team.getForm() != null ? team.getForm() : 50;
//                     System.out.println("- " + team.getName() + " (Força: " + form + ")");
//                 }
                
//                 // Criar tabela de classificação
//                 Map<String, TeamStats> leagueTable = new HashMap<>();
//                 for (Team team : selectedTeams) {
//                     leagueTable.put(team.getName(), new TeamStats(team.getName()));
//                 }
                
//                 // Criar lista para armazenar todas as partidas
//                 List<Match> allMatches = new ArrayList<>();
                
//                 // Gerar calendário do campeonato (formato todos contra todos, ida e volta)
//                 List<List<Match>> rounds = generateChampionshipSchedule(selectedTeams);
                
//                 // Simular todas as rodadas
//                 System.out.println("\n=== SIMULAÇÃO DO CAMPEONATO ===");
//                 int roundNumber = 1;
                
//                 for (List<Match> round : rounds) {
//                     System.out.println("\nRodada " + roundNumber + ":");
                    
//                     for (Match match : round) {
//                         // Simular partida
//                         MatchSimulator.simulateMatch(match);
//                         allMatches.add(match);
                        
//                         // Atualizar estatísticas na tabela
//                         updateLeagueTable(leagueTable, match);
                        
//                         System.out.println(match.getScore());
//                     }
                    
//                     roundNumber++;
//                 }
                
//                 // Ordenar a tabela de classificação
//                 List<TeamStats> sortedTable = leagueTable.values().stream()
//                     .sorted(Comparator.comparing(TeamStats::getPoints)
//                             .thenComparing(TeamStats::getGoalDifference)
//                             .thenComparing(TeamStats::getGoalsFor)
//                             .reversed())
//                     .collect(Collectors.toList());
                
//                 // Gerar top artilheiros
//                 Map<String, Integer> goalScorers = new HashMap<>();
//                 for (Match match : allMatches) {
//                     // Garantir que getGoalScorers() retorna uma lista não nula
//                     List<String> scorers = match.getGoalScorers();
//                     if (scorers != null) {
//                         for (String scorer : scorers) {
//                             goalScorers.put(scorer, goalScorers.getOrDefault(scorer, 0) + 1);
//                         }
//                     }
//                 }
                
//                 List<Map.Entry<String, Integer>> topScorers = goalScorers.entrySet().stream()
//                     .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                     .limit(20)
//                     .collect(Collectors.toList());
                
//                 // Imprimir tabela final
//                 System.out.println("\n=== CLASSIFICAÇÃO FINAL ===");
//                 System.out.println("Pos | Time                | P  | J  | V  | E  | D  | GP | GC | SG");
//                 System.out.println("------------------------------------------------------------------");
                
//                 int position = 1;
//                 for (TeamStats stats : sortedTable) {
//                     System.out.printf("%-3d | %-20s | %-2d | %-2d | %-2d | %-2d | %-2d | %-2d | %-2d | %-2d\n", 
//                             position++, stats.getTeamName(), stats.getPoints(), stats.getMatches(), 
//                             stats.getWins(), stats.getDraws(), stats.getLosses(), 
//                             stats.getGoalsFor(), stats.getGoalsAgainst(), stats.getGoalDifference());
//                 }
                
//                 // Imprimir artilheiros
//                 System.out.println("\n=== ARTILHEIROS ===");
//                 for (int i = 0; i < Math.min(10, topScorers.size()); i++) {
//                     Map.Entry<String, Integer> scorer = topScorers.get(i);
//                     System.out.printf("%-2d. %-25s %d gols\n", i+1, scorer.getKey(), scorer.getValue());
//                 }
                
//                 // Exportar dados para JSON
//                 exportResultsToJson(selectedTeams, allMatches, sortedTable, topScorers);
                
//                 System.out.println("\nSimulação concluída e dados exportados com sucesso!");
//             } catch (Exception e) {
//                 System.err.println("Erro durante a simulação: " + e.getMessage());
//                 e.printStackTrace();
//             }
//         };
//     }
    
    
//     private static List<Team> selectTeamsBasedOnForm(List<Team> allTeams, int count) {
//     if (allTeams.size() <= count) {
//         return allTeams; // Retorna todos os times se não tivermos suficientes
//     }
    
//     // Calcular pesos totais
//     int totalWeight = 0;
//     for (Team team : allTeams) {
//         // Substituir getForm() pelo método correto, por exemplo, getStrength() ou getOverallRating()
//         Integer strength = team.getOverallRating(); // ou outra propriedade que exista
//         int weightValue = (strength != null) ? strength : 50; // Valor padrão de 50 se não tiver força
//         totalWeight += weightValue;
//     }
    
//     // Selecionar times usando seleção ponderada
//     List<Team> selectedTeams = new ArrayList<>();
//     Set<Integer> selectedIndexes = new HashSet<>();
    
//     while (selectedTeams.size() < count) {
//         int randomValue = random.nextInt(totalWeight);
//         int sumWeight = 0;
        
//         for (int i = 0; i < allTeams.size(); i++) {
//             if (selectedIndexes.contains(i)) continue;
            
//             Team team = allTeams.get(i);
//             // Substituir getForm() pelo mesmo método usado acima
//             Integer strength = team.getOverallRating(); // ou outra propriedade que exista
//             int weightValue = (strength != null) ? strength : 50;
//             sumWeight += weightValue;
            
//             if (randomValue < sumWeight) {
//                 selectedTeams.add(team);
//                 selectedIndexes.add(i);
//                 // Reduzir peso total
//                 totalWeight -= weightValue;
//                 break;
//             }
//         }
//     }
    
//     return selectedTeams;
// }
    
//     /**
//      * Gera um calendário de campeonato no formato todos contra todos (ida e volta)
//      */
//     private static List<List<Match>> generateChampionshipSchedule(List<Team> teams) {
//         int numTeams = teams.size();
//         int numRounds = 2 * (numTeams - 1); // Ida e volta
//         int matchesPerRound = numTeams / 2;
        
//         List<List<Match>> rounds = new ArrayList<>(numRounds);
        
//         // Algoritmo Robin Round para gerar os jogos da primeira metade (jogos de ida)
//         for (int round = 0; round < numTeams - 1; round++) {
//             List<Match> matches = new ArrayList<>(matchesPerRound);
            
//             for (int match = 0; match < matchesPerRound; match++) {
//                 int home = (round + match) % (numTeams - 1);
//                 int away = (numTeams - 1 - match + round) % (numTeams - 1);
                
//                 // Time fixo joga contra o time na posição "away"
//                 if (match == 0) {
//                     away = numTeams - 1;
//                 }
                
//                 // Alterna entre casa e fora para balancear
//                 if (round % 2 == 1) {
//                     matches.add(new Match(teams.get(away), teams.get(home)));
//                 } else {
//                     matches.add(new Match(teams.get(home), teams.get(away)));
//                 }
//             }
            
//             rounds.add(matches);
//         }
        
//         // Gera os jogos de volta invertendo mando de campo dos jogos de ida
//         for (int i = 0; i < numTeams - 1; i++) {
//             List<Match> firstHalfMatches = rounds.get(i);
//             List<Match> secondHalfMatches = new ArrayList<>();
            
//             for (Match match : firstHalfMatches) {
//                 secondHalfMatches.add(new Match(match.getAwayTeam(), match.getHomeTeam()));
//             }
            
//             rounds.add(secondHalfMatches);
//         }
        
//         return rounds;
//     }
    
//     /**
//      * Atualiza a tabela de classificação com o resultado de uma partida
//      */
//     private static void updateLeagueTable(Map<String, TeamStats> table, Match match) {
//         Team homeTeam = match.getHomeTeam();
//         Team awayTeam = match.getAwayTeam();
//         int homeGoals = match.getHomeGoals().size();
//         int awayGoals = match.getAwayGoals().size();
        
//         // Atualizando estatísticas do time da casa
//         TeamStats homeStats = table.get(homeTeam.getName());
//         homeStats.incrementMatches();
//         homeStats.incrementGoalsFor(homeGoals);
//         homeStats.incrementGoalsAgainst(awayGoals);
        
//         // Atualizando estatísticas do time visitante
//         TeamStats awayStats = table.get(awayTeam.getName());
//         awayStats.incrementMatches();
//         awayStats.incrementGoalsFor(awayGoals);
//         awayStats.incrementGoalsAgainst(homeGoals);
        
//         // Atualizando resultados (vitória, empate ou derrota)
//         if (homeGoals > awayGoals) {
//             homeStats.incrementWins();
//             awayStats.incrementLosses();
//         } else if (homeGoals < awayGoals) {
//             homeStats.incrementLosses();
//             awayStats.incrementWins();
//         } else {
//             homeStats.incrementDraws();
//             awayStats.incrementDraws();
//         }
//     }
    
//     /**
//      * Exporta os resultados para arquivos JSON em uma pasta específica
//      */
//     private static void exportResultsToJson(List<Team> teams, List<Match> matches, 
//                                 List<TeamStats> leagueTable, List<Map.Entry<String, Integer>> topScorers) {
//     // Definir diretório base para simulações
//     String baseSimulationPath = "C:\\Users\\artur\\OneDrive\\Área de Trabalho\\UFPB_2_PERIODO\\Programacao orientada a objetos\\brasileirao\\src\\main\\simulations";
    
//     // Criar o diretório base se não existir
//     File baseDir = new File(baseSimulationPath);
//     if (!baseDir.exists()) {
//         if (baseDir.mkdirs()) {
//             System.out.println("Diretório base de simulações criado: " + baseDir.getPath());
//         } else {
//             System.err.println("Não foi possível criar o diretório base de simulações!");
//             return;
//         }
//     }
    
//     // Determinar o número da próxima simulação
//     int simulationNumber = 1;
//     File dir;
    
//     // Encontrar o próximo número de simulação disponível
//     do {
//         dir = new File(baseDir, "SIMULACAO_" + simulationNumber);
//         simulationNumber++;
//     } while (dir.exists());
    
//     // Criar a pasta para a simulação atual
//     if (dir.mkdir()) {
//         System.out.println("Pasta de simulação criada: " + dir.getPath());
//     } else {
//         System.err.println("Não foi possível criar a pasta de simulação!");
//         return; // Sai da função se não conseguir criar o diretório
//     }
    
//     Gson gson = new GsonBuilder().setPrettyPrinting().create();
//     String basePath = dir.getPath() + File.separator;
    
//     try {
//         // Exportar times
//         FileWriter teamsWriter = new FileWriter(basePath + "championship_teams.json");
//         gson.toJson(teams, teamsWriter);
//         teamsWriter.close();
            
//             // Exportar partidas
//             FileWriter matchesWriter = new FileWriter(basePath + "championship_matches.json");
//             gson.toJson(matches, matchesWriter);
//             matchesWriter.close();
            
//             // Exportar tabela de classificação
//             FileWriter tableWriter = new FileWriter(basePath + "championship_table.json");
//             gson.toJson(leagueTable, tableWriter);
//             tableWriter.close();
            
//             // Exportar artilheiros
//             Map<String, Integer> scorersMap = new HashMap<>();
//             for (Map.Entry<String, Integer> entry : topScorers) {
//                 scorersMap.put(entry.getKey(), entry.getValue());
//             }
            
//             FileWriter scorersWriter = new FileWriter(basePath + "championship_scorers.json");
//             gson.toJson(scorersMap, scorersWriter);
//             scorersWriter.close();
            
//             // Exportar um arquivo de metadados da simulação
//             Map<String, Object> metadata = new HashMap<>();
//             metadata.put("simulationNumber", simulationNumber-1);
//             metadata.put("simulationDate", new Date().toString());
//             metadata.put("teamsCount", teams.size());
//             metadata.put("matchesCount", matches.size());
            
//             FileWriter metadataWriter = new FileWriter(basePath + "simulation_metadata.json");
//             gson.toJson(metadata, metadataWriter);
//             metadataWriter.close();
            
//         } catch (Exception e) {
//             System.err.println("Erro ao exportar os dados: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
    
//     /**
//      * Classe interna para armazenar estatísticas de times na tabela
//      */
//     static class TeamStats {
//         private String teamName;
//         private int matches = 0;
//         private int wins = 0;
//         private int draws = 0;
//         private int losses = 0;
//         private int goalsFor = 0;
//         private int goalsAgainst = 0;
        
//         public TeamStats(String teamName) {
//             this.teamName = teamName;
//         }
        
//         public String getTeamName() {
//             return teamName;
//         }
        
//         public int getMatches() {
//             return matches;
//         }
        
//         public void incrementMatches() {
//             this.matches++;
//         }
        
//         public int getWins() {
//             return wins;
//         }
        
//         public void incrementWins() {
//             this.wins++;
//         }
        
//         public int getDraws() {
//             return draws;
//         }
        
//         public void incrementDraws() {
//             this.draws++;
//         }
        
//         public int getLosses() {
//             return losses;
//         }
        
//         public void incrementLosses() {
//             this.losses++;
//         }
        
//         public int getGoalsFor() {
//             return goalsFor;
//         }
        
//         public void incrementGoalsFor(int goals) {
//             this.goalsFor += goals;
//         }
        
//         public int getGoalsAgainst() {
//             return goalsAgainst;
//         }
        
//         public void incrementGoalsAgainst(int goals) {
//             this.goalsAgainst += goals;
//         }
        
//         public int getPoints() {
//             return (wins * 3) + draws;
//         }
        
//         public int getGoalDifference() {
//             return goalsFor - goalsAgainst;
//         }
//     }
// }