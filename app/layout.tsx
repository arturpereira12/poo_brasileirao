import type { Metadata } from "next";
import { Geist, Outfit } from "next/font/google";
import { Nav } from "@/components/Nav";
import { TournamentProvider } from "@/components/TournamentProvider";
import "flag-icons/css/flag-icons.min.css";
import "./globals.css";

const geist = Geist({
  subsets: ["latin"],
  variable: "--font-geist-sans",
});

const outfit = Outfit({
  subsets: ["latin"],
  variable: "--font-outfit-display",
});

export const metadata: Metadata = {
  title: "WC26 Simulator | Terminal",
  description: "Advanced simulator for the 2026 World Cup"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR" className={`${geist.variable} ${outfit.variable} dark`}>
      <body className="antialiased font-geist min-h-[100dvh] flex flex-col relative">
        <div className="fixed inset-0 pointer-events-none z-[-1] bg-[url('/noise.svg')] opacity-20 contrast-150 grayscale" />
        <TournamentProvider>
          <Nav />
          <main className="flex-1 flex flex-col">
            {children}
          </main>
        </TournamentProvider>
      </body>
    </html>
  );
}
