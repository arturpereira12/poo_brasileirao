"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useTournament } from "@/components/TournamentProvider";
import { Play, Activity, LayoutGrid, Network, RotateCcw, X } from "lucide-react";
import { cn } from "@/lib/utils";

const facts = [
  "Final match set for July 19, 2026 at MetLife Stadium, NJ.",
  "Historic expansion: 48 nations competing across 104 matches.",
  "Tri-host alignment: USA, Mexico, and Canada over 16 cities.",
  "Debutants: Curaçao, Cape Verde, Jordan, and Uzbekistan.",
  "Mexico becomes the first nation to host the tournament three times."
];

export default function HomePage() {
  const { state, hydrated, startTournament, resetTournament } = useTournament();
  const [factIndex, setFactIndex] = useState(0);
  const [showRestartConfirm, setShowRestartConfirm] = useState(false);

  const confirmRestartTournament = () => {
    resetTournament();
    setShowRestartConfirm(false);
  };

  useEffect(() => {
    const timer = window.setInterval(() => setFactIndex((current) => (current + 1) % facts.length), 6000);
    return () => window.clearInterval(timer);
  }, []);

  return (
    <main className="relative flex-1 grid place-items-center overflow-hidden px-6 py-20 text-center min-h-[calc(100vh-58px)] bg-navy">
      {/* Vibrant Ambient Background Colors */}
      <div className="fixed -top-[20%] -left-[10%] w-[70vw] h-[70vh] rounded-full bg-wc-blue/25 blur-[140px] pointer-events-none mix-blend-screen" />
      <div className="fixed top-[10%] -right-[10%] w-[60vw] h-[60vh] rounded-full bg-wc-red/20 blur-[140px] pointer-events-none mix-blend-screen" />
      <div className="fixed -bottom-[20%] left-[10%] w-[80vw] h-[60vh] rounded-full bg-wc-green/20 blur-[140px] pointer-events-none mix-blend-screen" />

      <section className="relative z-10 mx-auto flex max-w-4xl flex-col items-center">
        <h1 className="font-outfit text-[clamp(3rem,8vw,6.5rem)] font-black leading-[0.9] tracking-tighter text-white">
          WORLD CUP
          <span className="block mt-2 text-white">
            2026
          </span>
        </h1>

        <div className="mt-8 flex max-w-lg items-start gap-3 text-left">
          <span className="font-mono text-gold-light mt-[2px] opacity-70">[</span>
          <p className="text-sm text-white/70 leading-relaxed font-mono">
            {facts[factIndex]}
          </p>
          <span className="font-mono text-gold-light mt-[2px] opacity-70">]</span>
        </div>

        <div className="mt-12 flex items-center gap-8 text-white/30 font-mono text-xs tracking-widest">
          <div className="flex flex-col items-center gap-2">
            <span className="text-2xl text-white/80 font-outfit">48</span>
            <span>NATIONS</span>
          </div>
          <div className="h-8 w-px bg-glass-border" />
          <div className="flex flex-col items-center gap-2">
            <span className="text-2xl text-white/80 font-outfit">12</span>
            <span>GROUPS</span>
          </div>
          <div className="h-8 w-px bg-glass-border" />
          <div className="flex flex-col items-center gap-2">
            <span className="text-2xl text-white/80 font-outfit">104</span>
            <span>MATCHES</span>
          </div>
        </div>

        <div className="mt-20 w-full max-w-xl">
          {!state.active ? (
            <div className="flex flex-col items-center gap-4">
              <button 
                onClick={startTournament} 
                disabled={!hydrated}
                className={cn(
                  "group relative flex items-center gap-4 overflow-hidden rounded-full bg-gradient-to-r from-wc-blue via-wc-red to-wc-green p-[1px] transition-transform hover:scale-105 active:scale-95 disabled:opacity-50 disabled:hover:scale-100"
                )}
              >
                <div className="flex h-full w-full items-center gap-4 rounded-full bg-navy px-8 py-4 transition-colors group-hover:bg-transparent">
                  <Play className="relative z-10 size-4 fill-white text-white group-hover:fill-white" />
                  <span className="relative z-10 font-outfit text-sm font-bold uppercase tracking-widest text-white">
                    Start Tournament
                  </span>
                </div>
              </button>
              <p className="label-micro tracking-widest text-white/40">
                Click to generate groups & matches
              </p>
            </div>
          ) : (
            <div className="flex flex-col items-center gap-8">
              <div className="flex items-center gap-3 border border-wc-green/30 bg-wc-green/10 px-6 py-2.5 rounded-full text-success-bright text-sm font-mono backdrop-blur-sm">
                <Activity className="size-4 animate-pulse" />
                <span>Tournament in Progress</span>
              </div>
              <div className="flex w-full flex-col sm:flex-row gap-4 justify-center">
                <Link 
                  href="/groups" 
                  className="flex items-center justify-center gap-3 rounded-xl border border-glass-border bg-navy-panel/50 px-8 py-4 text-sm font-medium text-white transition-all hover:bg-navy-panel hover:border-gold/30"
                >
                  <LayoutGrid className="size-4 text-white/50" />
                  View Groups
                </Link>
                <Link 
                  href="/bracket" 
                  className="flex items-center justify-center gap-3 rounded-xl border border-glass-border bg-navy-panel/50 px-8 py-4 text-sm font-medium text-white transition-all hover:bg-navy-panel hover:border-gold/30"
                >
                  <Network className="size-4 text-white/50" />
                  View Bracket
                </Link>
                <button
                  type="button"
                  onClick={() => setShowRestartConfirm(true)}
                  disabled={!hydrated}
                  className="flex items-center justify-center gap-3 rounded-xl border border-wc-red/30 bg-wc-red/10 px-8 py-4 text-sm font-medium text-white transition-all hover:border-wc-red/60 hover:bg-wc-red/20 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <RotateCcw className="size-4 text-danger-bright" />
                  Restart
                </button>
              </div>
            </div>
          )}
        </div>
      </section>

      {showRestartConfirm && (
        <div
          className="fixed inset-0 z-50 grid place-items-center bg-black/70 px-6 backdrop-blur-sm"
          role="dialog"
          aria-modal="true"
          aria-labelledby="restart-title"
        >
          <div className="w-full max-w-md border border-wc-red/40 bg-black p-6 text-left shadow-2xl shadow-black/40">
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <p className="mb-2 font-mono text-[10px] font-bold uppercase tracking-[0.24em] text-danger-bright">Destructive action</p>
                <h2 id="restart-title" className="font-outfit text-2xl font-black uppercase tracking-tight text-white">
                  Restart tournament?
                </h2>
              </div>
              <button
                type="button"
                aria-label="Cancel restart"
                onClick={() => setShowRestartConfirm(false)}
                className="text-white/40 transition-colors hover:text-white"
              >
                <X className="size-5" />
              </button>
            </div>

            <p className="font-mono text-xs leading-relaxed text-white/55">
              This will erase the current simulation progress and generate a fresh set of groups and matches.
            </p>

            <div className="mt-8 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
              <button
                type="button"
                onClick={() => setShowRestartConfirm(false)}
                className="border border-glass-border bg-white/5 px-5 py-3 font-mono text-xs font-bold uppercase tracking-widest text-white transition-colors hover:bg-white hover:text-navy"
              >
                Keep Current
              </button>
              <button
                type="button"
                onClick={confirmRestartTournament}
                disabled={!hydrated}
                className="flex items-center justify-center gap-2 border border-wc-red bg-wc-red/15 px-5 py-3 font-mono text-xs font-bold uppercase tracking-widest text-danger-bright transition-colors hover:bg-wc-red hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
              >
                <RotateCcw className="size-3" />
                Restart Now
              </button>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
