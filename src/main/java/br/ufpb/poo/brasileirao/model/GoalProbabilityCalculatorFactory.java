package br.ufpb.poo.brasileirao.model;

/**
 * Factory para obter calculadores de probabilidade de gol com base na posição
 */
public class GoalProbabilityCalculatorFactory {
    
    // Instâncias únicas para cada tipo de calculador (padrão Singleton)
    private static final GoalProbabilityCalculator FORWARD_CALCULATOR = new Forward("", 0);
    private static final GoalProbabilityCalculator MIDFIELDER_CALCULATOR = new Midfielder("", 0);
    private static final GoalProbabilityCalculator DEFENDER_CALCULATOR = new Defender("", 0);
    private static final GoalProbabilityCalculator GOALKEEPER_CALCULATOR = new Goalkeeper("", 0);

    /**
     * Retorna o calculador de probabilidade apropriado para a posição
     *
     * @param position a posição do jogador
     * @return calculador de probabilidade específico para a posição
     */
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
    
    /**
     * Determina qual posição deve marcar um gol com base em probabilidades
     *
     * @param randomValue um valor aleatório entre 0.0 e 1.0
     * @return a posição que deve marcar o gol
     */
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
            // Fallback, situação não deve ocorrer se as probabilidades forem implementadas corretamente
            return Position.FORWARD;
        }
    }
}