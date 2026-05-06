import type { ReactNode } from "react";

export function PageHeader({
  eyebrow,
  title,
  accent,
  children,
  centered = false,
  compact = false,
  uppercase = false
}: {
  eyebrow?: string;
  title: string;
  accent?: string;
  children?: ReactNode;
  centered?: boolean;
  compact?: boolean;
  uppercase?: boolean;
}) {
  return (
    <header className={`java-page-header ${compact ? "compact" : ""}`}>
      <div className={`app-container flex flex-wrap items-center gap-4 ${centered ? "justify-center text-center" : "justify-between"}`}>
        <div>
          {eyebrow && <div className="mb-2 text-xs font-black uppercase tracking-[0.18em] text-gold">{eyebrow}</div>}
          <h1 className={`java-page-title ${uppercase ? "upper" : ""}`}>
            {title}
            {accent && <span className="text-gold"> {accent}</span>}
          </h1>
        </div>
        {children}
      </div>
    </header>
  );
}
