"use client";

import Link from "next/link";
import { Flag } from "@/components/Flag";
import { getWinner, scoreDisplay } from "@/lib/tournament";
import type { Match } from "@/lib/types/tournament";

export function MatchCard({ match, compact = false }: { match: Match; compact?: boolean }) {
  const winner = getWinner(match);

  return (
    <article className={`grid gap-3 rounded-xl border border-white/10 bg-navy-panel/80 shadow-xl transition hover:border-gold/60 ${compact ? "p-3" : "p-4"}`}>
      <div className="flex justify-between gap-3 text-xs font-extrabold uppercase tracking-[0.12em] text-white/50">
        <span>{match.matchNumber ? `Jogo ${match.matchNumber}` : match.groupName ? `Grupo ${match.groupName}` : "Partida"}</span>
        <span>{match.date ? formatDate(match.date) : match.knockoutRound ? roundLabel(match.knockoutRound) : ""}</span>
      </div>
      <div className="grid items-center gap-3 md:grid-cols-[minmax(0,1fr)_auto_minmax(0,1fr)]">
        <TeamLine team={match.homeTeam} score={match.homeScore} played={match.played} winner={winner?.name === match.homeTeam.name} />
        <div className="min-w-20 text-center font-black text-gold md:text-base">{scoreDisplay(match)}</div>
        <TeamLine team={match.awayTeam} score={match.awayScore} played={match.played} winner={winner?.name === match.awayTeam.name} />
      </div>
      {!compact && match.played && (
        <div className="grid gap-1 text-sm text-white/55">
          {[match.homeTeam, match.awayTeam].map((team) => (
            <span key={team.name}>
              <Flag countryCode={team.countryCode} label={team.name} className="mr-2" /> {match.goalScorers[team.name]?.join(", ") || "Sem gols"}
            </span>
          ))}
        </div>
      )}
    </article>
  );
}

function TeamLine({
  team,
  score,
  played,
  winner
}: {
  team: Match["homeTeam"];
  score: number;
  played: boolean;
  winner: boolean;
}) {
  return (
    <Link
      href={`/teams/${team.countryCode}`}
      className={`flex min-w-0 items-center gap-2 text-white/75 transition hover:text-gold last:md:justify-end ${winner ? "font-black text-white" : ""}`}
    >
      <Flag countryCode={team.countryCode} label={team.name} />
      <span className="truncate">{team.name}</span>
      {played && <span className="font-black text-gold">{score}</span>}
    </Link>
  );
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat("pt-BR", { day: "2-digit", month: "2-digit", year: "numeric" }).format(new Date(`${value}T00:00:00`));
}

function roundLabel(round: string) {
  const labels: Record<string, string> = {
    ROUND_OF_32: "16 avos",
    ROUND_OF_16: "Oitavas",
    QUARTERFINAL: "Quartas",
    SEMIFINAL: "Semifinal",
    THIRD_PLACE: "3º lugar",
    FINAL: "Final"
  };
  return labels[round] ?? round;
}
