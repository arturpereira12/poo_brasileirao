"use client";

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { getAllTeams } from "@/lib/teams";
import {
  createEmptyTournamentState,
  initializeTournament,
  simulateAllGroupMatchDays,
  simulateCurrentGroupMatchDay,
  simulateCurrentKnockoutRound
} from "@/lib/tournament";
import type { TournamentState } from "@/lib/types/tournament";

const STORAGE_KEY = "wc26-tournament-state-v1";

type TournamentContextValue = {
  state: TournamentState;
  hydrated: boolean;
  startTournament: () => void;
  resetTournament: () => void;
  simulateGroupDay: () => void;
  simulateAllGroups: () => void;
  simulateKnockoutRound: () => void;
};

const TournamentContext = createContext<TournamentContextValue | null>(null);

export function TournamentProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<TournamentState>(() => createEmptyTournamentState());
  const [hydrated, setHydrated] = useState(false);

  useEffect(() => {
    // Avoid hydrating in the same tick as render to prevent cascading update warnings.
    const hydrateTimeout = setTimeout(() => {
      try {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored) {
          setState(JSON.parse(stored));
        }
      } catch (error) {
        console.error("Failed to load tournament state:", error);
      } finally {
        setHydrated(true);
      }
    }, 0);
    return () => clearTimeout(hydrateTimeout);
  }, []);

  useEffect(() => {
    if (hydrated) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    }
  }, [state, hydrated]);

  const value = useMemo<TournamentContextValue>(
    () => ({
      state,
      hydrated,
      startTournament: () => setState(initializeTournament(getAllTeams())),
      resetTournament: () => setState(createEmptyTournamentState()),
      simulateGroupDay: () => setState((current) => simulateCurrentGroupMatchDay(current)),
      simulateAllGroups: () => setState((current) => simulateAllGroupMatchDays(current)),
      simulateKnockoutRound: () => setState((current) => simulateCurrentKnockoutRound(current))
    }),
    [state, hydrated]
  );

  return <TournamentContext.Provider value={value}>{children}</TournamentContext.Provider>;
}

export function useTournament() {
  const context = useContext(TournamentContext);
  if (!context) throw new Error("useTournament must be used inside TournamentProvider");
  return context;
}
