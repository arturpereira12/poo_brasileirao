"use client";

import { useTournament } from "@/components/TournamentProvider";
import { isTournamentCompleted, phaseLabel, getWinner } from "@/lib/tournament";
import type { Match } from "@/lib/types/tournament";

export default function BracketPage() {
  const { state, simulateKnockoutRound } = useTournament();
  const columns: Array<[string, Match[]]> = [
    ["16 avos", state.r32Matches],
    ["Oitavas", state.r16Matches],
    ["Quartas", state.quarterFinals],
    ["Semifinais", state.semiFinals],
    ["Finais", [state.thirdPlaceMatch, state.finalMatch].filter(Boolean) as Match[]]
  ];
  const groupStageComplete = state.phase !== "GROUP_STAGE" && state.phase !== "NOT_STARTED";

  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_20%_30%,rgba(201,162,39,0.05)_0%,transparent_40%),radial-gradient(circle_at_80%_70%,rgba(45,138,78,0.05)_0%,transparent_40%)]">
      <header className="py-12 text-center">
        <div className="app-container">
          <h1 className="java-page-title upper">Fase <span>Eliminatória</span></h1>
          <p className="muted-text">{phaseLabel(state.phase)}</p>
        </div>
      </header>

      <div className="px-4 pb-28">
        {!groupStageComplete ? (
          <div className="mx-auto max-w-[600px] rounded border border-white/30 bg-[#212529] p-12 text-center">
            <div className="mb-4 text-6xl text-gold">🔒</div>
            <h3 className="mb-2 text-3xl font-bold text-white">Fase de Grupos em Andamento</h3>
            <p className="muted-text">O chaveamento será gerado assim que todos os grupos forem concluídos.</p>
            <a href="/groups" className="btn-gold-java mt-4">Ir para Grupos</a>
          </div>
        ) : (
          <>
            {state.champion && (
              <section className="mx-auto mb-6 max-w-xl rounded-[20px] bg-gradient-to-r from-gold to-gold-light p-8 text-center text-navy shadow-[0_0_50px_rgba(201,162,39,0.3)]">
                <div className="text-xl font-black uppercase tracking-[3px]">🏆 Campeão 🏆</div>
                <h2 className="my-2 text-4xl font-black">{state.champion.name}</h2>
                <div className="text-5xl">{state.champion.flagEmoji}</div>
              </section>
            )}

            <section className="flex items-start gap-5 overflow-x-auto pb-24">
              {columns.map(([title, matches]) => (
                <div className="flex min-h-[800px] min-w-[220px] flex-col justify-around" key={title}>
                  <div className="mb-5 border-b-2 border-gold p-2 text-center text-xs font-extrabold uppercase tracking-[2px] text-gold">{title}</div>
                  {matches.length ? (
                    matches.map((match) => <BracketMatch key={match.id} match={match} final={match.knockoutRound === "FINAL"} />)
                  ) : (
                    <div className="flex h-20 items-center justify-center rounded-[10px] border border-dashed border-white/10 bg-white/[0.02] text-xs font-semibold text-white/20">Aguardando...</div>
                  )}
                </div>
              ))}
            </section>

            {!state.champion && (
              <div className="fixed bottom-8 left-1/2 z-40 -translate-x-1/2">
                <button className="rounded-full bg-gold px-12 py-4 font-black uppercase tracking-[2px] text-navy shadow-2xl transition hover:-translate-y-1 hover:bg-white" onClick={simulateKnockoutRound} disabled={isTournamentCompleted(state)}>
                  ▶ Simular Próxima Rodada
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
    <article className={`my-2 overflow-hidden rounded-[10px] border ${final ? "border-2 border-gold" : "border-white/10"} bg-[rgba(30,45,80,0.6)] backdrop-blur transition hover:scale-[1.02] hover:border-gold`}>
      <div className={`flex justify-between border-b border-white/10 px-3 py-1 text-[0.65rem] font-bold text-white/40 ${final ? "bg-gold text-navy" : "bg-black/20"}`}>
        <span>{match.matchNumber ? `M#${match.matchNumber}` : "M#"}</span>
        <span>{final ? "GRANDE FINAL" : match.knockoutRound ?? ""}</span>
      </div>
      <BracketTeam team={match.homeTeam} score={match.homeScore} played={match.played} winner={winner?.name === match.homeTeam.name} loser={match.played && winner?.name !== match.homeTeam.name} />
      <BracketTeam team={match.awayTeam} score={match.awayScore} played={match.played} winner={winner?.name === match.awayTeam.name} loser={match.played && winner?.name !== match.awayTeam.name} />
    </article>
  );
}

function BracketTeam({
  team,
  score,
  played,
  winner,
  loser
}: {
  team: Match["homeTeam"];
  score: number;
  played: boolean;
  winner: boolean;
  loser: boolean;
}) {
  return (
    <div className={`flex items-center gap-2 border-b border-white/5 px-3 py-2 ${winner ? "winner" : ""} ${loser ? "opacity-50" : ""}`}>
      <span className="text-lg">{team.flagEmoji}</span>
      <span className={`flex-1 truncate text-sm font-semibold ${winner ? "text-white" : "text-white/75"}`}>{team.name}</span>
      <span className={`min-w-5 text-center font-black ${winner ? "rounded bg-gold px-1 text-navy" : "text-gold"}`}>{played ? score : ""}</span>
    </div>
  );
}
