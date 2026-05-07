import type {
  KnockoutRound,
  Match,
  Player,
  Scorer,
  Team,
  TeamGroupStats,
  TournamentPhase,
  TournamentState,
  WorldCupGroup
} from "@/lib/types/tournament";

const GROUP_LETTERS = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"];
const PHASE_LABELS: Record<TournamentPhase, string> = {
  NOT_STARTED: "Não iniciada",
  GROUP_STAGE: "Fase de grupos",
  ROUND_OF_32: "16 avos de final",
  ROUND_OF_16: "Oitavas de final",
  QUARTERFINAL: "Quartas de final",
  SEMIFINAL: "Semifinais",
  FINISHED: "Finais"
};

export function phaseLabel(phase: TournamentPhase): string {
  return PHASE_LABELS[phase];
}

export function createEmptyTournamentState(): TournamentState {
  return {
    allTeams: [],
    groups: [],
    topScorers: [],
    r32Matches: [],
    r16Matches: [],
    quarterFinals: [],
    semiFinals: [],
    thirdPlaceMatch: null,
    finalMatch: null,
    phase: "NOT_STARTED",
    currentGroupMatchDay: 0,
    active: false,
    champion: null,
    runnerUp: null,
    qualified3rd: []
  };
}

export function initializeTournament(teams: Team[]): TournamentState {
  if (teams.length !== 48) {
    throw new Error("Exactly 48 teams are required to start the World Cup.");
  }

  const allTeams = [...teams].sort((a, b) => a.group.localeCompare(b.group) || a.fifaRanking - b.fifaRanking);
  const groups = GROUP_LETTERS.map<WorldCupGroup>((letter) => ({
    letter,
    teams: allTeams.filter((team) => team.group.toUpperCase() === letter),
    matches: [],
    standings: []
  }));

  for (const group of groups) {
    group.matches = generateGroupMatches(group);
    group.standings = computeStandings(group.teams, []);
  }

  return {
    ...createEmptyTournamentState(),
    allTeams,
    groups,
    phase: "GROUP_STAGE",
    currentGroupMatchDay: 1,
    active: true
  };
}

export function simulateCurrentGroupMatchDay(state: TournamentState): TournamentState {
  if (state.phase !== "GROUP_STAGE" || state.currentGroupMatchDay > 3) return state;

  const day = state.currentGroupMatchDay;
  const scorerMap = topScorersToMap(state.topScorers);
  const groups = state.groups.map((group) => {
    const matches = group.matches.map((match) => {
      if (match.round !== day || match.played) return match;
      return simulateMatch(match, scorerMap, false);
    });

    return {
      ...group,
      matches,
      standings: computeStandings(group.teams, matches.filter((match) => match.played))
    };
  });

  let nextState: TournamentState = {
    ...state,
    groups,
    topScorers: scorerMapToTopScorers(scorerMap),
    currentGroupMatchDay: day + 1
  };

  if (nextState.currentGroupMatchDay > 3) {
    const qualified3rd = calculateQualifiedThirds(groups);
    nextState = {
      ...nextState,
      qualified3rd,
      r32Matches: generateR32Bracket(groups, qualified3rd),
      phase: "ROUND_OF_32"
    };
  }

  return nextState;
}

export function simulateAllGroupMatchDays(state: TournamentState): TournamentState {
  let next = state;
  while (next.phase === "GROUP_STAGE") {
    const updated = simulateCurrentGroupMatchDay(next);
    if (updated === next) break;
    next = updated;
  }
  return next;
}

