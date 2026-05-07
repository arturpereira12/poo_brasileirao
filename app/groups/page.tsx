"use client";

import Link from "next/link";
import { useMemo } from "react";
import { Flag } from "@/components/Flag";
import { useTournament } from "@/components/TournamentProvider";
import { getTournamentStats } from "@/lib/tournament";
import { LayoutGrid, AlertCircle, FastForward, Play, CheckCircle2 } from "lucide-react";
import { cn } from "@/lib/utils";

const displayTeamName = (name: string) => (name === "Bosnia and Herzegovina" ? "Bosnia" : name);

export default function GroupsPage() {
  const { state, startTournament, simulateGroupDay, simulateAllGroups } = useTournament();
  const stats = useMemo(() => getTournamentStats(state), [state]);
  const groupsWithTeamCodeMaps = useMemo(
    () =>
      state.groups.map((group) => ({
        group,
        teamCodeByName: new Map(group.teams.map((team) => [team.name, team.countryCode]))
      })),
    [state.groups]
  );
  const phaseDone = state.phase !== "GROUP_STAGE" && state.phase !== "NOT_STARTED";

  if (!state.active) {
    return (
      <main className="flex-1 flex flex-col items-center justify-center px-6 py-20">
        <div className="flex max-w-md flex-col items-center border border-wc-red/30 bg-wc-red/5 p-8 text-center backdrop-blur-md">
          <AlertCircle className="mb-4 size-8 text-wc-red" />
          <h2 className="mb-2 font-outfit text-xl font-bold uppercase tracking-widest text-white">System Offline</h2>
          <p className="mb-6 font-mono text-xs text-white/50">Simulation requires initialization sequence.</p>
          <button 
            onClick={startTournament}
            className="flex items-center gap-2 border border-glass-border bg-white/5 px-6 py-3 font-mono text-xs font-bold uppercase tracking-widest text-white transition-colors hover:bg-white hover:text-navy"
          >
            <Play className="size-3 fill-current" />
            Initialize Now
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="flex-1 pb-20">
      <header className="border-b border-glass-border bg-navy-panel/30 px-6 py-8 backdrop-blur-md">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <div className="mb-2 flex items-center gap-2 label-micro text-wc-blue">
              <LayoutGrid className="size-3" />
              <span>Phase 01</span>
            </div>
            <h1 className="font-outfit text-4xl font-black uppercase tracking-tight text-white">Group Stage</h1>
          </div>
          
          <div className="flex gap-2 font-mono text-xs">
            <span className={cn(
              "border px-3 py-1 uppercase tracking-widest text-[10px]",
              state.phase === "GROUP_STAGE" ? "border-wc-green/30 bg-wc-green/10 text-success-bright" : "border-glass-border bg-white/5 text-white/50"
            )}>
              {state.phase === "GROUP_STAGE" ? "Active" : phaseDone ? "Completed" : "Pending"}
            </span>
            <span className="border border-glass-border bg-white/5 px-3 py-1 uppercase tracking-widest text-[10px] text-white/50">
              Round {Math.min(state.currentGroupMatchDay, 3)}/3
            </span>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-7xl px-6 py-8">
        <section className="mb-10 grid grid-cols-2 gap-px border border-glass-border bg-glass-border md:grid-cols-4">
          <StatBox value={stats.simulatedGroupMatches} label="Matches Sim'd" />
          <StatBox value={stats.totalGoals} label="Total Goals" />
          <StatBox value={stats.averageGoals.toFixed(1)} label="Avg Goals/Match" />
          <StatBox value={state.groups.length} label="Groups Active" />
        </section>

        {state.phase === "GROUP_STAGE" && (
          <section className="mb-10 flex flex-wrap items-center justify-between gap-4 border border-wc-blue/30 bg-wc-blue/5 p-6">
            <p className="font-mono text-xs text-wc-blue">Awaiting command to process next match day.</p>
            <div className="flex flex-wrap gap-3">
              <button 
                className="flex items-center gap-2 border border-glass-border bg-white/5 px-6 py-3 font-mono text-xs font-bold uppercase tracking-widest text-white transition-colors hover:bg-white hover:text-navy"
                onClick={simulateGroupDay}
              >
                <FastForward className="size-3" />
                Sim Round {state.currentGroupMatchDay}
              </button>
              <button 
                className="flex items-center gap-2 border border-wc-red bg-wc-red/10 px-6 py-3 font-mono text-xs font-bold uppercase tracking-widest text-wc-red transition-colors hover:bg-wc-red hover:text-white"
                onClick={simulateAllGroups}
              >
                <FastForward className="size-3 fill-current" />
                Auto-Resolve Phase
              </button>
            </div>
          </section>
        )}

        {phaseDone && (
          <section className="mb-10 flex items-center justify-between border border-wc-green/30 bg-wc-green/10 p-6 text-success-bright">
            <div className="flex items-center gap-3 font-mono text-sm">
              <CheckCircle2 className="size-5" />
              <span>Phase 01 data finalized. 32 qualifiers locked.</span>
            </div>
            <Link 
              href="/bracket" 
              className="border border-wc-green px-4 py-2 font-mono text-xs font-bold uppercase tracking-widest transition-colors hover:bg-wc-green hover:text-white"
            >
              Access Bracket
            </Link>
          </section>
        )}

        <section className="grid gap-6 md:grid-cols-2 xl:grid-cols-4">
          {groupsWithTeamCodeMaps.map(({ group, teamCodeByName }) => (
            <article key={group.letter} className="content-auto flex flex-col border border-glass-border bg-navy-panel/40">
              <div className="flex items-center justify-between border-b border-glass-border bg-white/5 px-4 py-3">
                <span className="font-outfit text-xl font-black uppercase tracking-tight text-white">Group {group.letter}</span>
                <span className="font-mono text-[10px] tracking-widest text-white/30">4 TEAMS</span>
              </div>
              <div>
                <table className="w-full table-fixed text-left font-mono text-[9px] uppercase sm:text-[10px]">
                  <thead className="border-b border-glass-border text-white/40">
                    <tr>
                      <th className="w-7 px-2 py-2 font-normal">#</th>
                      <th className="px-2 py-2 font-normal">Team</th>
                      <th className="w-7 px-1 py-2 font-normal text-right">P</th>
                      <th className="w-7 px-1 py-2 font-normal text-right">W</th>
                      <th className="w-7 px-1 py-2 font-normal text-right">D</th>
                      <th className="w-7 px-1 py-2 font-normal text-right">L</th>
                      <th className="w-9 px-1 py-2 font-normal text-right text-white/20">GD</th>
                      <th className="w-10 px-2 py-2 font-bold text-white text-right">PTS</th>
                    </tr>
                  </thead>
                  <tbody>
                    {group.standings.map((team, index) => (
                      <tr
                        key={team.teamName}
                        className={cn(
                          "border-b border-glass-border/50",
                          index < 2 ? "bg-wc-green/5" : index === 2 ? "bg-white/5" : "opacity-40 grayscale"
                        )}
                      >
                        <td className="px-2 py-2 text-white/40">{index + 1}</td>
                        <td className="min-w-0 px-2 py-2 font-bold text-white">
                          <div className="flex min-w-0 items-center gap-2">
                            <Flag countryCode={team.countryCode ?? teamCodeByName.get(team.teamName)} label={team.teamName} className="h-3 w-4 shrink-0" />
                            <span className="truncate">{team.countryCode ?? teamCodeByName.get(team.teamName) ?? team.teamName.slice(0, 3).toUpperCase()}</span>
                          </div>
                        </td>
                        <td className="px-1 py-2 text-right">{team.played}</td>
                        <td className="px-1 py-2 text-right">{team.wins}</td>
                        <td className="px-1 py-2 text-right">{team.draws}</td>
                        <td className="px-1 py-2 text-right">{team.losses}</td>
                        <td className="px-1 py-2 text-right text-white/20">{team.goalDifference > 0 ? `+${team.goalDifference}` : team.goalDifference}</td>
                        <td className="px-2 py-2 text-right font-black text-white">{team.points}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              
              <div className="mt-auto border-t border-glass-border bg-black/20 p-3">
                <div className="mb-2 text-[8px] font-bold uppercase tracking-widest text-white/30">Match Log</div>
                <div className="flex flex-col gap-1">
                  {group.matches.map((match) => (
                    <div key={match.id} className={cn("grid grid-cols-[minmax(0,1fr)_auto_minmax(0,1fr)] items-center gap-2 font-mono text-[9px]", match.played ? "text-white/60" : "text-white/20")}>
                      <span className="flex min-w-0 items-center gap-1">
                        <Flag countryCode={match.homeTeam.countryCode} label={match.homeTeam.name} className="h-2.5 w-3.5 shrink-0" />
                        <span className="break-words leading-tight">{displayTeamName(match.homeTeam.name)}</span>
                      </span>
                      <span className={cn("font-bold", match.played ? "text-white" : "")}>{match.played ? `${match.homeScore}-${match.awayScore}` : "vs"}</span>
                      <span className="flex min-w-0 items-center justify-end gap-1 text-right">
                        <span className="break-words leading-tight">{displayTeamName(match.awayTeam.name)}</span>
                        <Flag countryCode={match.awayTeam.countryCode} label={match.awayTeam.name} className="h-2.5 w-3.5 shrink-0" />
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </article>
          ))}
        </section>

        <section className="mt-8 flex flex-wrap gap-6 label-micro tracking-widest text-white/40">
          <Legend color="bg-success-bright" label="Advancing (Top 2)" />
          <Legend color="bg-white/40" label="3rd Place (Wildcard Pool)" />
          <Legend color="bg-black border border-glass-border" label="Eliminated" />
        </section>
      </div>
    </main>
  );
}

function StatBox({ value, label }: { value: string | number; label: string }) {
  return (
    <div className="bg-navy p-6">
      <div className="font-outfit text-3xl font-black text-white">{value}</div>
      <div className="mt-1 font-mono text-[10px] uppercase tracking-[0.2em] text-white/40">{label}</div>
    </div>
  );
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <span className="flex items-center gap-2">
      <span className={cn("size-2 rounded-sm", color)} />
      {label}
    </span>
  );
}
