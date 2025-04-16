package br.ufpb.poo.brasileirao.match;

import java.util.Random;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.model.Player;
import java.util.List;

public class MatchSimulator {
    private static final Random random = new Random();

    /**
     * Simula uma partida de futebol entre dois times
     * @param match O objeto Match que contém os times e a data da partida
     * @return O objeto Match atualizado com o resultado da simulação
     */
    public static Match simulateMatch(Match match) {
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        
       // Fatores que influenciam a simulação com multiplicadores de casa/visitante
        int homeAttackStrength = (int)((homeTeam.getAttackStrength() != null ? homeTeam.getAttackStrength() : 70) * 1.3);
        int homeDefenseStrength = (int)((homeTeam.getDefenseStrength() != null ? homeTeam.getDefenseStrength() : 70) * 1.3);
        int homeMidfieldStrength = (int)((homeTeam.getMidfieldStrength() != null ? homeTeam.getMidfieldStrength() : 70) * 1.3);

        int awayAttackStrength = (int)((awayTeam.getAttackStrength() != null ? awayTeam.getAttackStrength() : 70) * 0.95);
        int awayDefenseStrength = (int)((awayTeam.getDefenseStrength() != null ? awayTeam.getDefenseStrength() : 70) * 0.95);
        int awayMidfieldStrength = (int)((awayTeam.getMidfieldStrength() != null ? awayTeam.getMidfieldStrength() : 70) * 0.95);
        
        // Fator de vantagem para o time da casa
        int homeAdvantage = 10;
        
        // Cálculo de probabilidade de gols baseado nos atributos dos times
        // Time da casa tem mais chances de marcar devido ao fator de vantagem
        int homeGoalProbability = calculateGoalProbability(
                homeAttackStrength + homeAdvantage, 
                awayDefenseStrength,
                homeMidfieldStrength);
        
        int awayGoalProbability = calculateGoalProbability(
                awayAttackStrength, 
                homeDefenseStrength + homeAdvantage/2,
                awayMidfieldStrength);
        
        // Simular os gols
        int homeGoals = simulateGoals(homeGoalProbability);
        int awayGoals = simulateGoals(awayGoalProbability);
        
        // Registrar os gols no objeto Match
        List<Player> homePlayers = homeTeam.getPlayers();
        List<Player> awayPlayers = awayTeam.getPlayers();
        
        // Registrar gols do time da casa
        for (int i = 0; i < homeGoals; i++) {
            String scorer = selectGoalScorer(homePlayers);
            match.addHomeTeamGoal(scorer);
        }
        
        // Registrar gols do time visitante
        for (int i = 0; i < awayGoals; i++) {
            String scorer = selectGoalScorer(awayPlayers);
            match.addAwayTeamGoal(scorer);
        }
        
        return match;
    }
    
    private static int calculateGoalProbability(int attackStrength, int opponentDefense, int midfieldStrength) {
        // Fórmula considerando ataque vs. defesa com influência do meio campo
        double baseChance = (attackStrength * 0.6) - (opponentDefense * 0.4) + (midfieldStrength * 0.2);
        
        // Normaliza para um valor entre 30 e 90
        return Math.max(30, Math.min(90, (int)baseChance));
    }
    
    
    private static int simulateGoals(int goalProbability) {
        // Algoritmo de simulação de gols
        int goals = 0;
        
        // Chance de marcar pelo menos 1 gol
        if (random.nextInt(100) < goalProbability) {
            goals++;
            
            // Chance de marcar o segundo gol (mais difícil)
            if (random.nextInt(100) < goalProbability * 0.7) {
                goals++;
                
                // Chance de marcar o terceiro gol (ainda mais difícil)
                if (random.nextInt(100) < goalProbability * 0.5) {
                    goals++;
                    
                    // Pequena chance de uma goleada
                    while (random.nextInt(100) < goalProbability * 0.3 && goals < 7) {
                        goals++;
                    }
                }
            }
        }
        
        return goals;
    }
    
    /**
     * Seleciona qual jogador marcou um gol com base nas habilidades
     */
    private static String selectGoalScorer(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return "Jogador desconhecido";
        }
        
        // Pesos para cada posição (atacantes têm mais chances de marcar)
        int totalWeight = 0;
        int[] weights = new int[players.size()];
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int playerStrength = player.getStrength() != null ? player.getStrength() : 70;
            int positionFactor;
            
            // Atacantes têm mais chances de marcar
            if (player.getPosition() == null) {
                positionFactor = 1;
            } else {
                switch (player.getPosition().toString()) {
                    case "Forward": positionFactor = 3; break;
                    case "Midfielder": positionFactor = 2; break;
                    case "Defender": positionFactor = 1; break;
                    case "Goalkeeper": positionFactor = 1; break; // Antes era 0, agora é 1 para dar chance
                    default: positionFactor = 1;
                }
            }
            
            weights[i] = playerStrength * positionFactor;
            totalWeight += weights[i];
        }
        
        if (totalWeight == 0) {
            // Se não houver peso válido (não deveria acontecer), escolha aleatoriamente
            return players.get(random.nextInt(players.size())).getName();
        }
        
        // Seleção ponderada do marcador do gol
        int random_value = random.nextInt(totalWeight);
        int sum = 0;
        
        for (int i = 0; i < players.size(); i++) {
            sum += weights[i];
            if (random_value < sum) {
                return players.get(i).getName();
            }
        }
        
        // Contingência - não deveria chegar aqui
        return players.get(0).getName();
    }
}