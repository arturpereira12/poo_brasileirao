package br.ufpb.poo.brasileirao;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.match.MatchSimulator;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.controladores.TeamController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.ArrayList;

@SpringBootApplication
public class Mainantiga {

    public static void main(String[] args) {
        SpringApplication.run(Mainantiga.class, args);
    }
    
    @Bean
    public CommandLineRunner simulateMatches() {
        return args -> {
            System.out.println("=== SIMULADOR DO CAMPEONATO BRASILEIRO ===");
            
            // Carregar times do JSON
            TeamController teamController = new TeamController();
            List<Team> teams = teamController.leiaDoArquivo();
            
            if (teams == null || teams.isEmpty()) {
                System.out.println("Nenhum time encontrado para simular!");
                return;
            }
            
            System.out.println("Times carregados: " + teams.size());
            
            // Simular algumas partidas de exemplo
            List<Match> matchResults = new ArrayList<>();
            
            // Simular 3 partidas aleatórias
            for (int i = 0; i < 3; i++) {
                // Selecionar dois times aleatórios diferentes
                int team1Index = (int) (Math.random() * teams.size());
                int team2Index = (int) (Math.random() * teams.size());
                
                // Garantir que não seja o mesmo time
                while (team1Index == team2Index) {
                    team2Index = (int) (Math.random() * teams.size());
                }
                
                Team team1 = teams.get(team1Index);
                Team team2 = teams.get(team2Index);
                
                // Criar e simular a partida
                Match match = new Match(team1, team2);
                MatchSimulator.simulateMatch(match);
                
                matchResults.add(match);
                System.out.println("Resultado: " + match.getScore());
                
                // Mostrar os artilheiros
                System.out.println("Gols: " + match.getGoalScorers());
                System.out.println("-----------------------------");
            }
            
            System.out.println("\n=== RESULTADOS DAS PARTIDAS SIMULADAS ===");
            for (int i = 0; i < matchResults.size(); i++) {
                Match match = matchResults.get(i);
                System.out.println("Partida " + (i+1) + ": " + match.getScore());
            }
        };
    }
}