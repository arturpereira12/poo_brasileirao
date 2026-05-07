export type Position = "GOALKEEPER" | "DEFENDER" | "MIDFIELDER" | "FORWARD";

export type Player = {
  name: string;
  strength: number;
  position: Position;
};

export type Team = {
  name: string;
  strength: number;
  players: Player[];
  attackStrength: number;
  defenseStrength: number;
  midfieldStrength: number;
  maxPlayerStrength?: number;
  countryCode: string;
  confederation: string;
  group: string;
  flagEmoji: string;
  fifaRanking: number;
};

export type TournamentPhase =
  | "NOT_STARTED"
  | "GROUP_STAGE"
  | "ROUND_OF_32"
  | "ROUND_OF_16"
  | "QUARTERFINAL"
  | "SEMIFINAL"
  | "FINISHED";

export type KnockoutRound =
  | "ROUND_OF_32"
  | "ROUND_OF_16"
  | "QUARTERFINAL"
  | "SEMIFINAL"
  | "THIRD_PLACE"
  | "FINAL";

export type Match = {
  id: string;
  homeTeam: Team;
  awayTeam: Team;
  homeScore: number;
  awayScore: number;
  date: string | null;
  round: number;
  played: boolean;
  groupName?: string;
  knockout: boolean;
  knockoutRound?: KnockoutRound;
  wentToExtraTime: boolean;
  wentToPenalties: boolean;
  homePenalties: number;
  awayPenalties: number;
  venue?: string;
  matchNumber?: number;
  goalScorers: Record<string, string[]>;
};

export type TeamGroupStats = {
  teamName: string;
  countryCode?: string;
  flagEmoji: string;
  played: number;
  wins: number;
  draws: number;
  losses: number;
  goalsFor: number;
  goalsAgainst: number;
  goalDifference: number;
  points: number;
};

export type WorldCupGroup = {
  letter: string;
  teams: Team[];
  matches: Match[];
  standings: TeamGroupStats[];
};

export type Scorer = {
  playerName: string;
  teamName: string;
  goals: number;
};

export type TournamentState = {
  allTeams: Team[];
  groups: WorldCupGroup[];
  topScorers: Scorer[];
  r32Matches: Match[];
  r16Matches: Match[];
  quarterFinals: Match[];
  semiFinals: Match[];
  thirdPlaceMatch: Match | null;
  finalMatch: Match | null;
  phase: TournamentPhase;
  currentGroupMatchDay: number;
  active: boolean;
  champion: Team | null;
  runnerUp: string | null;
  qualified3rd: Team[];
};
