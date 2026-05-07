"use client";

import { useMemo } from "react";
import { Flag } from "@/components/Flag";
import { useTournament } from "@/components/TournamentProvider";
import { getAllGroupMatches, getAllKnockoutMatches, getTournamentStats, scoreDisplay } from "@/lib/tournament";

import { BarChart3, Trophy, ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";

export default function StatsPage() {
  const { state } = useTournament();
  const { stats, latestMatches } = useMemo(() => {
    const playedGroup = getAllGroupMatches(state).filter((match) => match.played);
    const playedKnockout = getAllKnockoutMatches(state).filter((match) => match.played);

    const latestMatches = [...playedKnockout, ...playedGroup]
      .sort((a, b) => getMatchActivityTime(b) - getMatchActivityTime(a))
      .slice(0, 8);

    return {
      stats: getTournamentStats(state),
      latestMatches
    };
  }, [state]);

  return (
    <main className="flex-1 pb-20">
      <header className="border-b border-glass-border bg-navy-panel/30 px-6 py-8 backdrop-blur-md">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <div className="mb-2 flex items-center gap-2 label-micro text-white/50">
              <BarChart3 className="size-3" />
              <span>Metrics System</span>
            </div>
            <h1 className="font-outfit text-4xl font-black uppercase tracking-tight text-white">Global Analytics</h1>
          </div>
          <div className="flex font-mono text-xs">
            <span className="border border-glass-border bg-white/5 px-4 py-2 uppercase tracking-widest text-[10px] text-white/50">
              {stats.phase}
            </span>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-7xl px-6 py-12">
        {state.champion && (
          <section className="mb-8 border border-wc-red bg-wc-red/5 p-6 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <Trophy className="size-8 text-wc-red" />
              <div>
                <div className="label-micro tracking-widest text-wc-red">World Champion</div>
                <h2 className="font-outfit text-2xl font-black uppercase text-white">{state.champion.name}</h2>
              </div>
            </div>
            <Flag countryCode={state.champion.countryCode} label={state.champion.name} className="text-5xl" />
          </section>
        )}

        <section className="mb-12 grid grid-cols-1 gap-px bg-glass-border border border-glass-border md:grid-cols-3">
          <StatsBox value={stats.totalMatches} label="Matches Resolved" />
          <StatsBox value={stats.totalGoals} label="Total Goals" />
          <StatsBox value={stats.averageGoals.toFixed(1)} label="Avg Goals/Match" />
        </section>

        <section className="grid gap-12 lg:grid-cols-2 lg:gap-8">
          <div>
            <div className="mb-4 flex items-center gap-3">
              <h2 className="font-outfit text-xl font-bold uppercase tracking-widest text-white">Top Scorers</h2>
              <div className="h-px flex-1 bg-glass-border" />
            </div>
            <div className="border border-glass-border bg-navy-panel/40">
              <table className="w-full text-left font-mono text-[10px] uppercase">
                <thead className="border-b border-glass-border bg-white/5 text-white/40">
                  <tr>
                    <th className="px-3 py-2 font-normal text-left">#</th>
                    <th className="px-3 py-2 font-normal text-left">Player</th>
                    <th className="px-3 py-2 font-normal text-left">Team</th>
                    <th className="px-3 py-2 font-normal text-right">Goals</th>
                  </tr>
                </thead>
                <tbody>
                  {state.topScorers.slice(0, 20).map((scorer, index) => (
                    <tr key={`${scorer.playerName}-${scorer.teamName}`} className="border-b border-glass-border/50 hover:bg-white/5">
                      <td className="px-3 py-3 text-white/40">{index + 1}</td>
                      <td className="px-3 py-3 font-bold text-white">{scorer.playerName}</td>
                      <td className="px-3 py-3 text-white/60">{scorer.teamName}</td>
                      <td className="px-3 py-3 text-right text-lg font-black text-white">{scorer.goals}</td>
                    </tr>
                  ))}
                  {!state.topScorers.length && (
                    <tr>
                      <td colSpan={4} className="px-3 py-12 text-center text-white/30 border-b border-glass-border/50">AWAITING DATA</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div>
            <div className="mb-4 flex items-center gap-3">
              <h2 className="font-outfit text-xl font-bold uppercase tracking-widest text-white">Recent Activity</h2>
              <div className="h-px flex-1 bg-glass-border" />
            </div>
            <div className="border border-glass-border bg-navy-panel/40">
              <table className="w-full text-left font-mono text-[10px] uppercase">
                <thead className="border-b border-glass-border bg-white/5 text-white/40">
                  <tr>
                    <th className="px-3 py-2 font-normal text-right">Home</th>
                    <th className="px-3 py-2 font-normal text-center w-20">Score</th>
                    <th className="px-3 py-2 font-normal">Away</th>
                  </tr>
                </thead>
                <tbody>
                  {latestMatches.length ? latestMatches.map((match) => (
                    <tr key={match.id} className="border-b border-glass-border/50 hover:bg-white/5">
                      <td className="px-3 py-3 text-right text-white/80">{match.homeTeam.name}</td>
                      <td className="px-3 py-3 text-center font-bold text-wc-red">{scoreDisplay(match)}</td>
                      <td className="px-3 py-3 text-white/80">{match.awayTeam.name}</td>
                    </tr>
                  )) : (
                    <tr>
                      <td className="px-3 py-12 text-center text-white/30 border-b border-glass-border/50" colSpan={3}>AWAITING DATA</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}

function getMatchActivityTime(match: { date: string | null; matchNumber?: number; round: number }) {
  if (match.date) return new Date(match.date).getTime();
  return match.matchNumber ? Date.UTC(2026, 6, match.matchNumber) : match.round;
}

function StatsBox({ value, label }: { value: number | string; label: string }) {
  return (
    <div className="bg-navy p-8">
      <div className="font-outfit text-4xl font-black text-white">{value}</div>
      <div className="mt-2 label-micro tracking-widest text-white/40">{label}</div>
    </div>
  );
}
