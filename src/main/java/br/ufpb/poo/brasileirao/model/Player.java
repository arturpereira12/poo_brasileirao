package br.ufpb.poo.brasileirao.model;

import java.util.Objects;

import br.ufpb.poo.brasileirao.exception.InvalidStrengthException;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
Abstract class representing a generic football player with all the necessary attributes and methods
 */
@Data
@NoArgsConstructor
public class Player { 
    private String name;      
    private Integer strength;     
    private Position position; 

    /**
      Constructor to create a player
      @param name The player's name
      @param strength The player's strength (must be between 0 and 100)
      @param position The player's main position
      @throws InvalidStrengthException if strength is outside the 0-100 range
     */
    public Player(String name, int strength, Position position) { 
        Objects.requireNonNull(name, "Player name cannot be null");
        Objects.requireNonNull(position, "Player position cannot be null");
        this.name = name;
        setStrength(strength); 
        this.position = position;
    }

    /**
    Sets the player's strength, considering we might change the player's strength
    @param strength 
    @throws InvalidStrengthException 
     */
    public void setStrength(int strength) {
        if (strength < 0 || strength > 100) {
            throw new InvalidStrengthException("Player '" + this.name + "' strength must be between 0 and 100. Received value: " + strength);
        }
        this.strength = strength;
    }

    /**
    Use it just by printing ("Player name") Ex: System.out.println(player1);
     */
    @Override
    public String toString() {
        return name + " (" + position + ", Strength: " + strength + ")";
    }


}