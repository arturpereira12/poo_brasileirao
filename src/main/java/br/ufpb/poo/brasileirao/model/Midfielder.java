package br.ufpb.poo.brasileirao.model;

public class Midfielder extends Player implements GoalProbabilityCalculator { 
    // Probabilidade base para meio-campistas (20%)
    private static final double BASE_GOAL_PROBABILITY = 0.20;
    
    public Midfielder(String name, int strength) {
        super(name, strength, Position.MIDFIELDER); 
    }
    
    @Override
    public double calculateGoalProbability() {
        // Meio-campistas têm probabilidade média de marcar gols (20%)
        return BASE_GOAL_PROBABILITY;
    }
    
    @Override
    public boolean shouldScore(double randomValue) {
        return randomValue >= 0.75 && randomValue < 0.95;
    }
}