export function simulateCurrentKnockoutRound(state: TournamentState): TournamentState {
  const scorerMap = topScorersToMap(state.topScorers);

  if (state.phase === "ROUND_OF_32") {
    const r32Matches = simulateRound(state.r32Matches, scorerMap);
    return { ...state, r32Matches, r16Matches: generateNextRound(r32Matches, 89, "ROUND_OF_16"), phase: "ROUND_OF_16", topScorers: scorerMapToTopScorers(scorerMap) };
  }

  if (state.phase === "ROUND_OF_16") {
    const r16Matches = simulateRound(state.r16Matches, scorerMap);
    return { ...state, r16Matches, quarterFinals: generateNextRound(r16Matches, 97, "QUARTERFINAL"), phase: "QUARTERFINAL", topScorers: scorerMapToTopScorers(scorerMap) };
  }

  if (state.phase === "QUARTERFINAL") {
    const quarterFinals = simulateRound(state.quarterFinals, scorerMap);
    return { ...state, quarterFinals, semiFinals: generateNextRound(quarterFinals, 101, "SEMIFINAL"), phase: "SEMIFINAL", topScorers: scorerMapToTopScorers(scorerMap) };
  }

  if (state.phase === "SEMIFINAL") {
    const semiFinals = simulateRound(state.semiFinals, scorerMap);
    const [thirdPlaceMatch, finalMatch] = generateThirdAndFinal(semiFinals);
    return { ...state, semiFinals, thirdPlaceMatch, finalMatch, phase: "FINISHED", topScorers: scorerMapToTopScorers(scorerMap) };
  }

  if (state.phase === "FINISHED") {
    const thirdPlaceMatch = state.thirdPlaceMatch?.played ? state.thirdPlaceMatch : state.thirdPlaceMatch ? simulateMatch(state.thirdPlaceMatch, scorerMap, true) : null;
    const finalMatch = state.finalMatch?.played ? state.finalMatch : state.finalMatch ? simulateMatch(state.finalMatch, scorerMap, true) : null;
    return {
      ...state,
      thirdPlaceMatch,
      finalMatch,
      champion: finalMatch?.played ? getWinner(finalMatch) : state.champion,
      runnerUp: finalMatch?.played ? getLoser(finalMatch)?.name ?? null : state.runnerUp,
      topScorers: scorerMapToTopScorers(scorerMap)
    };
  }

  return state;
}

export function getAllGroupMatches(state: TournamentState): Match[] {
  return state.groups.flatMap((group) => group.matches);
}

export function getGroupMatchesForDay(state: TournamentState, day: number): Match[] {
  return getAllGroupMatches(state).filter((match) => match.round === day);
}

export function getAllKnockoutMatches(state: TournamentState): Match[] {
  return [
    ...state.r32Matches,
    ...state.r16Matches,
    ...state.quarterFinals,
    ...state.semiFinals,
    ...(state.thirdPlaceMatch ? [state.thirdPlaceMatch] : []),
    ...(state.finalMatch ? [state.finalMatch] : [])
  ];
}

export function isTournamentCompleted(state: TournamentState): boolean {
  return state.phase === "FINISHED" && Boolean(state.finalMatch?.played);
}

export type TournamentStats = {
  totalGroupMatches: number;
  simulatedGroupMatches: number;
  totalGoals: number;
  totalMatches: number;
  averageGoals: number;
  phase: string;
  currentGroupMatchDay: number;
  champion: string | null;
};

export function getTournamentStats(state: TournamentState): TournamentStats {
  const playedGroupMatches = getAllGroupMatches(state).filter((match) => match.played);
  const playedKnockoutMatches = getAllKnockoutMatches(state).filter((match) => match.played);
  const playedMatches = [...playedGroupMatches, ...playedKnockoutMatches];
  const totalGoals = playedMatches.reduce((sum, match) => sum + match.homeScore + match.awayScore, 0);

  return {
    totalGroupMatches: getAllGroupMatches(state).length,
    simulatedGroupMatches: playedGroupMatches.length,
    totalGoals,
    totalMatches: playedMatches.length,
    averageGoals: playedMatches.length ? Math.round((totalGoals / playedMatches.length) * 100) / 100 : 0,
    phase: phaseLabel(state.phase),
    currentGroupMatchDay: state.currentGroupMatchDay,
    champion: state.champion?.name ?? null
  };
}

export function scoreDisplay(match: Match): string {
  if (!match.played) return "vs";
  const score = `${match.homeScore} - ${match.awayScore}`;
  if (match.wentToPenalties) return `${score} (pen: ${match.homePenalties}-${match.awayPenalties})`;
  if (match.wentToExtraTime) return `${score} (prorr.)`;
  return score;
}

export function getWinner(match: Match): Team | null {
  if (!match.played) return null;
  if (match.wentToPenalties) return match.homePenalties > match.awayPenalties ? match.homeTeam : match.awayTeam;
  if (match.homeScore > match.awayScore) return match.homeTeam;
  if (match.awayScore > match.homeScore) return match.awayTeam;
  return null;
}

export function getLoser(match: Match): Team | null {
  const winner = getWinner(match);
  if (!winner) return null;
  return winner.name === match.homeTeam.name ? match.awayTeam : match.homeTeam;
}

