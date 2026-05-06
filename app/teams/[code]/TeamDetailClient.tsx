"use client";

import Link from "next/link";
import { useMemo } from "react";
import { useTournament } from "@/components/TournamentProvider";
import { getTeamByCodeOrName } from "@/lib/teams";
import { getAllGroupMatches, getAllKnockoutMatches, scoreDisplay } from "@/lib/tournament";
import type { Team } from "@/lib/types/tournament";

export function TeamDetailClient({ code, fallbackTeamName }: { code: string; fallbackTeamName: string }) {
  const { state } = useTournament();
  
  // Use tournament state if active, otherwise fallback to the bare minimum we have
  const activeTeam = state.active 
    ? state.allTeams.find(t => t.countryCode.toLowerCase() === code.toLowerCase() || t.name.toLowerCase() === decodeURIComponent(code).toLowerCase())
    : undefined;

  // We are currently breaking the rule server-serialization by passing the whole team object from the server.
  // Instead, the server component should pass only what's necessary (e.g. the team name) and we fetch the rest
  // from our static data if the tournament isn't active.
  const staticTeam = useMemo(() => getTeamByCodeOrName(code), [code]);
  
  const team = activeTeam ?? staticTeam;
  
  const matches = useMemo(() => {
    if (!team) return [];
    return [...getAllGroupMatches(state), ...getAllKnockoutMatches(state)].filter(
      (match) => match.homeTeam.name === team.name || match.awayTeam.name === team.name
    );
  }, [state, team]);

  if (!team) return null;

  return (
    <main>
      <section className="relative mb-12 overflow-hidden border-b-2 border-gold bg-gradient-to-br from-navy to-[#0d1b35] py-16 text-center">
        <div className="pointer-events-none absolute -right-[5%] top-1/2 -translate-y-1/2 text-[20rem] opacity-[0.05]">{team.flagEmoji}</div>
        <div className="app-container relative">
          <div className="mb-4 text-8xl leading-none">{team.flagEmoji}</div>
          <h1 className="text-5xl font-black uppercase tracking-[2px] text-white md:text-[3.5rem]">{team.name}</h1>
          <div className="mt-3 text-xl font-bold uppercase tracking-[3px] text-gold">Grupo {team.group}</div>
        </div>
      </section>

      <div className="app-container grid gap-12 pb-12">
        <section className="grid gap-4 md:grid-cols-4">
          <DetailStat value={team.fifaRanking} label="Ranking FIFA" />
          <DetailStat value={team.strength} label="Força Geral" />
          <DetailStat value={team.attackStrength} label="Ataque" />
          <DetailStat value={team.defenseStrength} label="Defesa" />
        </section>

        <section className="grid gap-10 lg:grid-cols-2">
          <div>
            <h2 className="mb-6 border-l-4 border-gold pl-4 text-2xl font-extrabold text-white">Convocados</h2>
            <div className="grid gap-2">
              {team.players.map((player) => (
                <div key={player.name} className="flex items-center justify-between rounded-[10px] border border-white/10 bg-white/[0.03] p-4 transition hover:border-gold hover:bg-white/[0.08]">
                  <div>
                    <div className="font-bold text-white">{player.name}</div>
                    <div className="text-xs font-semibold uppercase tracking-[0.12em] text-white/40">{positionLabel(player.position)}</div>
                  </div>
                  <div className="font-black text-gold">{player.strength}</div>
                </div>
              ))}
            </div>
          </div>

          <div>
            <h2 className="mb-6 border-l-4 border-gold pl-4 text-2xl font-extrabold text-white">Partidas no Torneio</h2>
            <div className="grid gap-3">
              {matches.map((match) => (
                <div key={match.id} className="flex items-center justify-between border-b border-white/10 py-4">
                  <div className="flex-1 text-right">
                    <span>{match.homeTeam.name}</span> <span>{match.homeTeam.flagEmoji}</span>
                  </div>
                  <div className="min-w-[60px] text-center text-xl font-black">{scoreDisplay(match)}</div>
                  <div className="flex-1 text-left">
                    <span>{match.awayTeam.flagEmoji}</span> <span>{match.awayTeam.name}</span>
                  </div>
                </div>
              ))}
              {!matches.length && (
                <div className="text-white/50">
                  Ainda não disputou partidas. <Link className="text-gold" href="/groups">Simule a fase de grupos.</Link>
                </div>
              )}
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}

function DetailStat({ value, label }: { value: number | string; label: string }) {
  return (
    <div className="glass-card p-6 text-center">
      <div className="text-3xl font-black text-gold">{value}</div>
      <div className="text-xs uppercase tracking-[1px] text-white/40">{label}</div>
    </div>
  );
}

function positionLabel(position: Team["players"][number]["position"]): string {
  const labels = {
    GOALKEEPER: "Goleiro",
    DEFENDER: "Defensor",
    MIDFIELDER: "Meio-campista",
    FORWARD: "Atacante"
  };
  return labels[position];
}
