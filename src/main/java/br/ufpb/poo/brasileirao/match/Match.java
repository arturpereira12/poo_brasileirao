package br.ufpb.poo.brasileirao.match;

import br.ufpb.poo.brasileirao.model.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que representa uma partida de futebol.
 * Versão unificada com suporte a ambos os usos no projeto.
 */
public class Match {
    private Team homeTeam;
    private Team awayTeam;
    private int homeScore;
    private int awayScore;
    private LocalDate date;
    private LocalDateTime dateTime;
    private int round;
    private boolean played;
    private Map<String, List<String>> goalScorers; // time -> lista de jogadores que marcaram gols

    /**
     * Cria uma nova partida usando LocalDate.
     * 
     * @param homeTeam o time da casa
     * @param awayTeam o time visitante
     * @param date a data da partida
     * @param round a rodada à qual a partida pertence
     */
    public Match(Team homeTeam, Team awayTeam, LocalDate date, int round) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.dateTime = date != null ? date.atStartOfDay() : null;
        this.round = round;
        this.homeScore = 0;
        this.awayScore = 0;
        this.played = false;
        this.goalScorers = new HashMap<>();
        this.goalScorers.put(homeTeam.getName(), new ArrayList<>());
        this.goalScorers.put(awayTeam.getName(), new ArrayList<>());
    }

    /**
     * Cria uma nova partida usando LocalDateTime (para compatibilidade com o código existente).
     * 
     * @param homeTeam o time da casa
     * @param awayTeam o time visitante
     * @param dateTime a data e hora da partida
     */
    public Match(Team homeTeam, Team awayTeam, LocalDateTime dateTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.dateTime = dateTime;
        this.date = dateTime != null ? dateTime.toLocalDate() : null;
        this.round = 0; // Rodada não especificada
        this.homeScore = 0;
        this.awayScore = 0;
        this.played = false;
        this.goalScorers = new HashMap<>();
        this.goalScorers.put(homeTeam.getName(), new ArrayList<>());
        this.goalScorers.put(awayTeam.getName(), new ArrayList<>());
    }

    /**
     * Define o resultado da partida.
     * 
     * @param homeScore gols do time da casa
     * @param awayScore gols do time visitante
     */
    public void setResult(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.played = true;
    }

    /**
     * Adiciona um gol para um jogador de um time.
     * 
     * @param teamName o nome do time
     * @param playerName o nome do jogador
     */
    public void addGoal(String teamName, String playerName) {
        if (goalScorers.containsKey(teamName)) {
            goalScorers.get(teamName).add(playerName);
        }
    }

    /**
     * Obtém o time da casa.
     * 
     * @return o time da casa
     */
    public Team getHomeTeam() {
        return homeTeam;
    }

    /**
     * Define o time da casa.
     * 
     * @param homeTeam o time da casa
     */
    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    /**
     * Obtém o time visitante.
     * 
     * @return o time visitante
     */
    public Team getAwayTeam() {
        return awayTeam;
    }

    /**
     * Define o time visitante.
     * 
     * @param awayTeam o time visitante
     */
    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    /**
     * Obtém os gols do time da casa.
     * 
     * @return os gols do time da casa
     */
    public int getHomeScore() {
        return homeScore;
    }

    /**
     * Define os gols do time da casa.
     * 
     * @param homeScore os gols do time da casa
     */
    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    /**
     * Obtém os gols do time visitante.
     * 
     * @return os gols do time visitante
     */
    public int getAwayScore() {
        return awayScore;
    }

    /**
     * Define os gols do time visitante.
     * 
     * @param awayScore os gols do time visitante
     */
    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    /**
     * Obtém a data da partida.
     * 
     * @return a data da partida
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Define a data da partida.
     * 
     * @param date a data da partida
     */
    public void setDate(LocalDate date) {
        this.date = date;
        this.dateTime = date != null ? date.atStartOfDay() : null;
    }

    /**
     * Obtém a data e hora da partida.
     * 
     * @return a data e hora da partida
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Define a data e hora da partida.
     * 
     * @param dateTime a data e hora da partida
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.date = dateTime != null ? dateTime.toLocalDate() : null;
    }

    /**
     * Para compatibilidade com o código existente que usa LocalDateTime.
     */
    public LocalDateTime getDate(boolean asDateTime) {
        return this.dateTime;
    }

    /**
     * Para compatibilidade com o código existente que usa LocalDateTime.
     */
    public void setDate(LocalDateTime dateTime) {
        setDateTime(dateTime);
    }

    /**
     * Obtém a data da partida formatada como string (dd/MM/yyyy).
     * 
     * @return a data formatada como string
     */
    public String getFormattedDate() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    /**
     * Obtém a rodada à qual a partida pertence.
     * 
     * @return a rodada
     */
    public int getRound() {
        return round;
    }

    /**
     * Define a rodada à qual a partida pertence.
     * 
     * @param round a rodada
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * Verifica se a partida já foi jogada.
     * 
     * @return true se a partida já foi jogada, false caso contrário
     */
    public boolean isPlayed() {
        return played;
    }

    /**
     * Define se a partida já foi jogada.
     * 
     * @param played true se a partida já foi jogada, false caso contrário
     */
    public void setPlayed(boolean played) {
        this.played = played;
    }

    /**
     * Obtém os artilheiros de um time nesta partida.
     * 
     * @param teamName o nome do time
     * @return lista de jogadores que marcaram gols
     */
    public List<String> getGoalScorers(String teamName) {
        return goalScorers.getOrDefault(teamName, new ArrayList<>());
    }

    /**
     * Obtém o nome do time da casa.
     * 
     * @return o nome do time da casa
     */
    public String getHomeTeamName() {
        return homeTeam.getName();
    }

    /**
     * Obtém o nome do time visitante.
     * 
     * @return o nome do time visitante
     */
    public String getAwayTeamName() {
        return awayTeam.getName();
    }

    /**
     * Retorna uma representação em string da partida.
     * 
     * @return a string que representa a partida
     */
    @Override
    public String toString() {
        String result = homeTeam.getName() + " vs " + awayTeam.getName() + " - " + getFormattedDate();
        if (played) {
            result += " - " + homeScore + "x" + awayScore;
        }
        return result;
    }
} 