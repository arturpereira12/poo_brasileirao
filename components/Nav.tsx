"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState } from "react";

const links = [
  { href: "/", label: "Home" },
  { href: "/groups", label: "Grupos" },
  { href: "/bracket", label: "Chaveamento" },
  { href: "/matches", label: "Partidas" },
  { href: "/stats", label: "Estatísticas" },
  { href: "/teams", label: "Seleções" }
];

export function Nav() {
  const pathname = usePathname();
  const [open, setOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 border-b-2 border-gold bg-[#212529]/95 backdrop-blur">
      <div className="flex min-h-[58px] w-full flex-wrap items-center justify-between px-3">
        <Link href="/" className="whitespace-nowrap text-[1.3rem] font-extrabold" style={{ color: "#c9a227" }} onClick={() => setOpen(false)}>
          ⚽ Copa do Mundo 2026
        </Link>
        <button
          type="button"
          aria-label="Toggle navigation"
          aria-expanded={open}
          className="inline-flex size-10 items-center justify-center rounded-md border border-white/15 text-white/60 lg:hidden"
          onClick={() => setOpen((current) => !current)}
        >
          <span className="grid gap-1">
            <span className="block h-0.5 w-6 bg-current" />
            <span className="block h-0.5 w-6 bg-current" />
            <span className="block h-0.5 w-6 bg-current" />
          </span>
        </button>
        <div className={`${open ? "flex" : "hidden"} w-full flex-col gap-1 pb-3 lg:flex lg:w-auto lg:flex-row lg:items-center lg:pb-0`}>
          {links.map((link) => {
            const active = link.href === "/" ? pathname === "/" : pathname.startsWith(link.href);
            return (
              <Link
                key={link.href}
                href={link.href}
                onClick={() => setOpen(false)}
                className={`whitespace-nowrap rounded-lg px-4 py-2 text-sm font-semibold transition ${
                  active ? "text-gold" : "text-white/80 hover:text-gold"
                }`}
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
