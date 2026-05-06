import type { ReactNode } from "react";

export function PhaseTag({ children, tone = "gold" }: { children: ReactNode; tone?: "green" | "gold" | "muted" }) {
  const color =
    tone === "green"
      ? "border-success-green bg-success-green/20 text-green-300"
      : tone === "muted"
        ? "border-white/20 bg-white/10 text-white/60"
        : "border-gold bg-gold/20 text-gold";
  return <span className={`inline-flex rounded-full border px-3 py-1 text-xs font-bold uppercase tracking-[0.14em] ${color}`}>{children}</span>;
}
