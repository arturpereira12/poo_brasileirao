"use client";

import { useMemo, useState } from "react";
import { useTournament } from "@/components/TournamentProvider";
import { EmptyState } from "@/components/ui/EmptyState";
import { getAllGroupMatches, scoreDisplay } from "@/lib/tournament";
import type { Match } from "@/lib/types/tournament";

type MatchFilter = "GROUP" | "R32" | "R16" | "QF" | "SF" | "FINAL";

const filters: Array<[MatchFilter, string]> = [
  ["GROUP", "Grupo"],
  ["R32", "16 avos"],
  ["R16", "Oitavas"],
  ["QF", "Quartas"],
  ["SF", "Semifinais"],
  ["FINAL", "Finais"]
];

export default function MatchesPage() {
  const { state } = useTournament();
  const [phase, setPhase] = useState<MatchFilter>("GROUP");
  const [day, setDay] = useState(1);

  const matches = useMemo(() => {
    const byPhase: Record<MatchFilter, Match[]> = {
      GROUP: getAllGroupMatches(state).filter((match) => match.round === day),
      R32: state.r32Matches,
      R16: state.r16Matches,
      QF: state.quarterFinals,
      SF: state.semiFinals,
      FINAL: [state.thirdPlaceMatch, state.finalMatch].filter(Boolean) as Match[]
    };
    return byPhase[phase];
  }, [day, phase, state]);

  return (
    <main>
      <header className="java-page-header text-center">
        <div className="app-container">
          <h1 className="mb-0 text-5xl font-black uppercase text-white">Partidas</h1>
          <p className="font-bold uppercase text-gold">FIFA World Cup 2026</p>
        </div>
      </header>

      <div className="app-container pb-12">
        <section className="glass-card mb-8 p-6">
          <div className="flex flex-wrap items-center gap-3">
            <div className="flex flex-wrap gap-2">
              {filters.map(([value, label]) => (
                <button
                  key={value}
                  type="button"
                  className={`rounded-full px-6 py-2 font-semibold transition ${phase === value ? "bg-gold text-navy" : "text-white/80 hover:text-gold"}`}
                  onClick={() => setPhase(value)}
                >
                  {value === "GROUP" ? "Fase de Grupos" : label}
                </button>
              ))}
            </div>
            {phase === "GROUP" && (
              <div className="ml-auto flex overflow-hidden rounded border border-gold">
                {[1, 2, 3].map((item) => (
                  <button
                    key={item}
                    type="button"
                    className={`px-3 py-1.5 text-sm ${day === item ? "bg-gold text-navy" : "text-gold"}`}
                    onClick={() => setDay(item)}
                  >
                    Rodada {item}
                  </button>
                ))}
              </div>
            )}
          </div>
        </section>

        <section className="grid gap-4">
          {matches.length ? matches.map((match) => <JavaMatchCard key={match.id} match={match} />) : <EmptyState title="Nenhuma partida encontrada para este filtro." />}
        </section>
      </div>
    </main>
  );
}

function JavaMatchCard({ match }: { match: Match }) {
  return (
    <article className="glass-card overflow-hidden transition hover:scale-[1.01] hover:border-gold">
      <div className="flex justify-between bg-black/20 px-6 py-2 text-xs font-bold uppercase tracking-[1.5px] text-gold">
        <span>{match.knockoutRound ? roundLabel(match.knockoutRound) : `Grupo ${match.groupName}`}</span>
        <span>{match.matchNumber ? `Match #${match.matchNumber}` : `Rodada ${match.round}`}</span>
      </div>
      <div className="flex items-center justify-center gap-8 px-6 py-6">
        <div className="flex flex-1 items-center justify-end gap-4">
          <span className="text-xl font-bold text-white">{match.homeTeam.name}</span>
          <span className="text-3xl">{match.homeTeam.flagEmoji}</span>
        </div>
        <div className="min-w-[120px] rounded-[10px] border border-gold bg-black/30 px-6 py-2 text-center text-2xl font-black text-white">
          {match.played ? scoreDisplay(match).replace("x", "-") : <span className="text-base text-gold">VS</span>}
        </div>
        <div className="flex flex-1 items-center justify-start gap-4">
          <span className="text-3xl">{match.awayTeam.flagEmoji}</span>
          <span className="text-xl font-bold text-white">{match.awayTeam.name}</span>
        </div>
      </div>
      {match.played && (
        <div className="flex border-t border-white/10 bg-black/10 px-6 py-3 text-sm text-white/60">
          <div className="flex-1 text-right">{match.goalScorers[match.homeTeam.name]?.join(", ")}</div>
          <div className="mx-8 text-gold">⚽</div>
          <div className="flex-1 text-left">{match.goalScorers[match.awayTeam.name]?.join(", ")}</div>
        </div>
      )}
    </article>
  );
}

function roundLabel(round: string) {
  const labels: Record<string, string> = {
    ROUND_OF_32: "R32",
    ROUND_OF_16: "Oitavas",
    QUARTERFINAL: "Quartas",
    SEMIFINAL: "Semis",
    THIRD_PLACE: "3º Lugar",
    FINAL: "Final"
  };
  return labels[round] ?? round;
}
