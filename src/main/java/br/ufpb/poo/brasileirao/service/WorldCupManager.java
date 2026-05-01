package br.ufpb.poo.brasileirao.service;

import br.ufpb.poo.brasileirao.match.KnockoutRound;
import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Player;
import br.ufpb.poo.brasileirao.model.Position;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.tournament.GroupStandings;
import br.ufpb.poo.brasileirao.tournament.TopScorersTable;
import br.ufpb.poo.brasileirao.tournament.TournamentPhase;
import br.ufpb.poo.brasileirao.tournament.WorldCupGroup;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorldCupManager {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    @Getter private List<Team> allTeams = new ArrayList<>();
    @Getter private List<WorldCupGroup> groups = new ArrayList<>();
    @Getter private TopScorersTable topScorers = new TopScorersTable();

    // Knockout brackets (null/empty until group stage is complete)
    @Getter private List<Match> r32Matches = new ArrayList<>();
    @Getter private List<Match> r16Matches = new ArrayList<>();
    @Getter private List<Match> quarterFinals = new ArrayList<>();
    @Getter private List<Match> semiFinals = new ArrayList<>();
    @Getter private Match thirdPlaceMatch;
    @Getter private Match finalMatch;

    // State
    @Getter private TournamentPhase phase = TournamentPhase.NOT_STARTED;
    @Getter private int currentGroupMatchDay = 0;
    @Getter private boolean active = false;
    @Getter private Team champion = null;
    @Getter private String runnerUp = null;

    private final Random random = new Random();

    // Groups A-L in order
    private static final String[] GROUP_LETTERS =
            {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    /**
     * Bootstraps the tournament with exactly 48 teams.
     * Teams are assigned to groups based on their {@code group} field.
     */
    public void initialize(List<Team> teams) {
        if (teams == null || teams.size() != 48) {
            throw new IllegalArgumentException("Exactly 48 teams are required to start the World Cup.");
        }

        // Store a copy sorted by group letter then FIFA ranking
        allTeams = teams.stream()
                .sorted(Comparator.comparing(Team::getGroup)
                        .thenComparingInt(Team::getFifaRanking))
                .collect(Collectors.toList());

        // Reset state
        groups.clear();
        r32Matches.clear();
        r16Matches.clear();
        quarterFinals.clear();
        semiFinals.clear();
        thirdPlaceMatch = null;
        finalMatch = null;
        topScorers = new TopScorersTable();
        champion = null;
        runnerUp = null;
        phase = TournamentPhase.GROUP_STAGE;
        currentGroupMatchDay = 1;
        active = true;

        // Create 12 groups
        for (String letter : GROUP_LETTERS) {
            groups.add(new WorldCupGroup(letter));
        }

        // Assign each team to its group
        for (Team team : allTeams) {
            String groupLetter = team.getGroup() == null ? "A" : team.getGroup().toUpperCase();
            WorldCupGroup group = findGroup(groupLetter);
            if (group != null) {
                group.addTeam(team);
            }
        }

        generateGroupMatches();
    }

    private WorldCupGroup findGroup(String letter) {
        return groups.stream()
                .filter(g -> g.getLetter().equals(letter))
                .findFirst()
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // Group Stage Match Generation
    // -------------------------------------------------------------------------

    /**
     * Generates 6 group-stage matches per group (2 per match day, 3 match days).
     * Schedule:
     *   Day 1 – t0 vs t1, t2 vs t3
     *   Day 2 – t0 vs t2, t1 vs t3
     *   Day 3 – t0 vs t3, t1 vs t2
     */
    void generateGroupMatches() {
        LocalDate baseDate = LocalDate.of(2026, 6, 11);

        for (WorldCupGroup group : groups) {
            List<Team> t = group.getTeams();
            if (t.size() < 4) continue;

            Team t0 = t.get(0);
            Team t1 = t.get(1);
            Team t2 = t.get(2);
            Team t3 = t.get(3);

            // Match day 1 — base + 0 days
            Match m1 = buildGroupMatch(t0, t1, baseDate, group.getLetter(), 1);
            Match m2 = buildGroupMatch(t2, t3, baseDate, group.getLetter(), 1);

            // Match day 2 — base + 3 days
            LocalDate day2 = baseDate.plusDays(3);
            Match m3 = buildGroupMatch(t0, t2, day2, group.getLetter(), 2);
            Match m4 = buildGroupMatch(t1, t3, day2, group.getLetter(), 2);

            // Match day 3 — base + 6 days
            LocalDate day3 = baseDate.plusDays(6);
            Match m5 = buildGroupMatch(t0, t3, day3, group.getLetter(), 3);
            Match m6 = buildGroupMatch(t1, t2, day3, group.getLetter(), 3);

            group.addMatch(m1);
            group.addMatch(m2);
            group.addMatch(m3);
            group.addMatch(m4);
            group.addMatch(m5);
            group.addMatch(m6);
        }
    }

    private Match buildGroupMatch(Team home, Team away, LocalDate date, String groupLetter, int round) {
        Match m = new Match(home, away, date, round);
        m.setGroupName(groupLetter);
        m.setKnockout(false);
        return m;
    }

    // -------------------------------------------------------------------------
    // Group Stage Simulation
    // -------------------------------------------------------------------------

    /**
     * Simulates all matches for the current group match day across all 12 groups.
     *
     * @return {@code true} if matches were simulated; {@code false} if group stage is already done.
     */
    public synchronized boolean simulateCurrentGroupMatchDay() {
        if (phase != TournamentPhase.GROUP_STAGE || currentGroupMatchDay > 3) {
            return false;
        }

        int day = currentGroupMatchDay;
        for (WorldCupGroup group : groups) {
            List<Match> dayMatches = group.getMatchesForDay(day);
            for (Match match : dayMatches) {
                if (!match.isPlayed()) {
                    simulateMatch(match);
                    group.getStandings().registerMatch(match);
                    // topScorers already updated inside simulateMatch via assignGoalScorers
                }
            }
        }

        currentGroupMatchDay++;

        if (currentGroupMatchDay > 3) {
            calculateQualifiers();
            generateR32Bracket();
            phase = TournamentPhase.ROUND_OF_32;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Qualifier Calculation
    // -------------------------------------------------------------------------

    // Accessible for tests / controllers
    List<Team> qualified3rd = new ArrayList<>();

    /**
     * Determines which 8 of the 12 third-place teams advance.
     * Sort criteria: points DESC → GD DESC → GS DESC → FIFA ranking ASC.
     */
    void calculateQualifiers() {
        List<ThirdPlaceEntry> thirds = new ArrayList<>();

        for (WorldCupGroup group : groups) {
            Team third = group.getThirdPlace();
            GroupStandings.TeamGroupStats stats = group.getThirdPlaceStats();
            if (third != null && stats != null) {
                thirds.add(new ThirdPlaceEntry(third, stats, group.getLetter()));
            }
        }

        thirds.sort(Comparator
                .comparingInt(ThirdPlaceEntry::getPoints).reversed()
                .thenComparingInt(ThirdPlaceEntry::getGoalDifference).reversed()
                .thenComparingInt(ThirdPlaceEntry::getGoalsFor).reversed()
                .thenComparingInt(e -> e.team.getFifaRanking()));

        qualified3rd = thirds.stream()
                .limit(8)
                .map(e -> e.team)
                .collect(Collectors.toList());
    }

    private static class ThirdPlaceEntry {
        final Team team;
        final GroupStandings.TeamGroupStats stats;
        @SuppressWarnings("unused")
        final String groupLetter;

        ThirdPlaceEntry(Team team, GroupStandings.TeamGroupStats stats, String groupLetter) {
            this.team = team;
            this.stats = stats;
            this.groupLetter = groupLetter;
        }

        int getPoints() { return stats.getPoints(); }
        int getGoalDifference() { return stats.getGoalDifference(); }
        int getGoalsFor() { return stats.getGoalsFor(); }
    }

    // -------------------------------------------------------------------------
    // R32 Bracket Generation
    // -------------------------------------------------------------------------

    /**
     * Generates the official WC 2026 Round of 32 bracket (matches 73-88).
     */
    void generateR32Bracket() {
        r32Matches.clear();

        // Build winner / runner-up lookup maps
        Map<String, Team> w = new HashMap<>();
        Map<String, Team> r = new HashMap<>();
        for (WorldCupGroup group : groups) {
            String letter = group.getLetter();
            w.put(letter, group.getFirstPlace());
            r.put(letter, group.getSecondPlace());
        }

        // Qualified 3rd-place teams (sorted best-to-worst by calculateQualifiers)
        List<Team> t3 = qualified3rd;

        // Bracket (match numbers 73-88)
        r32Matches.add(buildKnockoutMatch(r.get("A"),  r.get("B"),  73));
        r32Matches.add(buildKnockoutMatch(w.get("E"),  safeGet(t3, 0), 74));
        r32Matches.add(buildKnockoutMatch(w.get("F"),  r.get("C"),  75));
        r32Matches.add(buildKnockoutMatch(w.get("C"),  r.get("F"),  76));
        r32Matches.add(buildKnockoutMatch(w.get("I"),  safeGet(t3, 1), 77));
        r32Matches.add(buildKnockoutMatch(r.get("E"),  r.get("I"),  78));
        r32Matches.add(buildKnockoutMatch(w.get("A"),  safeGet(t3, 2), 79));
        r32Matches.add(buildKnockoutMatch(w.get("L"),  safeGet(t3, 3), 80));
        r32Matches.add(buildKnockoutMatch(w.get("D"),  safeGet(t3, 4), 81));
        r32Matches.add(buildKnockoutMatch(w.get("G"),  safeGet(t3, 5), 82));
        r32Matches.add(buildKnockoutMatch(r.get("K"),  r.get("L"),  83));
        r32Matches.add(buildKnockoutMatch(w.get("H"),  r.get("J"),  84));
        r32Matches.add(buildKnockoutMatch(w.get("B"),  safeGet(t3, 6), 85));
        r32Matches.add(buildKnockoutMatch(w.get("J"),  r.get("H"),  86));
        r32Matches.add(buildKnockoutMatch(w.get("K"),  safeGet(t3, 7), 87));
        r32Matches.add(buildKnockoutMatch(r.get("D"),  r.get("G"),  88));
    }

    private Match buildKnockoutMatch(Team home, Team away, int matchNumber) {
        // Fallback for null teams (safety guard)
        if (home == null || away == null) {
            throw new IllegalStateException(
                    "Cannot build knockout match #" + matchNumber + ": one or both teams are null.");
        }
        Match m = new Match(home, away, (LocalDate) null, 0);
        m.setKnockout(true);
        m.setKnockoutRound(KnockoutRound.ROUND_OF_32);
        m.setMatchNumber(matchNumber);
        return m;
    }

    private Team safeGet(List<Team> list, int index) {
        return (list != null && index < list.size()) ? list.get(index) : null;
    }

    // -------------------------------------------------------------------------
    // Knockout Stage Simulation
    // -------------------------------------------------------------------------

    /**
     * Simulates all matches in the current knockout round and advances the phase.
     *
     * @return {@code true} if matches were simulated.
     */
    public synchronized boolean simulateCurrentKnockoutRound() {
        return switch (phase) {
            case ROUND_OF_32 -> simulateRound(r32Matches, () -> {
                generateR16Bracket();
                phase = TournamentPhase.ROUND_OF_16;
            });
            case ROUND_OF_16 -> simulateRound(r16Matches, () -> {
                generateQuarterFinalBracket();
                phase = TournamentPhase.QUARTERFINAL;
            });
            case QUARTERFINAL -> simulateRound(quarterFinals, () -> {
                generateSemiFinalBracket();
                phase = TournamentPhase.SEMIFINAL;
            });
            case SEMIFINAL -> simulateRound(semiFinals, () -> {
                generateThirdAndFinal();
                phase = TournamentPhase.FINISHED;
            });
            case FINISHED -> {
                // Simulate 3rd-place and final if not yet played
                boolean simulated = false;
                if (thirdPlaceMatch != null && !thirdPlaceMatch.isPlayed()) {
                    simulateKnockoutMatch(thirdPlaceMatch);
                    simulated = true;
                }
                if (finalMatch != null && !finalMatch.isPlayed()) {
                    simulateKnockoutMatch(finalMatch);
                    champion = finalMatch.getWinner();
                    runnerUp = finalMatch.getLoser() != null ? finalMatch.getLoser().getName() : null;
                    simulated = true;
                }
                yield simulated;
            }
            default -> false;
        };
    }

    private boolean simulateRound(List<Match> matches, Runnable onComplete) {
        if (matches.isEmpty()) return false;
        for (Match m : matches) {
            if (!m.isPlayed()) {
                simulateKnockoutMatch(m);
            }
        }
        onComplete.run();
        return true;
    }

    // ---- Bracket builders for each knockout round ----

    private void generateR16Bracket() {
        r16Matches.clear();
        // R32 winners: matches are in pairs that feed R16
        // Pair order: (M73 winner vs M74 winner), (M75 winner vs M76 winner), ...
        List<Team> winners = r32Matches.stream()
                .map(Match::getWinner)
                .collect(Collectors.toList());

        int matchNum = 89;
        for (int i = 0; i < winners.size(); i += 2) {
            Match m = buildKnockoutMatchR(winners.get(i), winners.get(i + 1),
                    matchNum++, KnockoutRound.ROUND_OF_16);
            r16Matches.add(m);
        }
    }

    private void generateQuarterFinalBracket() {
        quarterFinals.clear();
        List<Team> winners = r16Matches.stream()
                .map(Match::getWinner)
                .collect(Collectors.toList());

        int matchNum = 97;
        for (int i = 0; i < winners.size(); i += 2) {
            Match m = buildKnockoutMatchR(winners.get(i), winners.get(i + 1),
                    matchNum++, KnockoutRound.QUARTERFINAL);
            quarterFinals.add(m);
        }
    }

    private void generateSemiFinalBracket() {
        semiFinals.clear();
        List<Team> winners = quarterFinals.stream()
                .map(Match::getWinner)
                .collect(Collectors.toList());

        int matchNum = 101;
        for (int i = 0; i < winners.size(); i += 2) {
            Match m = buildKnockoutMatchR(winners.get(i), winners.get(i + 1),
                    matchNum++, KnockoutRound.SEMIFINAL);
            semiFinals.add(m);
        }
    }

    private void generateThirdAndFinal() {
        // Losers of semi-finals play for 3rd place
        Team sf1Loser = semiFinals.get(0).getLoser();
        Team sf2Loser = semiFinals.get(1).getLoser();
        thirdPlaceMatch = buildKnockoutMatchR(sf1Loser, sf2Loser, 103, KnockoutRound.THIRD_PLACE);

        // Winners of semi-finals play the final
        Team sf1Winner = semiFinals.get(0).getWinner();
        Team sf2Winner = semiFinals.get(1).getWinner();
        finalMatch = buildKnockoutMatchR(sf1Winner, sf2Winner, 104, KnockoutRound.FINAL);
    }

    private Match buildKnockoutMatchR(Team home, Team away, int matchNumber, KnockoutRound round) {
        if (home == null || away == null) {
            throw new IllegalStateException(
                    "Cannot build " + round + " match #" + matchNumber + ": one or both teams are null.");
        }
        Match m = new Match(home, away, (LocalDate) null, 0);
        m.setKnockout(true);
        m.setKnockoutRound(round);
        m.setMatchNumber(matchNumber);
        return m;
    }

    // -------------------------------------------------------------------------
    // Match Simulation — Group Stage
    // -------------------------------------------------------------------------

    /**
     * Simulates a group-stage match (draws allowed).
     */
    void simulateMatch(Match match) {
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();

        // Small neutral-venue bonus for the "home" team in fixture list
        int homeStrength = homeTeam.getStrength() + 5;
        int awayStrength = awayTeam.getStrength();

        double homeLuck = 0.85 + random.nextDouble() * 0.30;
        double awayLuck = 0.85 + random.nextDouble() * 0.30;

        double finalHome = homeStrength * homeLuck;
        double finalAway = awayStrength * awayLuck;

        double homeGoalChance = finalHome / 380.0;
        double awayGoalChance = finalAway / 380.0;

        int homeGoals = 0;
        int awayGoals = 0;

        for (int i = 0; i < 7; i++) {
            if (random.nextDouble() < homeGoalChance) homeGoals++;
            if (random.nextDouble() < awayGoalChance) awayGoals++;
        }

        homeGoals = Math.min(homeGoals, 5);
        awayGoals = Math.min(awayGoals, 5);

        match.setResult(homeGoals, awayGoals);

        assignGoalScorers(match, homeTeam, homeGoals);
        assignGoalScorers(match, awayTeam, awayGoals);
    }

    // -------------------------------------------------------------------------
    // Match Simulation — Knockout
    // -------------------------------------------------------------------------

    /**
     * Simulates a knockout match. Draws are resolved via extra time, then penalties.
     */
    void simulateKnockoutMatch(Match match) {
        // --- 90-minute simulation (re-use same logic) ---
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();

        int homeStrength = homeTeam.getStrength() + 5;
        int awayStrength = awayTeam.getStrength();

        double homeLuck = 0.85 + random.nextDouble() * 0.30;
        double awayLuck = 0.85 + random.nextDouble() * 0.30;

        double finalHome = homeStrength * homeLuck;
        double finalAway = awayStrength * awayLuck;

        double homeGoalChance = finalHome / 380.0;
        double awayGoalChance = finalAway / 380.0;

        int homeGoals = 0;
        int awayGoals = 0;

        for (int i = 0; i < 7; i++) {
            if (random.nextDouble() < homeGoalChance) homeGoals++;
            if (random.nextDouble() < awayGoalChance) awayGoals++;
        }

        homeGoals = Math.min(homeGoals, 5);
        awayGoals = Math.min(awayGoals, 5);

        // --- Extra time if tied ---
        if (homeGoals == awayGoals) {
            match.setWentToExtraTime(true);

            double homeLuck2 = 0.85 + random.nextDouble() * 0.30;
            double awayLuck2 = 0.85 + random.nextDouble() * 0.30;

            double finalHome2 = homeStrength * homeLuck2;
            double finalAway2 = awayStrength * awayLuck2;

            double homeChance2 = finalHome2 / 380.0;
            double awayChance2 = finalAway2 / 380.0;

            for (int i = 0; i < 2; i++) {
                if (random.nextDouble() < homeChance2) homeGoals++;
                if (random.nextDouble() < awayChance2) awayGoals++;
            }

            // --- Penalty shootout if still tied ---
            if (homeGoals == awayGoals) {
                match.setWentToPenalties(true);

                final double conversionRate = 0.70;

                int hp = 0;
                int ap = 0;

                // 5 kicks each
                for (int i = 0; i < 5; i++) {
                    if (random.nextDouble() < conversionRate) hp++;
                    if (random.nextDouble() < conversionRate) ap++;
                }

                // Sudden death until someone wins
                int maxSuddenDeath = 20;
                int sd = 0;
                while (hp == ap && sd < maxSuddenDeath) {
                    boolean homeScores = random.nextDouble() < conversionRate;
                    boolean awayScores = random.nextDouble() < conversionRate;
                    if (homeScores && !awayScores) hp++;
                    else if (!homeScores && awayScores) ap++;
                    // both score or both miss → continue
                    sd++;
                }

                // Ultimate fallback: give home team the win
                if (hp == ap) hp++;

                match.setHomePenalties(hp);
                match.setAwayPenalties(ap);
            }
        }

        match.setResult(homeGoals, awayGoals);

        assignGoalScorers(match, homeTeam, homeGoals);
        assignGoalScorers(match, awayTeam, awayGoals);
    }

    // -------------------------------------------------------------------------
    // Goal Scorer Simulation
    // -------------------------------------------------------------------------

    private void assignGoalScorers(Match match, Team team, int goals) {
        List<Player> forwards    = getPlayersByPosition(team, Position.FORWARD);
        List<Player> midfielders = getPlayersByPosition(team, Position.MIDFIELDER);
        List<Player> defenders   = getPlayersByPosition(team, Position.DEFENDER);
        List<Player> goalkeepers = getPlayersByPosition(team, Position.GOALKEEPER);

        for (int i = 0; i < goals; i++) {
            double r = random.nextDouble();
            Player scorer = null;

            if (r < 0.60 && !forwards.isEmpty()) {
                scorer = selectPlayerWeightedByStrength(forwards);
            } else if (r < 0.85 && !midfielders.isEmpty()) {
                scorer = selectPlayerWeightedByStrength(midfielders);
            } else if (r < 0.97 && !defenders.isEmpty()) {
                scorer = selectPlayerWeightedByStrength(defenders);
            } else if (!goalkeepers.isEmpty()) {
                scorer = selectPlayerWeightedByStrength(goalkeepers);
            }

            // Fallback: any player on the team
            if (scorer == null) {
                scorer = selectPlayerWeightedByStrength(team.getPlayers());
            }

            if (scorer != null) {
                match.addGoal(team.getName(), scorer.getName());
                topScorers.addGoal(scorer.getName(), team.getName());
            }
        }
    }

    private Player selectPlayerWeightedByStrength(List<Player> players) {
        if (players == null || players.isEmpty()) return null;
        int total = players.stream().mapToInt(Player::getStrength).sum();
        if (total <= 0) return players.get(random.nextInt(players.size()));
        int rand = random.nextInt(total);
        int cumulative = 0;
        for (Player p : players) {
            cumulative += p.getStrength();
            if (rand < cumulative) return p;
        }
        return players.get(players.size() - 1);
    }

    private List<Player> getPlayersByPosition(Team team, Position pos) {
        return team.getPlayers().stream()
                .filter(p -> p.getPosition() == pos)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Match Accessors
    // -------------------------------------------------------------------------

    /** All group-stage matches across all 12 groups, in group/day order. */
    public List<Match> getAllGroupMatches() {
        return groups.stream()
                .flatMap(g -> g.getMatches().stream())
                .collect(Collectors.toList());
    }

    /** All group-stage matches for a specific match day (1–3) across all 12 groups. */
    public List<Match> getGroupMatchesForDay(int day) {
        return groups.stream()
                .flatMap(g -> g.getMatchesForDay(day).stream())
                .collect(Collectors.toList());
    }

    /** All knockout matches in chronological bracket order. */
    public List<Match> getAllKnockoutMatches() {
        List<Match> all = new ArrayList<>();
        all.addAll(r32Matches);
        all.addAll(r16Matches);
        all.addAll(quarterFinals);
        all.addAll(semiFinals);
        if (thirdPlaceMatch != null) all.add(thirdPlaceMatch);
        if (finalMatch != null) all.add(finalMatch);
        return all;
    }

    // -------------------------------------------------------------------------
    // Tournament Status
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} only when the final match has been played.
     */
    public boolean isTournamentCompleted() {
        return phase == TournamentPhase.FINISHED
                && finalMatch != null
                && finalMatch.isPlayed();
    }

    /** Summary statistics for display on dashboards / REST endpoints. */
    public Map<String, Object> getTournamentStats() {
        List<Match> groupMatches = getAllGroupMatches();
        long simulated = groupMatches.stream().filter(Match::isPlayed).count();
        int totalGoals = groupMatches.stream()
                .filter(Match::isPlayed)
                .mapToInt(m -> m.getHomeScore() + m.getAwayScore())
                .sum();

        // Also count knockout goals
        int knockoutGoals = getAllKnockoutMatches().stream()
                .filter(Match::isPlayed)
                .mapToInt(m -> m.getHomeScore() + m.getAwayScore())
                .sum();
        int grandTotalGoals = totalGoals + knockoutGoals;
        long grandTotalSimulated = simulated + getAllKnockoutMatches().stream().filter(Match::isPlayed).count();

        double averageGoals = grandTotalSimulated > 0
                ? Math.round((double) grandTotalGoals / grandTotalSimulated * 100.0) / 100.0
                : 0.0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalGroupMatches", groupMatches.size());
        stats.put("simulatedGroupMatches", simulated);
        stats.put("totalGoals", grandTotalGoals);
        stats.put("averageGoals", averageGoals);
        stats.put("phase", phase.getDisplayName());
        stats.put("currentGroupMatchDay", currentGroupMatchDay);
        stats.put("champion", champion != null ? champion.getName() : null);
        return stats;
    }
}
