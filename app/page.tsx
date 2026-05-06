"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { Button, LinkButton } from "@/components/ui/Button";
import { useTournament } from "@/components/TournamentProvider";

const facts = [
  "A Final será em 19 de Julho de 2026 no MetLife Stadium, NJ.",
  "Pela primeira vez, a Copa terá 48 seleções e 104 partidas.",
  "Sede tripla: EUA, México e Canadá receberão os jogos em 16 cidades.",
  "Estreantes: Curaçao, Cabo Verde, Jordânia e Uzbequistão estão na Copa!",
  "Curaçao é a menor nação da história a se classificar para uma Copa.",
  "O México é o primeiro país a sediar a Copa pela terceira vez."
];

export default function HomePage() {
  const { state, hydrated, startTournament } = useTournament();
  const [factIndex, setFactIndex] = useState(0);

  useEffect(() => {
    const timer = window.setInterval(() => setFactIndex((current) => (current + 1) % facts.length), 6000);
    return () => window.clearInterval(timer);
  }, []);

  return (
    <main className="home-hero relative grid place-items-center overflow-hidden px-4 py-8 text-center">
      <section className="relative z-10 mx-auto mt-8 flex max-w-5xl flex-col items-center">
        <div className="mb-8 text-6xl drop-shadow-[0_4px_16px_rgba(201,162,39,0.5)]">🏆</div>
        <div className="mb-6 rounded-full border border-gold bg-gold/15 px-5 py-2 text-xs font-bold uppercase tracking-[0.28em] text-gold">
          Simulador Oficial
        </div>

        <h1 className="text-[clamp(2.5rem,7vw,5rem)] font-black leading-[1.05] text-white drop-shadow-2xl">
          FIFA COPA DO MUNDO
          <span className="block text-gold">2026</span>
        </h1>
        <div className="mx-auto my-6 h-[3px] w-20 bg-gradient-to-r from-transparent via-gold to-transparent" />

        <div className="mb-8 flex w-full max-w-[600px] items-center gap-4 rounded-xl border border-gold/20 bg-white/[0.03] px-6 py-3 text-left backdrop-blur">
          <div className="grid size-8 shrink-0 place-items-center rounded-full bg-gold text-sm font-black text-navy shadow-[0_0_15px_rgba(201,162,39,0.3)]">
            !
          </div>
          <div className="min-w-0">
            <div className="text-[0.65rem] font-black uppercase tracking-[0.18em] text-gold">VOCÊ SABIA?</div>
            <div className="truncate text-sm font-medium text-white/90 sm:text-base">{facts[factIndex]}</div>
          </div>
        </div>

        <p className="mb-10 text-[1.2rem] font-normal text-white/70">📍 EUA • Canadá • México — 3 Países, 1 Taça</p>

        <div className="mb-8 flex items-center justify-center gap-5">
          <HostFlag flag="🇺🇸" label="EUA" />
          <span className="text-2xl text-gold/40">⚡</span>
          <HostFlag flag="🇨🇦" label="Canadá" />
          <span className="text-2xl text-gold/40">⚡</span>
          <HostFlag flag="🇲🇽" label="México" />
        </div>

        <div className="mb-12 mt-8 flex flex-wrap justify-center gap-6">
          <InfoCard value="48" label="🏁 Seleções" />
          <InfoCard value="12" label="🏆 Grupos" />
          <InfoCard value="104" label="⚽ Partidas" />
        </div>

        {!state.active ? (
          <div className="grid justify-items-center gap-3">
            <Button variant="green" className="rounded-full px-12 py-4 text-base uppercase tracking-wide" onClick={startTournament} disabled={!hydrated}>
              ▶ INICIAR TORNEIO
            </Button>
            <p className="text-sm text-white/40">ⓘ Clique para gerar todos os grupos e partidas automaticamente</p>
          </div>
        ) : (
          <div className="rounded-2xl border border-success-green bg-success-green/15 px-8 py-6">
            <div className="mb-2 font-bold text-green-300">
              <span className="mr-2 inline-block size-2.5 rounded-full bg-green-300 align-middle [animation:pulse-dot_1.5s_infinite]" />
              Torneio em andamento
            </div>
            <p className="mb-4 text-sm text-white/60">A Copa do Mundo 2026 está sendo simulada. Acompanhe os grupos e o chaveamento.</p>
            <div className="flex flex-wrap justify-center gap-3">
              <LinkButton href="/groups" variant="gold" className="rounded-full px-8">
                🏆 Ver Grupos
              </LinkButton>
              <Link href="/bracket" className="inline-flex min-h-10 items-center rounded-full border border-white/60 px-8 py-2 text-sm font-bold text-white transition hover:bg-white hover:text-navy">
                📊 Ver Chaveamento
              </Link>
            </div>
          </div>
        )}
      </section>
    </main>
  );
}

function HostFlag({ flag, label }: { flag: string; label: string }) {
  return (
    <div className="grid justify-items-center gap-1">
      <div className="text-[3.5rem] leading-none drop-shadow-lg transition hover:scale-110">{flag}</div>
      <div className="text-xs font-bold uppercase tracking-[0.18em] text-white/50">{label}</div>
    </div>
  );
}

function InfoCard({ value, label }: { value: string; label: string }) {
  return (
    <div className="rounded-2xl border border-gold/30 bg-white/[0.05] px-10 py-6 text-center backdrop-blur transition hover:-translate-y-1 hover:border-gold hover:bg-gold/10">
      <div className="text-[2.8rem] font-black leading-none text-gold">{value}</div>
      <div className="mt-2 text-xs font-semibold uppercase tracking-[0.18em] text-white/60">{label}</div>
    </div>
  );
}
