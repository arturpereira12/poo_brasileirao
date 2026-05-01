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

    // WC fields
    @Getter @Setter private String groupName;
    @Getter @Setter private boolean knockout;
    @Getter @Setter private KnockoutRound knockoutRound;
    @Getter @Setter private boolean wentToExtraTime;
    @Getter @Setter private boolean wentToPenalties;
    @Getter @Setter private int homePenalties;
    @Getter @Setter private int awayPenalties;
    @Getter @Setter private String venue;
    @Getter @Setter private int matchNumber;

    private Map<String, List<String>> goalScorers;

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
        this(homeTeam, awayTeam, dateTime != null ? dateTime.toLocalDate() : null, 0);
        this.dateTime = dateTime;
    }

    public void setResult(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.played = true;
    }

    public void addGoal(String teamName, String playerName) {
        goalScorers.computeIfAbsent(teamName, k -> new ArrayList<>()).add(playerName);
    }

    public void setDate(LocalDate date) {
        this.date = date;
        this.dateTime = date != null ? date.atStartOfDay() : null;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.date = dateTime != null ? dateTime.toLocalDate() : null;
    }

    public String getFormattedDate() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public List<String> getGoalScorers(String teamName) {
        return goalScorers.getOrDefault(teamName, new ArrayList<>());
    }

    public String getHomeTeamName() { return homeTeam.getName(); }
    public String getAwayTeamName() { return awayTeam.getName(); }

    public Team getWinner() {
        if (!played) return null;
        if (wentToPenalties) {
            return homePenalties > awayPenalties ? homeTeam : awayTeam;
        }
        if (homeScore > awayScore) return homeTeam;
        if (awayScore > homeScore) return awayTeam;
        return null;
    }

    public Team getLoser() {
        if (!played) return null;
        Team winner = getWinner();
        if (winner == null) return null;
        return winner == homeTeam ? awayTeam : homeTeam;
    }

    public String getScoreDisplay() {
        if (!played) return "vs";
        String score = homeScore + " - " + awayScore;
        if (wentToPenalties) {
            score += " (pen: " + homePenalties + "-" + awayPenalties + ")";
        } else if (wentToExtraTime) {
            score += " (prorr.)";
        }
        return score;
    }

    @Override
    public String toString() {
        String result = homeTeam.getName() + " vs " + awayTeam.getName();
        if (played) result += " " + getScoreDisplay();
        return result;
    }
}
