import type { ReactNode } from "react";

export function Panel({ children, className = "" }: { children: ReactNode; className?: string }) {
  return <section className={`glass-card p-5 ${className}`}>{children}</section>;
}
