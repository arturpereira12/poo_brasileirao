package br.ufpb.poo.brasileirao.tournament;

import br.ufpb.poo.brasileirao.match.Match;

import java.util.*;

public class GroupStandings {

    private final Map<String, TeamGroupStats> statsMap = new LinkedHashMap<>();
    private final Map<String, Integer> fifaRankingMap = new HashMap<>();
    private final List<Match> groupMatches = new ArrayList<>();

    public void addTeam(String name, int fifaRanking, String flagEmoji) {
        statsMap.put(name, new TeamGroupStats(name, flagEmoji));
        fifaRankingMap.put(name, fifaRanking);
    }

    public void registerMatch(Match match) {
        groupMatches.add(match);
        if (!match.isPlayed()) return;

        String home = match.getHomeTeamName();
        String away = match.getAwayTeamName();
        int hs = match.getHomeScore();
        int as = match.getAwayScore();

        if (hs > as) {
            statsMap.get(home).addWin(hs, as);
            statsMap.get(away).addLoss(hs, as);
        } else if (as > hs) {
            statsMap.get(away).addWin(as, hs);
            statsMap.get(home).addLoss(as, hs);
        } else {
            statsMap.get(home).addDraw(hs, as);
            statsMap.get(away).addDraw(as, hs);
        }
    }

    public List<TeamGroupStats> getStandings() {
        List<TeamGroupStats> list = new ArrayList<>(statsMap.values());
        list.sort(comparatorWith(list));
        return list;
    }

    private Comparator<TeamGroupStats> comparatorWith(List<TeamGroupStats> all) {
        return (a, b) -> {
            // 1. Overall points
            int cmp = Integer.compare(b.getPoints(), a.getPoints());
            if (cmp != 0) return cmp;

            // 2. Overall GD
            cmp = Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            if (cmp != 0) return cmp;

            // 3. Overall GS
            cmp = Integer.compare(b.getGoalsFor(), a.getGoalsFor());
            if (cmp != 0) return cmp;

            // 4-6. Head-to-head (as fallback for ties in points, GD and GS)
            int h2hPts = compareH2H(a, b, "points");
            if (h2hPts != 0) return h2hPts;

            int h2hGD = compareH2H(a, b, "gd");
            if (h2hGD != 0) return h2hGD;

            int h2hGS = compareH2H(a, b, "gs");
            if (h2hGS != 0) return h2hGS;

            // 7. FIFA ranking (lower = better)
            int rankA = fifaRankingMap.getOrDefault(a.getTeamName(), 999);
            int rankB = fifaRankingMap.getOrDefault(b.getTeamName(), 999);
            return Integer.compare(rankA, rankB);
        };
    }

    private int compareH2H(TeamGroupStats a, TeamGroupStats b, String criterion) {
        Match h2h = groupMatches.stream()
            .filter(m -> m.isPlayed()
                && ((m.getHomeTeamName().equals(a.getTeamName()) && m.getAwayTeamName().equals(b.getTeamName()))
                    || (m.getHomeTeamName().equals(b.getTeamName()) && m.getAwayTeamName().equals(a.getTeamName()))))
            .findFirst().orElse(null);

        if (h2h == null) return 0;

        boolean aIsHome = h2h.getHomeTeamName().equals(a.getTeamName());
        int aGoals = aIsHome ? h2h.getHomeScore() : h2h.getAwayScore();
        int bGoals = aIsHome ? h2h.getAwayScore() : h2h.getHomeScore();

        return switch (criterion) {
            case "points" -> {
                int aPts = aGoals > bGoals ? 3 : aGoals == bGoals ? 1 : 0;
                int bPts = bGoals > aGoals ? 3 : bGoals == aGoals ? 1 : 0;
                yield Integer.compare(bPts, aPts);
            }
            case "gd" -> Integer.compare(bGoals - aGoals, aGoals - bGoals);
            case "gs" -> Integer.compare(bGoals, aGoals);
            default -> 0;
        };
    }

    public static class TeamGroupStats {
        private final String teamName;
        private final String flagEmoji;
        private int played, wins, draws, losses, goalsFor, goalsAgainst, points;

        public TeamGroupStats(String name, String flagEmoji) { 
            this.teamName = name; 
            this.flagEmoji = flagEmoji;
        }

        public void addWin(int gf, int ga) { played++; wins++; goalsFor += gf; goalsAgainst += ga; points += 3; }
        public void addDraw(int gf, int ga) { played++; draws++; goalsFor += gf; goalsAgainst += ga; points++; }
        public void addLoss(int gf, int ga) { played++; losses++; goalsFor += gf; goalsAgainst += ga; }

        public String getTeamName() { return teamName; }
        public String getFlagEmoji() { return flagEmoji; }
        public int getPlayed() { return played; }
        public int getWins() { return wins; }
        public int getDraws() { return draws; }
        public int getLosses() { return losses; }
        public int getGoalsFor() { return goalsFor; }
        public int getGoalsAgainst() { return goalsAgainst; }
        public int getGoalDifference() { return goalsFor - goalsAgainst; }
        public int getPoints() { return points; }
    }
}
