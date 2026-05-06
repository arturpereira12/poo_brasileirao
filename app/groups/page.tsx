"use client";

import Link from "next/link";
import { useTournament } from "@/components/TournamentProvider";
import { Button, LinkButton } from "@/components/ui/Button";
import { EmptyState } from "@/components/ui/EmptyState";
import { PageHeader } from "@/components/ui/PageHeader";
import { PhaseTag } from "@/components/ui/PhaseTag";
import { StatCard } from "@/components/ui/StatCard";
import { getTournamentStats } from "@/lib/tournament";

export default function GroupsPage() {
  const { state, startTournament, simulateGroupDay, simulateAllGroups } = useTournament();
  const stats = getTournamentStats(state);
  const phaseDone = state.phase !== "GROUP_STAGE" && state.phase !== "NOT_STARTED";

  if (!state.active) {
    return (
      <main>
        <PageHeader title="Fase de" accent="Grupos" compact>
          <div className="flex w-full items-center gap-2">
            <PhaseTag tone="muted">Não iniciada</PhaseTag>
          </div>
        </PageHeader>
        <div className="app-container py-4">
          <EmptyState title="Torneio não iniciado">
            <p className="mb-4">Inicie o torneio primeiro para visualizar os grupos.</p>
            <Button variant="gold" onClick={startTournament}>▶ Ir para o Início</Button>
          </EmptyState>
        </div>
      </main>
    );
  }

  return (
    <main>
      <PageHeader title="🏆 Fase de" accent="Grupos" compact>
        <div className="flex flex-wrap items-center gap-2">
          <PhaseTag tone={state.phase === "GROUP_STAGE" ? "green" : phaseDone ? "muted" : "gold"}>
            {state.phase === "GROUP_STAGE" ? "Em andamento" : phaseDone ? "Concluída" : "Não iniciada"}
          </PhaseTag>
          <PhaseTag>📅 Rodada {Math.min(state.currentGroupMatchDay, 3)}</PhaseTag>
          {phaseDone && <LinkButton href="/bracket" variant="gold">Ver Chaveamento</LinkButton>}
        </div>
      </PageHeader>

      <div className="app-container grid gap-6 py-4">
        <section className="grid grid-cols-2 gap-3 md:grid-cols-4">
          <StatCard value={stats.simulatedGroupMatches} label="Partidas Simuladas" />
          <StatCard value={stats.totalGoals} label="Gols Marcados" />
          <StatCard value={stats.averageGoals.toFixed(1)} label="Média Gols/Jogo" />
          <StatCard value={state.groups.length} label="Grupos" />
        </section>

        {state.phase === "GROUP_STAGE" && (
          <section className="glass-card p-4">
            <div className="flex flex-wrap items-center gap-3">
              <p className="min-w-0 flex-1 text-sm text-white/60">ⓘ Simule rodada por rodada ou todas de uma vez para avançar na fase de grupos.</p>
              <button className="btn-sim-java" onClick={simulateGroupDay}>⏭ Simular Rodada {state.currentGroupMatchDay}</button>
              <button className="btn-gold-java" onClick={simulateAllGroups}>⏩ Simular Todas as Rodadas</button>
            </div>
          </section>
        )}

        {phaseDone && (
          <section className="rounded-xl border border-success-green bg-success-green/15 p-4 text-green-300">
            <strong>✓ Fase de grupos concluída!</strong> 32 seleções avançaram para a fase eliminatória.
            <Link className="ml-3 inline-flex rounded-lg bg-gold px-3 py-1 text-sm font-bold text-navy" href="/bracket">Ver Chaveamento</Link>
          </section>
        )}

        <section className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
          {state.groups.map((group) => (
            <article key={group.letter} className="overflow-hidden rounded-[14px] border border-white/10 bg-[#1e2d50] transition hover:-translate-y-0.5 hover:border-gold/40 hover:shadow-2xl">
              <div className="flex items-center justify-between bg-gradient-to-r from-gold to-gold-light px-4 py-3 text-navy">
                <span className="text-sm font-black uppercase tracking-[0.18em]">Grupo {group.letter}</span>
                <span className="text-[0.65rem] font-bold opacity-70">4 SELEÇÕES</span>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-[0.78rem]">
                  <thead className="bg-black/30 text-[0.68rem] uppercase tracking-[0.12em] text-white/50">
                    <tr>
                      {["#", "", "Seleção", "J", "V", "E", "D", "GP", "GC", "SG", "PTS"].map((head) => (
                        <th key={head} className="px-2 py-1.5 text-left font-bold">{head}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {group.standings.map((team, index) => (
                      <tr
                        key={team.teamName}
                        className={`border-b border-white/5 ${
                          index < 2 ? "border-l-4 border-l-success-green bg-success-green/15" : index === 2 ? "border-l-4 border-l-gold bg-gold/10" : "opacity-70"
                        }`}
                      >
                        <td className="px-2 py-1.5"><PositionBadge index={index} /></td>
                        <td className="px-2 py-1.5">{team.flagEmoji}</td>
                        <td className="max-w-24 truncate px-2 py-1.5 font-semibold text-white/85">{team.teamName}</td>
                        <td className="px-2 py-1.5">{team.played}</td>
                        <td className="px-2 py-1.5">{team.wins}</td>
                        <td className="px-2 py-1.5">{team.draws}</td>
                        <td className="px-2 py-1.5">{team.losses}</td>
                        <td className="px-2 py-1.5">{team.goalsFor}</td>
                        <td className="px-2 py-1.5">{team.goalsAgainst}</td>
                        <td className="px-2 py-1.5">{team.goalDifference}</td>
                        <td className="px-2 py-1.5 font-black text-white">{team.points}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <details>
                <summary className="flex cursor-pointer list-none items-center justify-between bg-black/30 px-4 py-2 text-xs font-bold uppercase tracking-[0.14em] text-white/45">
                  ⚽ Resultados <span>⌄</span>
                </summary>
                <div>
                  {group.matches.map((match) => (
                    <div key={match.id} className={`flex items-center justify-between border-b border-white/5 px-4 py-2 text-xs ${match.played ? "" : "opacity-40"}`}>
                      <span className="flex-1 truncate text-right text-white/70">{match.homeTeam.name}</span>
                      <span className="mx-2 min-w-10 text-center font-black text-gold">{match.played ? `${match.homeScore}-${match.awayScore}` : "vs"}</span>
                      <span className="flex-1 truncate text-left text-white/70">{match.awayTeam.name}</span>
                    </div>
                  ))}
                </div>
              </details>
            </article>
          ))}
        </section>

        <section className="flex flex-wrap gap-4 text-sm text-white/50">
          <Legend color="bg-success-green" label="Classificado (Top 2)" />
          <Legend color="bg-gold" label="3º Lugar (possível vaga)" />
          <Legend color="bg-[#333]" label="Eliminado" />
        </section>

      </div>
    </main>
  );
}

function PositionBadge({ index }: { index: number }) {
  const colors = ["bg-gold text-navy", "bg-[#c0c0c0] text-black", "bg-[#cd7f32] text-white", "bg-white/10 text-white/50"];
  return <span className={`inline-grid size-5 place-items-center rounded-full text-xs font-bold ${colors[index] ?? colors[3]}`}>{index + 1}</span>;
}

function Legend({ color, label }: { color: string; label: string }) {
  return (
    <span className="inline-flex items-center gap-2">
      <span className={`size-2.5 rounded-sm ${color}`} />
      {label}
    </span>
  );
}
