package br.ufpb.poo.brasileirao.exception;

public class IncompleteTeamException extends RuntimeException {
    public IncompleteTeamException(String message) {
        super(message);
    }
}