function generateGroupMatches(group: WorldCupGroup): Match[] {
  const [t0, t1, t2, t3] = group.teams;
  if (!t0 || !t1 || !t2 || !t3) return [];
  const base = new Date("2026-06-11T00:00:00");

  return [
    buildGroupMatch(t0, t1, base, group.letter, 1, 1),
    buildGroupMatch(t2, t3, base, group.letter, 1, 2),
    buildGroupMatch(t0, t2, addDays(base, 3), group.letter, 2, 3),
    buildGroupMatch(t1, t3, addDays(base, 3), group.letter, 2, 4),
    buildGroupMatch(t0, t3, addDays(base, 6), group.letter, 3, 5),
    buildGroupMatch(t1, t2, addDays(base, 6), group.letter, 3, 6)
  ];
}

function buildGroupMatch(homeTeam: Team, awayTeam: Team, date: Date, groupName: string, round: number, slot: number): Match {
  return baseMatch(`${groupName}-${round}-${slot}`, homeTeam, awayTeam, date.toISOString().slice(0, 10), round, {
    groupName,
    knockout: false
  });
}

function buildKnockoutMatch(homeTeam: Team, awayTeam: Team, matchNumber: number, knockoutRound: KnockoutRound): Match {
  return baseMatch(`M${matchNumber}`, homeTeam, awayTeam, null, 0, {
    knockout: true,
    knockoutRound,
    matchNumber
  });
}

function baseMatch(
  id: string,
  homeTeam: Team,
  awayTeam: Team,
  date: string | null,
  round: number,
  partial: Partial<Match>
): Match {
  return {
    id,
    homeTeam,
    awayTeam,
    homeScore: 0,
    awayScore: 0,
    date,
    round,
    played: false,
    knockout: false,
    wentToExtraTime: false,
    wentToPenalties: false,
    homePenalties: 0,
    awayPenalties: 0,
    goalScorers: {
      [homeTeam.name]: [],
      [awayTeam.name]: []
    },
    ...partial
  };
}

function computeStandings(teams: Team[], matches: Match[]): TeamGroupStats[] {
  const stats = new Map<string, TeamGroupStats>();
  const rank = new Map<string, number>();

  for (const team of teams) {
    stats.set(team.name, {
      teamName: team.name,
      countryCode: team.countryCode,
      flagEmoji: team.flagEmoji,
      played: 0,
      wins: 0,
      draws: 0,
      losses: 0,
      goalsFor: 0,
      goalsAgainst: 0,
      goalDifference: 0,
      points: 0
    });
    rank.set(team.name, team.fifaRanking);
  }

  for (const match of matches) {
    const home = stats.get(match.homeTeam.name);
    const away = stats.get(match.awayTeam.name);
    if (!home || !away || !match.played) continue;

    if (match.homeScore > match.awayScore) {
      addResult(home, match.homeScore, match.awayScore, "win");
      addResult(away, match.awayScore, match.homeScore, "loss");
    } else if (match.awayScore > match.homeScore) {
      addResult(away, match.awayScore, match.homeScore, "win");
      addResult(home, match.homeScore, match.awayScore, "loss");
    } else {
      addResult(home, match.homeScore, match.awayScore, "draw");
      addResult(away, match.awayScore, match.homeScore, "draw");
    }
  }

  return [...stats.values()].sort((a, b) => {
    return (
      b.points - a.points ||
      b.goalDifference - a.goalDifference ||
      b.goalsFor - a.goalsFor ||
      compareHeadToHead(a, b, matches, "points") ||
      compareHeadToHead(a, b, matches, "gd") ||
      compareHeadToHead(a, b, matches, "gs") ||
      (rank.get(a.teamName) ?? 999) - (rank.get(b.teamName) ?? 999)
    );
  });
}

function addResult(stats: TeamGroupStats, goalsFor: number, goalsAgainst: number, result: "win" | "draw" | "loss") {
  stats.played += 1;
  stats.goalsFor += goalsFor;
  stats.goalsAgainst += goalsAgainst;
  stats.goalDifference = stats.goalsFor - stats.goalsAgainst;
  if (result === "win") {
    stats.wins += 1;
    stats.points += 3;
  } else if (result === "draw") {
    stats.draws += 1;
    stats.points += 1;
  } else {
    stats.losses += 1;
  }
}

