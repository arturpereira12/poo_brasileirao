import type { Metadata } from "next";
import Link from "next/link";
import { PageHeader } from "@/components/ui/PageHeader";
import { getAllTeams } from "@/lib/teams";

export const metadata: Metadata = {
  title: "Seleções — Copa do Mundo 2026",
  description: "Lista das 48 seleções agrupadas por grupo no simulador da Copa do Mundo 2026."
};

export default function TeamsPage() {
  const teams = getAllTeams();
  const grouped = new Map<string, typeof teams>();
  for (const team of teams) {
    grouped.set(team.group, [...(grouped.get(team.group) ?? []), team]);
  }

  return (
    <main>
      <PageHeader title="As" accent="48 Seleções" centered uppercase>
        <p className="mt-2 w-full text-center muted-text">Participantes da Copa do Mundo FIFA 2026</p>
      </PageHeader>
      <div className="app-container grid gap-12 pb-12">
        {[...grouped.entries()].map(([group, groupTeams]) => (
          <section key={group}>
            <h2 className="section-title-java uppercase text-gold">Grupo {group}</h2>
            <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-4">
              {groupTeams.map((team) => (
                <Link key={team.countryCode} href={`/teams/${team.countryCode}`} className="glass-card flex h-full min-h-[235px] flex-col items-center justify-center p-6 text-center text-inherit transition hover:-translate-y-1 hover:border-gold hover:text-white">
                  <span className="mb-4 text-[3.5rem] leading-none">{team.flagEmoji}</span>
                  <span className="mb-2 text-[1.2rem] font-extrabold text-white">{team.name}</span>
                  <span className="text-xs uppercase tracking-[1px] text-white/50">Ranking FIFA: #{team.fifaRanking}</span>
                  <span className="mt-3 rounded-full bg-gold/20 px-3 py-0.5 text-xs font-bold text-gold">⚡ Força: {team.strength}</span>
                </Link>
              ))}
            </div>
          </section>
        ))}
      </div>
    </main>
  );
}
