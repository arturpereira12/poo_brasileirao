import type { ReactNode } from "react";

export function PhaseTag({ children, tone = "gold" }: { children: ReactNode; tone?: "green" | "gold" | "muted" }) {
  const color =
    tone === "green"
      ? "phase-green"
      : tone === "muted"
        ? "phase-muted"
        : "phase-gold";
  return <span className={`inline-flex rounded-full border px-3 py-1 text-xs font-bold uppercase tracking-[0.14em] ${color}`}>{children}</span>;
}
