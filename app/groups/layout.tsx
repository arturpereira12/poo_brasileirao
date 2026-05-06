import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Fase de Grupos — Copa do Mundo 2026",
  description: "Classificação, rodadas e resultados dos 12 grupos da Copa do Mundo 2026."
};

export default function GroupsLayout({ children }: { children: React.ReactNode }) {
  return children;
}
