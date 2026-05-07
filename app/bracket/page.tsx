"use client";

import Link from "next/link";
import { useMemo } from "react";
import { Flag } from "@/components/Flag";
import { useTournament } from "@/components/TournamentProvider";
import { isTournamentCompleted, phaseLabel, getWinner } from "@/lib/tournament";
import type { Match } from "@/lib/types/tournament";

import { Network, Lock, Trophy } from "lucide-react";

export default function BracketPage() {
  const { state, simulateKnockoutRound } = useTournament();
  const columns = useMemo<Array<[string, Match[]]>>(
    () => [
      ["Round of 32", state.r32Matches],
      ["Round of 16", state.r16Matches],
      ["Quarter-finals", state.quarterFinals],
      ["Semi-finals", state.semiFinals],
      ["Finals", [state.thirdPlaceMatch, state.finalMatch].filter(Boolean) as Match[]]
    ],
    [state.r32Matches, state.r16Matches, state.quarterFinals, state.semiFinals, state.thirdPlaceMatch, state.finalMatch]
  );
  const groupStageComplete = state.phase !== "GROUP_STAGE" && state.phase !== "NOT_STARTED";

  return (
    <main className="flex-1 pb-20">
      <header className="border-b border-glass-border bg-navy-panel/30 px-6 py-8 backdrop-blur-md">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <div className="mb-2 flex items-center gap-2 label-micro text-wc-red">
              <Network className="size-3" />
              <span>Phase 02</span>
            </div>
            <h1 className="font-outfit text-4xl font-black uppercase tracking-tight text-white">Knockout Stage</h1>
          </div>
          
          <div className="flex font-mono text-xs">
            <span className="border border-glass-border bg-white/5 px-4 py-2 uppercase tracking-widest text-[10px] text-white/50">
              {phaseLabel(state.phase)}
            </span>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-7xl px-6 py-12 relative">
        {!groupStageComplete ? (
          <div className="mx-auto flex max-w-md flex-col items-center border border-glass-border bg-white/5 p-12 text-center backdrop-blur-sm">
            <Lock className="mb-6 size-8 text-white/20" />
            <h3 className="mb-3 font-outfit text-xl font-bold uppercase tracking-widest text-white">Groups Active</h3>
            <p className="mb-8 font-mono text-xs text-white/40">Bracket generation locked until Phase 01 concludes.</p>
            <Link href="/groups" className="border border-white/20 bg-white/5 px-6 py-3 font-mono text-xs font-bold uppercase tracking-widest text-white transition-colors hover:bg-white hover:text-navy">
              Return to Groups
            </Link>
          </div>
        ) : (
          <>
            {state.champion && (
              <section className="relative mx-auto mb-16 flex max-w-2xl flex-col items-center overflow-hidden border border-wc-red/40 bg-wc-red/5 px-12 py-16 text-center">
                <div className="absolute top-0 w-full h-1 bg-gradient-to-r from-wc-blue via-wc-red to-wc-green" />
                <Trophy className="mb-6 size-12 text-wc-red" />
                <div className="mb-2 label-micro tracking-[0.3em] text-wc-red">
                  World Champion 2026
                </div>
                <h2 className="font-outfit text-5xl font-black tracking-tighter text-white">{state.champion.name}</h2>
                <Flag countryCode={state.champion.countryCode} label={state.champion.name} className="mt-8 text-7xl" />
              </section>
            )}

            <section className="flex items-start gap-8 overflow-x-auto pb-24 snap-x">
              {columns.map(([title, matches]) => (
                <div className="flex flex-col min-w-[240px] snap-center shrink-0 min-h-[600px] justify-between py-10 relative group" key={title}>
                  {/* Subtle connection line for alignment visualization behind cards */}
                  <div className="absolute top-10 bottom-10 left-1/2 w-px bg-glass-border/30 -z-10" />

                  <div className="absolute -top-6 left-0 right-0 border-b border-wc-blue/50 pb-2 text-center font-mono text-[10px] font-extrabold uppercase tracking-[0.2em] text-wc-blue drop-shadow-sm">
                    [{title}]
                  </div>

                  {matches.length ? (
                    matches.map((match) => (
                      <BracketMatch key={match.id} match={match} final={match.knockoutRound === "FINAL"} />
                    ))
                  ) : (
                    <div className="flex h-20 items-center justify-center rounded-sm border border-dashed border-white/10 bg-white/[0.01] text-xs font-mono text-white/20">
                      AWAITING DATA
                    </div>
                  )}
                </div>
              ))}
            </section>

            {!state.champion && (
              <div className="fixed bottom-10 left-1/2 z-40 -translate-x-1/2">
                <button 
                  className="rounded-none bg-gradient-to-r from-wc-blue via-wc-red to-wc-green px-8 py-4 font-outfit text-sm font-black uppercase tracking-[0.2em] text-white shadow-2xl transition-transform hover:-translate-y-1 active:scale-95 disabled:opacity-50 disabled:pointer-events-none" 
                  onClick={simulateKnockoutRound} 
                  disabled={isTournamentCompleted(state)}
                >
                  <span className="flex items-center gap-3">
                    <span className="inline-block h-2 w-2 rounded-full bg-white animate-pulse" />
                    SIMULATE NEXT ROUND
                  </span>
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </main>
  );
}

function BracketMatch({ match, final = false }: { match: Match; final?: boolean }) {
  const winner = getWinner(match);

  return (
    <article className={`my-3 overflow-hidden rounded-sm border ${final ? "border-wc-red" : "border-glass-border/40"} bg-navy-panel/80 backdrop-blur-sm transition-all hover:scale-[1.02] hover:border-white/20 hover:bg-navy-panel`}>
      <div className={`flex justify-between border-b px-3 py-1.5 label-micro tracking-widest ${final ? "border-wc-red/30 bg-wc-red/10 text-wc-red" : "border-glass-border/30 bg-black/40 text-white/30"}`}>
        <span>{match.matchNumber ? `M#${match.matchNumber}` : "M#"}</span>
        <span>{final ? "FINAL MATCH" : match.knockoutRound ?? ""}</span>
      </div>
      <BracketTeam team={match.homeTeam} score={match.homeScore} played={match.played} winner={winner?.name === match.homeTeam.name} loser={match.played && winner?.name !== match.homeTeam.name} final={final} />
      <div className="h-px w-full bg-glass-border/10" />
      <BracketTeam team={match.awayTeam} score={match.awayScore} played={match.played} winner={winner?.name === match.awayTeam.name} loser={match.played && winner?.name !== match.awayTeam.name} final={final} />
    </article>
  );
}

function BracketTeam({
  team,
  score,
  played,
  winner,
  loser,
  final
}: {
  team: Match["homeTeam"];
  score: number;
  played: boolean;
  winner: boolean;
  loser: boolean;
  final?: boolean;
}) {
  return (
    <div className={`flex items-center gap-3 px-3 py-2.5 transition-opacity ${loser ? "opacity-30 grayscale" : "opacity-100"}`}>
      <Flag countryCode={team.countryCode} label={team.name} className="text-xl" />
      <span className={`flex-1 truncate font-outfit text-sm font-semibold tracking-wide ${winner ? "text-white" : "text-white/60"}`}>{team.name}</span>
      <span className={`min-w-6 text-right font-mono text-sm font-bold ${winner ? (final ? "text-wc-red" : "text-white") : "text-white/40"}`}>
        {played ? score : "-"}
      </span>
    </div>
  );
}
