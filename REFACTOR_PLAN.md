# WC26 Refactor Plan

This plan aims to improve the Next.js rewrite of the WC26 tournament simulator by addressing technical debt, improving performance according to Vercel's React best practices, and ensuring code quality and strict parity with the original Java version.

## Phase 1: Vercel React Best Practices Refactoring

### 1. Re-render Optimization & State Management
- **Rule `rerender-split-combined-hooks`**: Review `TournamentProvider`. Currently, the entire tournament state (which can get quite large) is held in a single `useState`. Since changes to the tournament state cause full context updates, any component consuming `useTournament()` will re-render. We should consider splitting state if parts update independently, or at least ensure we don't trigger unnecessary re-renders in components that only read parts of it.
- **Rule `rerender-functional-setstate`**: Ensure all state updates in `TournamentProvider` use functional updates (already mostly doing this, but double-check stability of callbacks).
- **Rule `rerender-memo` / `rerender-simple-expression-in-memo`**: Review `MatchesPage`. It uses `useMemo` for filtering matches based on phase. Since the state object changes on every match simulation, this `useMemo` might be re-evaluating often, but it's still better than re-filtering on every render.

### 2. Rendering Performance & Hydration
- **Hydration Fixes**: `NEXTJS_JAVA_PARITY_REVIEW.md` calls out hydration issues. The `TournamentProvider` initializes with `createEmptyTournamentState()` but doesn't actually read `localStorage` yet. Once persistence is added, we must follow rule `rendering-hydration-no-flicker` or ensure a proper `hydrated` flag delays rendering of client-specific data, avoiding SSR/CSR mismatches.
- **Rule `rendering-conditional-render`**: Review JSX conditionals. Use ternaries instead of `&&` for components to prevent layout shifts or returning `0` unintentionally.

### 3. Server-Side Performance & Bundle Size
- **Rule `server-serialization`**: In `app/teams/[code]/page.tsx`, we pass `fallbackTeam` (the whole `Team` object) to a Client Component. We should minimize the data passed. If the client can just fetch the team from `useTournament()` or a server action, do that, or only pass the bare minimum props.
- **Rule `server-dedup-props`**: Avoid passing large objects across the Server/Client boundary if not strictly needed.
- **Rule `bundle-barrel-imports`**: Avoid `import { Button, LinkButton } from "@/components/ui"`. Import directly from the specific component file if possible, or ensure the bundler tree-shakes effectively.

### 4. JavaScript Performance
- **Rule `js-set-map-lookups` / `js-index-maps`**: In `app/teams/page.tsx`, `grouped` uses `Map`. In `lib/tournament.ts`, `topScorersToMap` uses a Map. These are good. Ensure we use Maps/Sets wherever we do frequent lookups (e.g., `getTeamByCodeOrName` currently does a linear scan `source.find(...)`. If called often, build a lookup map).
- **Rule `js-cache-property-access`**: In `lib/tournament.ts`, hot loops simulating matches should cache property access where applicable.

## Phase 2: Code Quality and Best Practices Fixes

### 1. Fix TournamentProvider Hydration
As stated in `NEXTJS_JAVA_PARITY_REVIEW.md`:
- Introduce a `hydrated` state in `TournamentProvider` properly.
- Load from `localStorage` in a `useEffect`.
- Only persist state changes to `localStorage` after hydration is complete.
- Version the local storage payload to avoid breaking changes in the future.

### 2. Component Refactoring & File Structure
- Move UI components out of `components/ui.tsx` into individual files inside `components/ui/` to avoid a massive barrel file and improve maintainability (Rule `bundle-barrel-imports`).
- Refactor `app/teams/[code]/TeamDetailClient.tsx` to accept less data from the server and rely on context for tournament-specific match data.

### 3. Type Safety & Validation
- Implement Zod for validating `national_teams.json` in `lib/teams.ts` instead of manual `validateTeams` parsing. This provides stronger type safety and cleaner error handling.
- Separate serializable tournament state types from derived types.

### 4. Performance Optimizations in lib/tournament.ts
- `computeStandings` creates a new Map and array every time. For large simulations, optimize sorting and map lookups.
- `calculateQualifiedThirds` sorts repeatedly. Ensure efficient sorting.
- `simulateMatch` creates new arrays for goal scorers.

## Phase 3: Parity and Completeness

- Ensure Tailwind CSS is fully utilized and replace any lingering custom classes in `globals.css`.
- Verify `generateStaticParams` in `app/teams/[code]/page.tsx` covers all necessary paths.
- Add comprehensive Unit Tests for `lib/tournament.ts` to ensure exact parity with Java simulation logic.

## Actionable Steps (Next PRs/Commits)

1. **Hydration & Persistence**: Fix `TournamentProvider` to handle `localStorage` securely and without hydration mismatches.
2. **Component Splitting**: Break down `components/ui.tsx` into `components/ui/Button.tsx`, `components/ui/PageHeader.tsx`, etc.
3. **Data Fetching & Props**: Update `app/teams/[code]/page.tsx` to minimize props passed to Client Components.
4. **Validation**: Add `zod` and rewrite data validation in `lib/teams.ts`.
5. **Performance**: Apply specific Vercel JS/React performance rules to hot paths in `lib/tournament.ts` and React components.
