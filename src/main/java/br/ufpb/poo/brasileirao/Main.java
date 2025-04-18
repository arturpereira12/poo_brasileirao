package br.ufpb.poo.brasileirao;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.model.Player;
import br.ufpb.poo.brasileirao.controladores.TeamController;
import br.ufpb.poo.brasileirao.controladores.TournamentController;
import br.ufpb.poo.brasileirao.tournament.LeagueStandings;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    public static class TournamentSimulator {
        private final ObjectMapper mapper;

        public TournamentSimulator(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        public String simulateTournament() throws Exception {
            System.out.println("=== SIMULADOR DO CAMPEONATO BRASILEIRO ===");

            try {
                List<Team> teams = carregarTimes();
                if (teams == null || teams.isEmpty()) {
                    return "Nenhum time encontrado para simular!";
                }

                TournamentController tournamentController = executarTorneio(teams);
                String simulationFolder = salvarResultados(tournamentController);
                
                return "Simulação concluída com sucesso! Resultados salvos em: " + simulationFolder;

            } catch (Exception e) {
                System.err.println("Erro durante a simulação do torneio: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }

        private List<Team> carregarTimes() {
            TeamController teamController = new TeamController();
            List<Team> allTeams = teamController.leiaDoArquivo();

            if (allTeams == null || allTeams.isEmpty()) {
                System.out.println("Nenhum time encontrado para simular!");
                return null;
            }

            System.out.println("Total de times disponíveis: " + allTeams.size());
            
            // Selecionar apenas 20 times com base na força (strength)
            List<Team> selectedTeams = selecionarTimesPorForca(allTeams, 20);
            
            System.out.println("Times selecionados para o campeonato: " + selectedTeams.size());
            
            return selectedTeams;
        }

        private List<Team> selecionarTimesPorForca(List<Team> allTeams, int quantidade) {
            if (allTeams.size() <= quantidade) {
                return new ArrayList<>(allTeams);
            }
            
            List<Team> selectedTeams = new ArrayList<>();
            List<Team> candidatos = new ArrayList<>(allTeams);
            
            int somaTotal = 0;
            for (Team team : candidatos) {
                somaTotal += team.getStrength();
            }
            
            Random random = new Random();
            
            for (int i = 0; i < quantidade; i++) {
                if (candidatos.isEmpty()) {
                    break;
                }
                
                int somaParcial = 0;
                for (Team team : candidatos) {
                    somaParcial += team.getStrength();
                }
                
                int valorAleatorio = random.nextInt(somaParcial);
                int acumulado = 0;
                int indiceEscolhido = 0;
                
                for (int j = 0; j < candidatos.size(); j++) {
                    acumulado += candidatos.get(j).getStrength();
                    if (valorAleatorio < acumulado) {
                        indiceEscolhido = j;
                        break;
                    }
                }
                
                selectedTeams.add(candidatos.get(indiceEscolhido));
                candidatos.remove(indiceEscolhido);
            }
            
            System.out.println("\n=== TIMES SELECIONADOS PARA O CAMPEONATO ===");
            for (Team team : selectedTeams) {
                System.out.printf("%s (Força: %d)%n", team.getName(), team.getStrength());
            }
            System.out.println();
            
            return selectedTeams;
        }

        private TournamentController executarTorneio(List<Team> teams) throws Exception {
            TournamentController tournamentController = new TournamentController("Campeonato Brasileiro 2023");
            tournamentController.addTeams(teams);
            System.out.println("Times adicionados ao torneio: " + tournamentController.getLeagueStandings().getNumberOfTeams());

            tournamentController.startTournament();
            System.out.println("Torneio iniciado e calendário gerado!");

            System.out.println("Simulando todas as rodadas...");
            tournamentController.simulateAllRemainingRounds();
            System.out.println("Simulação concluída!");

            return tournamentController;
        }

        private String salvarResultados(TournamentController tournamentController) throws Exception {
            LeagueStandings standings = tournamentController.getLeagueStandings();
            List<Match> allMatches = tournamentController.getAllSimulatedMatches();
            TopScorersTable scorers = tournamentController.getTopScorers();
        
            System.out.println("\n=== RESUMO DO CAMPEONATO ===");
            System.out.println("Campeão: " + standings.getStandings().get(0).getTeamName());
            System.out.println("Total de jogos: " + allMatches.size());
        
            List<TopScorersTable.PlayerStats> topScorers = scorers.getTopScorers(10);
            if (topScorers != null && !topScorers.isEmpty()) {
                TopScorersTable.PlayerStats artilheiro = topScorers.get(0);
                System.out.println("Artilheiro: " + artilheiro.getPlayerName() + " - " + artilheiro.getGoals() + " gols");
            } else {
                System.out.println("Não foi possível determinar o artilheiro.");
            }
        
            String simulationFolderName = criarPastaSimulacao();
            
            String standingsPath = simulationFolderName + "/standings.json";
            String matchesPath = simulationFolderName + "/matches.json";
            String scorersPath = simulationFolderName + "/scorers.json";
            
            mapper.writeValue(new File(standingsPath), standings.getStandings());
            mapper.writeValue(new File(matchesPath), allMatches);
            mapper.writeValue(new File(scorersPath), topScorers);
        
            System.out.println("Resultados salvos na pasta '" + simulationFolderName + "'");
            return simulationFolderName;
        }
        
        private void criarDiretorioResultados() {
            File resultsDir = new File("results");
            if (!resultsDir.exists()) {
                boolean criado = resultsDir.mkdir();
                if (criado) {
                    System.out.println("Diretório 'results' criado com sucesso.");
                } else {
                    System.out.println("Não foi possível criar o diretório 'results'.");
                }
            }
        }
        
        private String criarPastaSimulacao() throws Exception {
            criarDiretorioResultados();
            
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            
            String simulationFolder = "results/simulacao_" + timestamp;
            File simulationDir = new File(simulationFolder);
            
            if (!simulationDir.exists()) {
                boolean criado = simulationDir.mkdir();
                if (!criado) {
                    throw new Exception("Não foi possível criar a pasta para esta simulação: " + simulationFolder);
                }
            }
            
            return simulationFolder;
        }
    }
}