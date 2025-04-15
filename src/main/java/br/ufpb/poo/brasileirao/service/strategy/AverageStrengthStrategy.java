package br.ufpb.poo.brasileirao.service.strategy;

import java.util.List;

import br.ufpb.poo.brasileirao.model.Player;
import br.ufpb.poo.brasileirao.model.Position;

/**
 * Calculates strength based on the simple average of players in each position.
 */
public class AverageStrengthStrategy implements StrengthCalculationStrategy {

    @Override
    public int calculateAttackStrength(List<Player> players) {
        return calculateAverageByPosition(players, Position.FORWARD);
    }

    @Override
    public int calculateDefenseStrength(List<Player> players) {
        double avgDefenders = calculateDoubleAverageByPosition(players, Position.DEFENDER);
        double avgGoalkeeper = calculateDoubleAverageByPosition(players, Position.GOALKEEPER);

        long defenderCount = 0;
        long goalkeeperCount = 0;
        if (players != null) {
            for (Player p : players) {
                if (p.getPosition() == Position.DEFENDER) {
                    defenderCount++;
                } else if (p.getPosition() == Position.GOALKEEPER) {
                    goalkeeperCount++;
                }
            }
        }


        if (defenderCount == 0 && goalkeeperCount == 0) return 0;
        if (defenderCount == 0) return (int) Math.round(avgGoalkeeper);
        if (goalkeeperCount == 0) return (int) Math.round(avgDefenders);
        return (int) Math.round((avgDefenders + avgGoalkeeper) / 2.0); // Avg of averages
    }

    @Override
    public int calculateMidfieldStrength(List<Player> players) {
        return calculateAverageByPosition(players, Position.MIDFIELDER);
    }

    private int calculateAverageByPosition(List<Player> players, Position position) {
        return (int) Math.round(calculateDoubleAverageByPosition(players, position));
    }

    /**
     * Helper method to calculate the average strength of players in a specific position.
     */
    private double calculateDoubleAverageByPosition(List<Player> players, Position position) {
        if (players == null || players.isEmpty()) {
            return 0.0;
        }

        double totalStrength = 0;
        int count = 0;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i); 
            if (player != null && player.getPosition() == position) {
                totalStrength += player.getStrength(); 
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        } else {
            return totalStrength / count;
        }
    }
}