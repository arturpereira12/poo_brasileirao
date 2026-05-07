import type { ReactNode } from "react";

export function EmptyState({ title, children }: { title: string; children?: ReactNode }) {
  return (
    <div className="rounded-2xl border border-gold/30 bg-navy-panel p-12 text-center">
      <div className="mb-3 text-5xl text-gold" aria-hidden="true">🔒</div>
      <h2 className="mb-2 text-2xl font-black text-white">{title}</h2>
      <div className="text-white/60">{children}</div>
    </div>
  );
}
