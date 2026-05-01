package br.ufpb.poo.brasileirao.tournament;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class WorldCupGroup {
    private final String letter;
    private final List<Team> teams;
    private final List<Match> matches;
    private final GroupStandings standings;

    public WorldCupGroup(String letter) {
        this.letter = letter;
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.standings = new GroupStandings();
    }

    public void addTeam(Team team) {
        teams.add(team);
        standings.addTeam(team.getName(), team.getFifaRanking(), team.getFlagEmoji());
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public List<Match> getMatchesForDay(int day) {
        int start = (day - 1) * 2;
        int end = Math.min(start + 2, matches.size());
        return matches.subList(start, end);
    }

    /** Returns teams sorted by group standings. */
    public List<GroupStandings.TeamGroupStats> getSortedStandings() {
        return standings.getStandings();
    }

    public Team getFirstPlace() {
        var st = standings.getStandings();
        return st.isEmpty() ? null : getTeamByName(st.get(0).getTeamName());
    }

    public Team getSecondPlace() {
        var st = standings.getStandings();
        return st.size() < 2 ? null : getTeamByName(st.get(1).getTeamName());
    }

    public Team getThirdPlace() {
        var st = standings.getStandings();
        return st.size() < 3 ? null : getTeamByName(st.get(2).getTeamName());
    }

    public GroupStandings.TeamGroupStats getThirdPlaceStats() {
        var st = standings.getStandings();
        return st.size() < 3 ? null : st.get(2);
    }

    private Team getTeamByName(String name) {
        return teams.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);
    }

    public boolean isComplete() {
        return matches.stream().allMatch(Match::isPlayed);
    }
}
