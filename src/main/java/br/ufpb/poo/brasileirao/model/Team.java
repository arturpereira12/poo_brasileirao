package br.ufpb.poo.brasileirao.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Team {
    private String name;
    private Integer strength;
    private List<Player> players = new ArrayList<>();
    private Integer attackStrength;
    private Integer defenseStrength;
    private Integer midfieldStrength;
    private int maxPlayerStrength;

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

    @Override
    public String toString() {
        return "Team: " + name + " (Players: " + players.size() +
               ", ATK:" + attackStrength + ", MID:" + midfieldStrength + ", DEF:" + defenseStrength + ")";
    }

    public int getMaxPlayerStrength() {
        return maxPlayerStrength;
    }

    public void setMaxPlayerStrength(int maxPlayerStrength) {
        this.maxPlayerStrength = maxPlayerStrength;
    }
}