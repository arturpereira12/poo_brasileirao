import { z } from "zod";
import teams from "@/data/national_teams.json";
import type { Team } from "@/lib/types/tournament";

const positionSchema = z.enum(["GOALKEEPER", "DEFENDER", "MIDFIELDER", "FORWARD"]);

const playerSchema = z.object({
  name: z.string().min(1),
  strength: z.number().finite(),
  position: positionSchema,
});

const teamSchema = z.object({
  name: z.string().min(1),
  strength: z.number().finite(),
  players: z.array(playerSchema),
  attackStrength: z.number().finite(),
  defenseStrength: z.number().finite(),
  midfieldStrength: z.number().finite(),
  countryCode: z.string().min(1),
  confederation: z.string().min(1),
  group: z.string().min(1),
  flagEmoji: z.string().min(1),
  fifaRanking: z.number().finite(),
});

const teamsSchema = z.array(teamSchema);

let cachedTeams: Team[] | null = null;

export function getAllTeams(): Team[] {
  if (cachedTeams) return cachedTeams;

  const parsedTeams = teamsSchema.parse(teams);
  
  cachedTeams = parsedTeams
    .map((team) => ({
      ...team,
      players: [...(team.players ?? [])],
      maxPlayerStrength: Math.max(...(team.players ?? []).map((player) => player.strength), 0)
    }))
    .sort((a, b) => a.group.localeCompare(b.group) || a.fifaRanking - b.fifaRanking)
    .map((team) => Object.freeze(team) as Team);

  return cachedTeams;
}

export function getTeamByCodeOrName(code: string, source: Team[] = getAllTeams()): Team | undefined {
  return source.find(
    (team) =>
      team.countryCode.toLowerCase() === code.toLowerCase() ||
      team.name.toLowerCase() === decodeURIComponent(code).toLowerCase()
  );
}
