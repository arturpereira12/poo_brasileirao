package br.ufpb.poo.brasileirao.model;

public class GoalProbabilityCalculatorFactory {
    
    // Instâncias únicas para cada tipo de calculador (padrão Singleton)
    private static final GoalProbabilityCalculator FORWARD_CALCULATOR = new Forward("", 0);
    private static final GoalProbabilityCalculator MIDFIELDER_CALCULATOR = new Midfielder("", 0);
    private static final GoalProbabilityCalculator DEFENDER_CALCULATOR = new Defender("", 0);
    private static final GoalProbabilityCalculator GOALKEEPER_CALCULATOR = new Goalkeeper("", 0);

    public static GoalProbabilityCalculator getCalculator(Position position) {
        if (position == null) {
            // Padrão é atacante, que tem mais chance de marcar
            return FORWARD_CALCULATOR;
        }
        
        return switch (position) {
            case FORWARD -> FORWARD_CALCULATOR;
            case MIDFIELDER -> MIDFIELDER_CALCULATOR;
            case DEFENDER -> DEFENDER_CALCULATOR;
            case GOALKEEPER -> GOALKEEPER_CALCULATOR;
        };
    }
    
    public static Position getPositionForGoal(double randomValue) {
        if (FORWARD_CALCULATOR.shouldScore(randomValue)) {
            return Position.FORWARD;
        } else if (MIDFIELDER_CALCULATOR.shouldScore(randomValue)) {
            return Position.MIDFIELDER;
        } else if (DEFENDER_CALCULATOR.shouldScore(randomValue)) {
            return Position.DEFENDER;
        } else if (GOALKEEPER_CALCULATOR.shouldScore(randomValue)) {
            return Position.GOALKEEPER;
        } else {
            return Position.FORWARD;
        }
    }
}