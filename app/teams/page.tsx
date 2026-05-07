import type { Metadata } from "next";
import Link from "next/link";
import { Flag as CountryFlag } from "@/components/Flag";
import { getAllTeams } from "@/lib/teams";
import { Flag, Activity } from "lucide-react";

export const metadata: Metadata = {
  title: "Teams | WC26 Simulator",
  description: "Roster of 48 nations in the 2026 World Cup."
};

export default function TeamsPage() {
  const teams = getAllTeams();
  const grouped = new Map<string, typeof teams>();
  for (const team of teams) {
    grouped.set(team.group, [...(grouped.get(team.group) ?? []), team]);
  }

  return (
    <main className="flex-1 pb-20">
      <header className="border-b border-glass-border bg-navy-panel/30 px-6 py-8 backdrop-blur-md">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <div className="mb-2 flex items-center gap-2 label-micro text-white/50">
              <Flag className="size-3" />
              <span>Database Access</span>
            </div>
            <h1 className="font-outfit text-4xl font-black uppercase tracking-tight text-white">Nations Database</h1>
          </div>
          
          <div className="flex font-mono text-xs">
            <span className="border border-glass-border bg-white/5 px-4 py-2 uppercase tracking-widest text-[10px] text-white/50">
              Total Count: {teams.length}
            </span>
          </div>
        </div>
      </header>
      <div className="mx-auto max-w-7xl px-6 py-12 grid gap-16">
        {[...grouped.entries()].map(([group, groupTeams]) => (
          <section key={group} className="content-auto">
            <div className="mb-6 flex items-center gap-4">
              <h2 className="font-outfit text-xl font-black uppercase tracking-widest text-white">Group {group}</h2>
              <div className="h-px flex-1 bg-glass-border" />
            </div>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
              {groupTeams.map((team) => (
                <Link 
                  key={team.countryCode} 
                  href={`/teams/${team.countryCode}`} 
                  className="group relative flex flex-col items-center justify-center border border-glass-border bg-navy-panel/40 p-8 text-center transition-all hover:-translate-y-1 hover:border-white/30 hover:bg-navy-panel"
                >
                  <CountryFlag countryCode={team.countryCode} label={team.name} className="mb-4 text-5xl transition-transform group-hover:scale-110" />
                  <span className="mb-2 font-outfit text-xl font-bold uppercase tracking-tight text-white">{team.name}</span>
                  <span className="label-micro tracking-widest text-white/40">FIFA Rank: {team.fifaRanking}</span>
                  <div className="mt-4 flex items-center gap-2 border border-glass-border bg-black/20 px-3 py-1 label-micro tracking-widest text-white/50">
                    <Activity className="size-3" />
                    PWR: {team.strength}
                  </div>
                </Link>
              ))}
            </div>
          </section>
        ))}
      </div>
    </main>

  );
}
