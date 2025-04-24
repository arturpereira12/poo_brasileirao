package br.ufpb.poo.brasileirao.model;

import java.util.Objects;

import br.ufpb.poo.brasileirao.exception.InvalidStrengthException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Player { 
    private String name;      
    private Integer strength;     
    private Position position; 

    public Player(String name, int strength, Position position) { 
        Objects.requireNonNull(name, "Player name cannot be null");
        Objects.requireNonNull(position, "Player position cannot be null");
        this.name = name;
        setStrength(strength); 
        this.position = position;
    }

    public void setStrength(int strength) {
        if (strength < 0 || strength > 100) {
            throw new InvalidStrengthException("Player '" + this.name + "' strength must be between 0 and 100. Received value: " + strength);
        }
        this.strength = strength;
    }

    @Override
    public String toString() {
        return name + " (" + position + ", Strength: " + strength + ")";
    }


}