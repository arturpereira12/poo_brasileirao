package br.ufpb.poo.brasileirao.exception;

public class InvalidStrengthException extends IllegalArgumentException {
    public InvalidStrengthException(String message) {
        super(message);
    }
}