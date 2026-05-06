import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { getAllTeams, getTeamByCodeOrName } from "@/lib/teams";
import { TeamDetailClient } from "./TeamDetailClient";

type TeamDetailPageProps = {
  params: Promise<{ code: string }>;
};

export function generateStaticParams() {
  return getAllTeams().map((team) => ({ code: team.countryCode }));
}

export async function generateMetadata({ params }: TeamDetailPageProps): Promise<Metadata> {
  const { code } = await params;
  const team = getTeamByCodeOrName(code);
  if (!team) {
    return {
      title: "Seleção não encontrada — Copa do Mundo 2026"
    };
  }

  return {
    title: `${team.name} — Copa do Mundo 2026`,
    description: `Elenco, força e partidas da seleção ${team.name} no simulador da Copa do Mundo 2026.`
  };
}

export default async function TeamDetailPage({ params }: TeamDetailPageProps) {
  const { code } = await params;
  const team = getTeamByCodeOrName(code);
  if (!team) notFound();

  return <TeamDetailClient code={code} fallbackTeamName={team.name} />;
}
