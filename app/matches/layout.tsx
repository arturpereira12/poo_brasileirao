import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Partidas — Copa do Mundo 2026",
  description: "Calendário de partidas por fase e rodada da Copa do Mundo 2026."
};

export default function MatchesLayout({ children }: { children: React.ReactNode }) {
  return children;
}
