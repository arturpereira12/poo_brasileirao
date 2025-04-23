package br.ufpb.poo.brasileirao.model;

/**
 * Interface que define o contrato para cálculo de probabilidade de gol por posição.
 */
public interface GoalProbabilityCalculator {
    
    /**
     * Calcula a probabilidade de um jogador desta posição marcar um gol.
     *
     * @return um valor entre 0.0 e 1.0 representando a probabilidade de marcar um gol
     */
    double calculateGoalProbability();
    
    /**
     * Verifica se um valor aleatório resulta em um gol para esta posição.
     *
     * @param randomValue um valor aleatório entre 0.0 e 1.0
     * @return true se o jogador desta posição deve marcar o gol, false caso contrário
     */
    boolean shouldScore(double randomValue);
}
