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
    private String countryCode;
    private String confederation;
    private String group;
    private String flagEmoji;
    private int fifaRanking;

    public Team(String name) {
        Objects.requireNonNull(name, "Team name cannot be null");
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (!this.players.contains(player)) {
            this.players.add(player);
        }
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public int getMaxPlayerStrength() {
        return maxPlayerStrength;
    }

    public void setMaxPlayerStrength(int maxPlayerStrength) {
        this.maxPlayerStrength = maxPlayerStrength;
    }

    @Override
    public String toString() {
        return flagEmoji != null
            ? flagEmoji + " " + name + " (Grupo " + group + ")"
            : name;
    }
}
