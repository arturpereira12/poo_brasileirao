import type { Metadata } from "next";
import { Nav } from "@/components/Nav";
import { TournamentProvider } from "@/components/TournamentProvider";
import "./globals.css";

export const metadata: Metadata = {
  title: "Copa do Mundo 2026 - Simulador",
  description: "Simulador da Copa do Mundo 2026 reescrito em Next.js"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <body>
        <TournamentProvider>
          <Nav />
          {children}
        </TournamentProvider>
      </body>
    </html>
  );
}
