"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState } from "react";
import { cn } from "@/lib/utils";
import { Trophy, Menu, X } from "lucide-react";

const links = [
  { href: "/", label: "Overview" },
  { href: "/groups", label: "Groups" },
  { href: "/bracket", label: "Bracket" },
  { href: "/matches", label: "Matches" },
  { href: "/stats", label: "Stats" },
  { href: "/teams", label: "Teams" }
];

export function Nav() {
  const pathname = usePathname();
  const [open, setOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 border-b border-glass-border bg-navy/80 backdrop-blur-xl">
      <div className="mx-auto flex h-14 max-w-7xl items-center justify-between px-6">
        <Link 
          href="/" 
          className="flex items-center gap-3 font-outfit text-sm font-semibold tracking-wide text-white transition-opacity hover:opacity-80" 
          onClick={() => setOpen(false)}
        >
          <Trophy className="size-4 text-gold" />
          <span>WC26 <span className="text-white/40">SIMULATOR</span></span>
        </Link>

        <button
          type="button"
          aria-label="Toggle navigation"
          className="text-white/60 transition-colors hover:text-white lg:hidden"
          onClick={() => setOpen((current) => !current)}
        >
          {open ? <X className="size-5" /> : <Menu className="size-5" />}
        </button>

        <div className={cn(
          "absolute inset-x-0 top-[57px] flex flex-col border-b border-glass-border bg-navy px-6 py-4 lg:static lg:flex lg:flex-row lg:items-center lg:gap-8 lg:border-none lg:bg-transparent lg:p-0",
          open ? "flex" : "hidden"
        )}>
          {links.map((link) => {
            const active = link.href === "/" ? pathname === "/" : pathname === link.href || pathname.startsWith(`${link.href}/`);
            return (
              <Link
                key={link.href}
                href={link.href}
                onClick={() => setOpen(false)}
                className={cn(
                  "py-2 text-xs font-semibold uppercase tracking-widest transition-colors lg:py-0",
                  active ? "text-gold" : "text-white/40 hover:text-white"
                )}
              >
                {link.label}
              </Link>
            );
          })}
        </div>
      </div>
    </nav>
  );
}