function compareHeadToHead(a: TeamGroupStats, b: TeamGroupStats, matches: Match[], criterion: "points" | "gd" | "gs"): number {
  const h2h = matches.find(
    (match) =>
      match.played &&
      ((match.homeTeam.name === a.teamName && match.awayTeam.name === b.teamName) ||
        (match.homeTeam.name === b.teamName && match.awayTeam.name === a.teamName))
  );
  if (!h2h) return 0;

  const aHome = h2h.homeTeam.name === a.teamName;
  const aGoals = aHome ? h2h.homeScore : h2h.awayScore;
  const bGoals = aHome ? h2h.awayScore : h2h.homeScore;
  if (criterion === "points") {
    const aPts = aGoals > bGoals ? 3 : aGoals === bGoals ? 1 : 0;
    const bPts = bGoals > aGoals ? 3 : bGoals === aGoals ? 1 : 0;
    return bPts - aPts;
  }
  if (criterion === "gd") return bGoals - aGoals - (aGoals - bGoals);
  return bGoals - aGoals;
}

export function calculateQualifiedThirds(groups: WorldCupGroup[]): Team[] {
  return groups
    .map((group) => {
      const stats = group.standings[2];
      const team = stats ? group.teams.find((item) => item.name === stats.teamName) : undefined;
      return team && stats ? { team, stats } : null;
    })
    .filter((entry): entry is { team: Team; stats: TeamGroupStats } => Boolean(entry))
    .sort(
      (a, b) =>
        b.stats.points - a.stats.points ||
        b.stats.goalDifference - a.stats.goalDifference ||
        b.stats.goalsFor - a.stats.goalsFor ||
        a.team.fifaRanking - b.team.fifaRanking
    )
    .slice(0, 8)
    .map((entry) => entry.team);
}

function generateR32Bracket(groups: WorldCupGroup[], qualified3rd: Team[]): Match[] {
  const winners = new Map<string, Team>();
  const runners = new Map<string, Team>();
  for (const group of groups) {
    const first = group.teams.find((team) => team.name === group.standings[0]?.teamName);
    const second = group.teams.find((team) => team.name === group.standings[1]?.teamName);
    if (first) winners.set(group.letter, first);
    if (second) runners.set(group.letter, second);
  }

  const team = (map: Map<string, Team>, key: string) => {
    const value = map.get(key);
    if (!value) throw new Error(`Missing team for group ${key}`);
    return value;
  };
  const third = (index: number) => {
    const value = qualified3rd[index];
    if (!value) throw new Error(`Missing third-place qualifier ${index + 1}`);
    return value;
  };

  return [
    buildKnockoutMatch(team(runners, "A"), team(runners, "B"), 73, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "E"), third(0), 74, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "F"), team(runners, "C"), 75, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "C"), team(runners, "F"), 76, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "I"), third(1), 77, "ROUND_OF_32"),
    buildKnockoutMatch(team(runners, "E"), team(runners, "I"), 78, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "A"), third(2), 79, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "L"), third(3), 80, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "D"), third(4), 81, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "G"), third(5), 82, "ROUND_OF_32"),
    buildKnockoutMatch(team(runners, "K"), team(runners, "L"), 83, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "H"), team(runners, "J"), 84, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "B"), third(6), 85, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "J"), team(runners, "H"), 86, "ROUND_OF_32"),
    buildKnockoutMatch(team(winners, "K"), third(7), 87, "ROUND_OF_32"),
    buildKnockoutMatch(team(runners, "D"), team(runners, "G"), 88, "ROUND_OF_32")
  ];
}

function generateNextRound(previous: Match[], firstMatchNumber: number, round: KnockoutRound): Match[] {
  const winners = previous.map(getWinner);
  const matches: Match[] = [];
  for (let index = 0; index < winners.length; index += 2) {
    const home = winners[index];
    const away = winners[index + 1];
    if (!home || !away) continue;
    matches.push(buildKnockoutMatch(home, away, firstMatchNumber + index / 2, round));
  }
  return matches;
}

function generateThirdAndFinal(semiFinals: Match[]): [Match | null, Match | null] {
  const firstLoser = getLoser(semiFinals[0]);
  const secondLoser = getLoser(semiFinals[1]);
  const firstWinner = getWinner(semiFinals[0]);
  const secondWinner = getWinner(semiFinals[1]);
  return [
    firstLoser && secondLoser ? buildKnockoutMatch(firstLoser, secondLoser, 103, "THIRD_PLACE") : null,
    firstWinner && secondWinner ? buildKnockoutMatch(firstWinner, secondWinner, 104, "FINAL") : null
  ];
}

function simulateRound(matches: Match[], scorerMap: Map<string, Scorer>): Match[] {
  return matches.map((match) => (match.played ? match : simulateMatch(match, scorerMap, true)));
}

