import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Estatísticas — Copa do Mundo 2026",
  description: "Artilharia, últimas partidas e estatísticas do torneio simulado."
};

export default function StatsLayout({ children }: { children: React.ReactNode }) {
  return children;
}
