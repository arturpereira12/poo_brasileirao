package br.ufpb.poo.brasileirao.model;

public class Forward extends Player implements GoalProbabilityCalculator { 
    // Probabilidade base para atacantes (75%)
    private static final double BASE_GOAL_PROBABILITY = 0.75;
    
    public Forward(String name, int strength) {
        super(name, strength, Position.FORWARD); 
    }
    
    @Override
    public double calculateGoalProbability() {
        // Atacantes têm maior probabilidade de marcar gols (75%)
        return BASE_GOAL_PROBABILITY;
    }
    
    @Override
    public boolean shouldScore(double randomValue) {
        return randomValue < BASE_GOAL_PROBABILITY;
    }
}