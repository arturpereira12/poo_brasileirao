"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
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
const STORAGE_VERSION = 1;

type StoredTournamentState = {
  version: typeof STORAGE_VERSION;
  state: TournamentState;
};

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
    const hydrateTimeout = window.setTimeout(() => {
      try {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored) {
          const parsed: unknown = JSON.parse(stored);
          if (isStoredTournamentState(parsed)) {
            setState(parsed.state);
          } else {
            localStorage.removeItem(STORAGE_KEY);
          }
        }
      } catch (error) {
        localStorage.removeItem(STORAGE_KEY);
        console.error("Failed to load tournament state:", error);
      } finally {
        setHydrated(true);
      }
    }, 0);
    return () => window.clearTimeout(hydrateTimeout);
  }, []);

  useEffect(() => {
    if (!hydrated) return;

    const payload: StoredTournamentState = { version: STORAGE_VERSION, state };
    const persist = () => localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));

    if (typeof window.requestIdleCallback === "function") {
      const idleId = window.requestIdleCallback(persist, { timeout: 1000 });
      return () => window.cancelIdleCallback(idleId);
    }

    const timeoutId = globalThis.setTimeout(persist, 0);
    return () => globalThis.clearTimeout(timeoutId);
  }, [state, hydrated]);

  const startTournament = useCallback(() => setState(initializeTournament(getAllTeams())), []);
  const resetTournament = useCallback(() => setState(createEmptyTournamentState()), []);
  const simulateGroupDay = useCallback(() => setState((current) => simulateCurrentGroupMatchDay(current)), []);
  const simulateAllGroups = useCallback(() => setState((current) => simulateAllGroupMatchDays(current)), []);
  const simulateKnockoutRound = useCallback(() => setState((current) => simulateCurrentKnockoutRound(current)), []);

  const value = useMemo<TournamentContextValue>(
    () => ({
      state,
      hydrated,
      startTournament,
      resetTournament,
      simulateGroupDay,
      simulateAllGroups,
      simulateKnockoutRound
    }),
    [state, hydrated, startTournament, resetTournament, simulateGroupDay, simulateAllGroups, simulateKnockoutRound]
  );

  return <TournamentContext.Provider value={value}>{children}</TournamentContext.Provider>;
}

export function useTournament() {
  const context = useContext(TournamentContext);
  if (!context) throw new Error("useTournament must be used inside TournamentProvider");
  return context;
}

function isStoredTournamentState(value: unknown): value is StoredTournamentState {
  if (!value || typeof value !== "object") return false;

  const candidate = value as Partial<StoredTournamentState>;
  return candidate.version === STORAGE_VERSION && isTournamentState(candidate.state);
}

function isTournamentState(value: unknown): value is TournamentState {
  if (!value || typeof value !== "object") return false;

  const candidate = value as Partial<TournamentState>;
  return (
    Array.isArray(candidate.allTeams) &&
    Array.isArray(candidate.groups) &&
    Array.isArray(candidate.topScorers) &&
    Array.isArray(candidate.r32Matches) &&
    Array.isArray(candidate.r16Matches) &&
    Array.isArray(candidate.quarterFinals) &&
    Array.isArray(candidate.semiFinals) &&
    isTournamentPhase(candidate.phase) &&
    typeof candidate.currentGroupMatchDay === "number" &&
    typeof candidate.active === "boolean" &&
    "thirdPlaceMatch" in candidate &&
    "finalMatch" in candidate &&
    "champion" in candidate &&
    "runnerUp" in candidate &&
    Array.isArray(candidate.qualified3rd)
  );
}

function isTournamentPhase(value: unknown): value is TournamentState["phase"] {
  return (
    value === "NOT_STARTED" ||
    value === "GROUP_STAGE" ||
    value === "ROUND_OF_32" ||
    value === "ROUND_OF_16" ||
    value === "QUARTERFINAL" ||
    value === "SEMIFINAL" ||
    value === "FINISHED"
  );
}
