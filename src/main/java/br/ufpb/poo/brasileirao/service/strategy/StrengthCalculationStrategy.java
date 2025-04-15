package br.ufpb.poo.brasileirao.service.strategy;

import java.util.List;

import br.ufpb.poo.brasileirao.model.Player;

/**
 * Interface for different team strength calculation algorithms
 */
public interface StrengthCalculationStrategy {
    int calculateAttackStrength(List<Player> players);
    int calculateDefenseStrength(List<Player> players);
    int calculateMidfieldStrength(List<Player> players);
}