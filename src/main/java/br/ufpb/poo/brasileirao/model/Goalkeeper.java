package br.ufpb.poo.brasileirao.model;

public class Goalkeeper extends Player implements GoalProbabilityCalculator { 
    // Probabilidade base para goleiros (praticamente zero)
    private static final double BASE_GOAL_PROBABILITY = 0.0001;
    
    public Goalkeeper(String name, int strength) {
        super(name, strength, Position.GOALKEEPER); 
    }
    
    @Override
    public double calculateGoalProbability() {
        // Goleiros têm probabilidade quase nula de marcar gols (0.01%)
        return BASE_GOAL_PROBABILITY;
    }
    
    @Override
    public boolean shouldScore(double randomValue) {
        // Goleiro só marca gol em situações extremamente raras
        return randomValue >= 0.9999;
    }
}