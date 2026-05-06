"use client";

import { useTournament } from "@/components/TournamentProvider";
import { getAllGroupMatches, getAllKnockoutMatches, getTournamentStats, scoreDisplay } from "@/lib/tournament";

export default function StatsPage() {
  const { state } = useTournament();
  const stats = getTournamentStats(state);
  const playedGroup = getAllGroupMatches(state).filter((match) => match.played);
  const playedKnockout = getAllKnockoutMatches(state).filter((match) => match.played);
  const latestMatches = [...playedKnockout, ...playedGroup].slice(-8).reverse();

  return (
    <main>
      <header className="java-page-header text-center">
        <div className="app-container">
          <h1 className="java-page-title upper">Estatísticas do <span>Torneio</span></h1>
          <p className="mb-0 muted-text">{stats.phase}</p>
        </div>
      </header>

      <div className="app-container grid gap-8 pb-12">
        {state.champion && (
          <section className="border-y border-gold bg-gradient-to-r from-transparent via-gold/10 to-transparent p-4 text-center">
            <h2 className="mb-0 text-xl font-bold">🏆 CAMPEÃO: <strong className="text-gold">{state.champion.name}</strong> {state.champion.flagEmoji}</h2>
          </section>
        )}

        <section className="grid gap-4 md:grid-cols-3">
          <StatsCard icon="⚽" value={stats.totalMatches} label="Partidas disputadas" />
          <StatsCard icon="◎" value={stats.totalGoals} label="Gols marcados" />
          <StatsCard icon="⌁" value={stats.averageGoals.toFixed(1)} label="Média de gols" />
        </section>

        <section className="grid gap-10 lg:grid-cols-2">
          <div>
            <h2 className="section-title-java">🏅 Artilharia</h2>
            <div className="glass-card overflow-hidden">
              <table className="java-table">
                <thead>
                  <tr>
                    <th className="text-center">#</th>
                    <th>Jogador</th>
                    <th>Seleção</th>
                    <th className="text-center">Gols</th>
                  </tr>
                </thead>
                <tbody>
                  {state.topScorers.slice(0, 20).map((scorer, index) => (
                    <tr key={`${scorer.playerName}-${scorer.teamName}`}>
                      <td className="text-center text-lg font-black text-gold">{index + 1}</td>
                      <td className="font-bold text-white">{scorer.playerName}</td>
                      <td>{scorer.teamName}</td>
                      <td className="text-center text-xl font-black text-white">{scorer.goals}</td>
                    </tr>
                  ))}
                  {!state.topScorers.length && (
                    <tr>
                      <td colSpan={4} className="py-6 text-center text-white/45">Nenhum gol marcado ainda.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div>
            <h2 className="section-title-java">↺ Últimas Partidas</h2>
            <h3 className="mb-3 text-xs font-bold uppercase text-gold">Fase de Grupos</h3>
            <div className="glass-card overflow-hidden">
              <table className="java-table">
                <tbody>
                  {latestMatches.length ? latestMatches.map((match) => (
                    <tr key={match.id}>
                      <td className="text-right">{match.homeTeam.name}</td>
                      <td className="text-center font-bold text-gold">{scoreDisplay(match)}</td>
                      <td>{match.awayTeam.name}</td>
                    </tr>
                  )) : (
                    <tr>
                      <td className="py-6 text-center text-white/45" colSpan={3}>Nenhuma partida simulada.</td>
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

function StatsCard({ icon, value, label }: { icon: string; value: number | string; label: string }) {
  return (
    <div className="java-stat-card loose">
      <div className="java-stat-icon">{icon}</div>
      <div className="java-stat-num">{value}</div>
      <div className="java-stat-label">{label}</div>
    </div>
  );
}
