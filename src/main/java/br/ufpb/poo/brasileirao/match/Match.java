package br.ufpb.poo.brasileirao.match;

import br.ufpb.poo.brasileirao.model.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class Match {
    @Getter @Setter private Team homeTeam;
    @Getter @Setter private Team awayTeam;
    @Getter @Setter private int homeScore;
    @Getter @Setter private int awayScore;
    @Getter private LocalDate date;
    @Getter private LocalDateTime dateTime;
    @Getter @Setter private int round;
    @Getter @Setter private boolean played;
    private Map<String, List<String>> goalScorers; // time -> lista de jogadores que marcaram gols

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

    public void setResult(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.played = true;
    }

    public void addGoal(String teamName, String playerName) {
        if (goalScorers.containsKey(teamName)) {
            goalScorers.get(teamName).add(playerName);
        }
    }

    public void setDate(LocalDate date) {
        this.date = date;
        this.dateTime = date != null ? date.atStartOfDay() : null;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.date = dateTime != null ? dateTime.toLocalDate() : null;
    }

    public LocalDateTime getDate(boolean asDateTime) {
        return this.dateTime;
    }

    public void setDate(LocalDateTime dateTime) {
        setDateTime(dateTime);
    }

    public String getFormattedDate() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public List<String> getGoalScorers(String teamName) {
        return goalScorers.getOrDefault(teamName, new ArrayList<>());
    }

    public String getHomeTeamName() {
        return homeTeam.getName();
    }

    public String getAwayTeamName() {
        return awayTeam.getName();
    }

    @Override
    public String toString() {
        String result = homeTeam.getName() + " vs " + awayTeam.getName() + " - " + getFormattedDate();
        if (played) {
            result += " - " + homeScore + "x" + awayScore;
        }
        return result;
    }
}