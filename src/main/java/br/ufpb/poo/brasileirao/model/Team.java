package br.ufpb.poo.brasileirao.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 Represents a football team
 */
@Data
@NoArgsConstructor
public class Team {
    private String name;
    private Integer strength;
    private List<Player> players = new ArrayList<>();
    private Integer attackStrength;
    private Integer defenseStrength;
    private Integer midfieldStrength;

    public Team(String name) {
        Objects.requireNonNull(name, "Team name cannot be null");
        this.name = name;
        this.players = new ArrayList<>();
        // Strengths initialized to 0
    }

    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (!this.players.contains(player)) {
            this.players.add(player);
        }
        // Strengths need recalculation after modification
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        // Strengths need recalculation if players are removed
    }


    /**
     * Calculates and updates team strengths using the provided strategy.
     * Must be called before using strengths in simulations.
     * @param strategy The algorithm to use for calculation.
     *
    public void calculateStrengths(StrengthCalculationStrategy strategy) {
        Objects.requireNonNull(strategy, "Calculation strategy cannot be null");
        this.attackStrength = strategy.calculateAttackStrength(this.players);
        this.defenseStrength = strategy.calculateDefenseStrength(this.players);
        this.midfieldStrength = strategy.calculateMidfieldStrength(this.players);
    }*/

    @Override
    public String toString() {
        return "Team: " + name + " (Players: " + players.size() +
               ", ATK:" + attackStrength + ", MID:" + midfieldStrength + ", DEF:" + defenseStrength + ")";
    }

   
}