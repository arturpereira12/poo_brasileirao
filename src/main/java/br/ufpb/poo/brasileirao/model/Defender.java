package br.ufpb.poo.brasileirao.model;

public class Defender extends Player implements GoalProbabilityCalculator { 
    // Probabilidade base para defensores (5%)
    private static final double BASE_GOAL_PROBABILITY = 0.05;
    
    public Defender(String name, int strength) {
        super(name, strength, Position.DEFENDER); 
    }
    
    @Override
    public double calculateGoalProbability() {
        // Defensores têm baixa probabilidade de marcar gols (5%)
        return BASE_GOAL_PROBABILITY;
    }
    
    @Override
    public boolean shouldScore(double randomValue) {
        return randomValue >= 0.95;
    }
}