import type { ReactNode } from "react";

export function EmptyState({ title, children }: { title: string; children?: ReactNode }) {
  return (
    <div className="not-started-java">
      <div className="mb-3 text-5xl text-gold">🔒</div>
      <h2 className="mb-2 text-2xl font-black text-white">{title}</h2>
      <div className="text-white/60">{children}</div>
    </div>
  );
}