function simulateMatch(match: Match, scorerMap: Map<string, Scorer>, knockout: boolean): Match {
  const homeStrength = match.homeTeam.strength + 5;
  const awayStrength = match.awayTeam.strength;
  const homeGoalChance = (homeStrength * randomLuck()) / 380;
  const awayGoalChance = (awayStrength * randomLuck()) / 380;

  let homeScore = countGoals(homeGoalChance, 7);
  let awayScore = countGoals(awayGoalChance, 7);
  let wentToExtraTime = false;
  let wentToPenalties = false;
  let homePenalties = 0;
  let awayPenalties = 0;

  if (knockout && homeScore === awayScore) {
    wentToExtraTime = true;
    homeScore += countGoals((homeStrength * randomLuck()) / 380, 2);
    awayScore += countGoals((awayStrength * randomLuck()) / 380, 2);

    if (homeScore === awayScore) {
      wentToPenalties = true;
      [homePenalties, awayPenalties] = simulatePenalties();
    }
  }

  const goalScorers = {
    [match.homeTeam.name]: assignGoalScorers(match.homeTeam, homeScore, scorerMap),
    [match.awayTeam.name]: assignGoalScorers(match.awayTeam, awayScore, scorerMap)
  };

  return {
    ...match,
    homeScore,
    awayScore,
    played: true,
    wentToExtraTime,
    wentToPenalties,
    homePenalties,
    awayPenalties,
    goalScorers
  };
}

function countGoals(chance: number, events: number): number {
  let goals = 0;
  for (let index = 0; index < events; index += 1) {
    if (Math.random() < chance) goals += 1;
  }
  return Math.min(goals, 5);
}

function randomLuck(): number {
  return 0.85 + Math.random() * 0.3;
}

export function simulatePenalties(): [number, number] {
  const conversionRate = 0.7;
  let home = 0;
  let away = 0;
  for (let index = 0; index < 5; index += 1) {
    if (Math.random() < conversionRate) home += 1;
    if (Math.random() < conversionRate) away += 1;
  }

  let suddenDeath = 0;
  while (home === away && suddenDeath < 20) {
    const homeScores = Math.random() < conversionRate;
    const awayScores = Math.random() < conversionRate;
    if (homeScores && !awayScores) home += 1;
    if (!homeScores && awayScores) away += 1;
    suddenDeath += 1;
  }
  if (home === away) home += 1;
  return [home, away];
}

function assignGoalScorers(team: Team, goals: number, scorerMap: Map<string, Scorer>): string[] {
  const scorers: string[] = [];
  for (let index = 0; index < goals; index += 1) {
    const player = selectScorer(team);
    if (!player) continue;
    scorers.push(player.name);
    const key = `${player.name}|${team.name}`;
    const current = scorerMap.get(key) ?? { playerName: player.name, teamName: team.name, goals: 0 };
    scorerMap.set(key, { ...current, goals: current.goals + 1 });
  }
  return scorers;
}

function selectScorer(team: Team): Player | null {
  const byPosition = (position: Player["position"]) => team.players.filter((player) => player.position === position);
  const r = Math.random();
  if (r < 0.6) return selectPlayerWeightedByStrength(byPosition("FORWARD")) ?? selectPlayerWeightedByStrength(team.players);
  if (r < 0.85) return selectPlayerWeightedByStrength(byPosition("MIDFIELDER")) ?? selectPlayerWeightedByStrength(team.players);
  if (r < 0.97) return selectPlayerWeightedByStrength(byPosition("DEFENDER")) ?? selectPlayerWeightedByStrength(team.players);
  return selectPlayerWeightedByStrength(byPosition("GOALKEEPER")) ?? selectPlayerWeightedByStrength(team.players);
}

function selectPlayerWeightedByStrength(players: Player[]): Player | null {
  if (!players.length) return null;
  const total = players.reduce((sum, player) => sum + player.strength, 0);
  if (total <= 0) return players[Math.floor(Math.random() * players.length)];
  let target = Math.floor(Math.random() * total);
  for (const player of players) {
    target -= player.strength;
    if (target < 0) return player;
  }
  return players[players.length - 1];
}

function topScorersToMap(topScorers: Scorer[]): Map<string, Scorer> {
  return new Map(topScorers.map((scorer) => [`${scorer.playerName}|${scorer.teamName}`, scorer]));
}

function scorerMapToTopScorers(scorerMap: Map<string, Scorer>): Scorer[] {
  return [...scorerMap.values()].sort((a, b) => b.goals - a.goals || a.playerName.localeCompare(b.playerName));
}

function addDays(date: Date, days: number): Date {
  const next = new Date(date);
  next.setDate(next.getDate() + days);
  return next;
}
