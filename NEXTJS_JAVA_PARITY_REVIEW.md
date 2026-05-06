# Goal: reescrever o projeto Next.js com paridade fiel ao Java

Este documento foi escrito para ser usado como contexto em um `/goal` do Codex CLI. Ele define o resultado esperado, as restricoes, os criterios de aceite e uma ordem segura para transformar a versao Next.js em uma reescrita fiel do projeto Java/Spring/Thymeleaf, usando boas praticas de Next.js, React, TypeScript e TailwindCSS.

## Objetivo principal

Reescrever e refinar o frontend Next.js para que ele:

- tenha a mesma interface, textos, hierarquia visual e fluxos do projeto Java/Spring/Thymeleaf original;
- preserve a mesma regra de negocio do simulador Java;
- use boas praticas modernas de Next.js App Router, React e TypeScript;
- use TailwindCSS de verdade, sem depender de um grande arquivo CSS global customizado;
- passe em lint, typecheck, build e nos testes adicionados;
- seja facil de comparar visualmente com as telas Java originais.

## Estado atual confirmado

- `npm run lint`: passou.
- `npm run typecheck`: passou.
- `npm run build`: passou.
- `data/national_teams.json` e `data/teams_and_players.json` batem com os JSONs originais do Java.
- `lib/tournament.ts` esta bem proximo de `WorldCupManager.java`.
- A interface Next ainda nao esta identica a do Java.
- TailwindCSS nao esta instalado nem configurado.
- O estado salvo em `localStorage` pode causar mismatch de hidratacao.

## Fontes de verdade

Use o projeto Java em `HEAD` como fonte de verdade para comportamento, textos e layout. No worktree atual, os arquivos Java aparecem como removidos, mas podem ser lidos pelo historico do Git.

Comandos uteis:

```bash
git show HEAD:src/main/resources/templates/home.html
git show HEAD:src/main/resources/templates/groups.html
git show HEAD:src/main/resources/templates/bracket.html
git show HEAD:src/main/resources/templates/matches.html
git show HEAD:src/main/resources/templates/stats.html
git show HEAD:src/main/resources/templates/teams.html
git show HEAD:src/main/resources/templates/team_detail.html
git show HEAD:src/main/java/br/ufpb/poo/brasileirao/service/WorldCupManager.java
git show HEAD:src/main/java/br/ufpb/poo/brasileirao/tournament/GroupStandings.java
```

## Nao fazer

- Nao restaurar o projeto Java no worktree, a menos que isso seja pedido explicitamente.
- Nao apagar dados em `data/`.
- Nao alterar a regra de simulacao sem comparar com `WorldCupManager.java`.
- Nao trocar Next.js por outro framework.
- Nao deixar Tailwind apenas instalado; ele precisa ser usado na UI.
- Nao remover funcionalidades da versao Java para simplificar a migracao.
- Nao finalizar sem rodar lint, typecheck e build.

## Criterios de aceite

O trabalho so deve ser considerado completo quando:

- TailwindCSS estiver instalado, configurado e usado nas telas/componentes.
- `app/globals.css` ficar restrito a base/theme/reset pequeno, sem concentrar toda a UI.
- A Home estiver visualmente equivalente ao `home.html`.
- Grupos, Chaveamento, Partidas, Estatisticas, Selecoes e Detalhe de Selecao estiverem visualmente equivalentes aos templates Java.
- Os textos principais estiverem em portugues com acentos corretos.
- A navegacao tiver os mesmos destinos e estados ativos da versao Java.
- O estado persistido nao gerar hydration mismatch.
- As rotas estaticas aproveitarem Server Components quando possivel.
- `app/teams/[code]/page.tsx` tiver `generateStaticParams` e metadata por selecao.
- O simulador continuar funcionando do inicio ao campeao.
- `npm run lint`, `npm run typecheck` e `npm run build` passarem.
- Houver testes unitarios para a regra principal de torneio.

## Prioridade 1: corrigir base tecnica

### 1. Corrigir hidratacao do estado salvo

Arquivo:

- `components/TournamentProvider.tsx`

Problema:

- O estado inicial le `localStorage` no primeiro render do cliente.
- No servidor, o estado inicial e vazio.
- Com torneio salvo, o HTML do servidor pode divergir do primeiro render do cliente.

Como corrigir:

- Inicializar `useState` sempre com `createEmptyTournamentState()`.
- Ler `localStorage` somente dentro de `useEffect`.
- Adicionar um estado `hydrated` ou `ready`.
- Persistir no `localStorage` apenas depois de carregar o estado salvo.
- Considerar um payload versionado: `{ version: 1, state }`.
- Validar minimamente o JSON antes de aceitar o estado salvo.

### 2. Instalar e configurar TailwindCSS

Arquivos provaveis:

- `package.json`
- `package-lock.json`
- `postcss.config.mjs`
- `app/globals.css`

Resultado esperado:

- TailwindCSS instalado conforme a versao de Next usada.
- PostCSS configurado.
- `app/globals.css` com import/diretivas Tailwind e poucos estilos globais.
- Tokens de tema equivalentes ao Java: navy, navy-light, gold, gold-light, green, card bg, border glass, text muted.

### 3. Criar componentes de UI reutilizaveis

Componentes recomendados:

- `StatCard`
- `PageHeader`
- `Button`
- `EmptyState`
- `TeamCard`
- `MatchCard`
- `GroupCard`
- `GroupStandingsTable`
- `PlayerRow`
- `PhaseTag`
- `Disclosure` ou componente simples para resultados recolhiveis.

Objetivo:

- Evitar duplicacao de `Stat`.
- Evitar classes globais genericas demais.
- Preservar paridade visual sem copiar blocos grandes de CSS.

## Prioridade 2: paridade visual por tela

### Home

Referencia:

- `src/main/resources/templates/home.html`

Elementos obrigatorios:

- Navbar escura com marca "Copa do Mundo 2026".
- Hero centralizado em tela cheia.
- Icone/trofeu no topo.
- Badge "Simulador Oficial".
- Titulo em duas linhas: "FIFA COPA DO MUNDO" e "2026" em dourado.
- Divisor dourado.
- Ticker "VOCE SABIA?" com rotacao automatica.
- Subtitulo dos paises sede: EUA, Canada e Mexico.
- Linha de flags dos tres paises sede.
- Cards: 48 selecoes, 12 grupos, 104 partidas.
- Estado nao iniciado com botao "INICIAR TORNEIO".
- Estado ativo com banner "Torneio em andamento" e links para Grupos e Chaveamento.
- Efeitos de hover nos cards, flags e botoes.

### Grupos

Referencia:

- `src/main/resources/templates/groups.html`

Elementos obrigatorios:

- Header visual de pagina com titulo "Fase de Grupos".
- Indicacao de fase: em andamento, concluida ou nao iniciada.
- Badge de rodada atual.
- Cards de estatisticas.
- Toolbar para simular rodada atual e todas as rodadas.
- Spinner/estado de simulacao ou feedback visual equivalente.
- Alerta de fase concluida com link para chaveamento.
- Grid com 12 grupos.
- Tabela completa por grupo: posicao, flag, selecao, J, V, E, D, GP, GC, SG, PTS.
- Estilo visual para classificados, terceiros e eliminados.
- Resultados recolhiveis por grupo.
- Legenda de classificacao.

### Chaveamento

Referencia:

- `src/main/resources/templates/bracket.html`

Elementos obrigatorios:

- Colunas para 16 avos, oitavas, quartas, semifinais e finais.
- Numeros dos jogos 73 a 104 quando aplicavel.
- Placar, prorrogacao e penaltis.
- Estado bloqueado antes do fim da fase de grupos.
- Botao de simular rodada eliminatoria.
- Destaque para campeao, vice e final.
- Layout horizontal com overflow em telas menores, como o mata-mata atual, mas com tratamento visual do Java.

### Partidas

Referencia:

- `src/main/resources/templates/matches.html`

Elementos obrigatorios:

- Header de calendario/partidas equivalente.
- Filtros por fase.
- Filtro por rodada na fase de grupos.
- Cards/lista de partidas com data, grupo/rodada, times, placar e status.
- Mensagem vazia igual em intencao ao Java.
- Nomenclatura consistente: Grupo, 16 avos, Oitavas, Quartas, Semifinais, Finais.

### Estatisticas

Referencia:

- `src/main/resources/templates/stats.html`

Elementos obrigatorios:

- Cards principais de estatisticas.
- Ranking de artilharia.
- Ultimas partidas.
- Destaque do campeao quando existir.
- Visual equivalente ao Java em cards, tabelas, cores e hierarquia.

### Selecoes

Referencias:

- `src/main/resources/templates/teams.html`
- `src/main/resources/templates/team_detail.html`

Elementos obrigatorios:

- Lista de selecoes agrupada por grupo.
- Cards de selecao com flag, nome, ranking e forca.
- Detalhe com hero de selecao, flag grande, nome em uppercase e grupo.
- Opcional desejavel: flag em background com baixa opacidade, como no Java.
- Cards: Ranking FIFA, Forca Geral, Ataque e Defesa.
- Lista de convocados com nome, posicao e forca.
- Lista de partidas da selecao no torneio.

## Prioridade 3: boas praticas de Next.js

### Server Components por padrao

Diretriz:

- Usar Server Components para paginas que so leem dados estaticos.
- Isolar interatividade em componentes pequenos com `"use client"`.
- Evitar colocar `"use client"` em paginas inteiras quando apenas um widget precisa de estado.

Alvos:

- `app/teams/page.tsx` pode continuar server-side.
- `app/teams/[code]/page.tsx` deve virar Server Component com `params`.
- Componentes que dependem do torneio salvo podem ficar client-side, mas devem ser pequenos.

### Metadata

Adicionar:

- Metadata por pagina principal.
- `generateMetadata` para `app/teams/[code]/page.tsx`.
- Titulos em portugues coerentes com o Java.

### Rotas dinamicas

Para `app/teams/[code]/page.tsx`:

- Implementar `generateStaticParams`.
- Usar `params` da propria pagina.
- Evitar `useParams`.
- Manter `notFound()` para selecao inexistente.

## Prioridade 4: TypeScript e dados

Melhorias:

- Manter `strict: true`.
- Tipar retorno de `getTournamentStats`.
- Criar unions para filtros de partidas.
- Evitar `as Team[]` sem validacao.
- Validar JSONs com Zod ou validador equivalente.
- Criar helpers para posicoes traduzidas, fases e rounds.
- Separar tipos de estado persistido do estado em memoria, se for versionar localStorage.

## Prioridade 5: regra de negocio e testes

### Manter paridade com Java

Comparar especialmente:

- Inicializacao com exatamente 48 times.
- Criacao dos grupos A-L.
- 6 partidas por grupo.
- Rodadas 1, 2 e 3 com os mesmos confrontos.
- Criterios de classificacao.
- Oito melhores terceiros.
- Chaveamento dos jogos 73-88.
- Geracao de oitavas, quartas, semifinais, terceiro lugar e final.
- Simulacao de gols.
- Prorrogacao e penaltis.
- Artilharia ponderada por posicao e forca.

### Testes unitarios recomendados

Criar testes para:

- `initializeTournament` rejeita quantidade diferente de 48 times.
- `initializeTournament` cria 12 grupos.
- Cada grupo tem 4 times e 6 partidas.
- `simulateCurrentGroupMatchDay` simula apenas a rodada atual.
- A fase muda para `ROUND_OF_32` apos tres rodadas.
- `calculateQualifiedThirds` seleciona 8 terceiros.
- `simulateCurrentKnockoutRound` avanca fases corretamente.
- Penaltis nunca terminam empatados.
- `getWinner` e `getLoser` funcionam com tempo normal e penaltis.
- Artilharia soma gols corretamente.

### Testes de fluxo recomendados

Criar testes para:

- Home sem torneio ativo.
- Iniciar torneio.
- Simular uma rodada.
- Simular todos os grupos.
- Acessar chaveamento.
- Simular mata-mata ate campeao.
- Abrir detalhe de uma selecao antes e depois de jogos.

## Validacao visual

Para considerar a interface "igual ao Java":

- Abrir cada template Java pelo historico ou rodar a versao Java se ela for restaurada em outro workspace.
- Capturar screenshots de referencia.
- Capturar screenshots da versao Next.
- Comparar desktop e mobile.
- Checar cores, espacamentos, tamanho de fonte, pesos, estados hover, cards, tabelas e banners.
- Corrigir sobreposicoes e quebras em telas pequenas.

Viewport minimo recomendado:

- Mobile: 390x844.
- Tablet: 768x1024.
- Desktop: 1440x900.

## Ordem sugerida de execucao

1. Criar uma branch de trabalho.
2. Corrigir hidratacao do `TournamentProvider`.
3. Instalar/configurar TailwindCSS.
4. Criar tokens de tema e componentes base.
5. Migrar Home com paridade visual.
6. Migrar Grupos com tabela completa.
7. Migrar Chaveamento.
8. Migrar Partidas.
9. Migrar Estatisticas.
10. Migrar Selecoes e Detalhe.
11. Refatorar Server/Client Components.
12. Adicionar metadata e rotas estaticas.
13. Adicionar validacao dos JSONs.
14. Adicionar testes unitarios e de fluxo.
15. Rodar lint, typecheck, build e testes.
16. Fazer validacao visual.

## Checklist final

- [ ] Estado salvo sem hydration mismatch.
- [ ] TailwindCSS instalado.
- [ ] TailwindCSS usado nos componentes.
- [ ] `app/globals.css` reduzido a base/theme.
- [ ] Home equivalente ao Java.
- [ ] Grupos equivalente ao Java.
- [ ] Chaveamento equivalente ao Java.
- [ ] Partidas equivalente ao Java.
- [ ] Estatisticas equivalente ao Java.
- [ ] Selecoes equivalente ao Java.
- [ ] Detalhe de selecao equivalente ao Java.
- [ ] Server Components usados onde fizer sentido.
- [ ] `generateStaticParams` em selecoes.
- [ ] Metadata por pagina.
- [ ] JSONs validados.
- [ ] Testes unitarios da simulacao.
- [ ] Testes de fluxo principal.
- [ ] `npm run lint` passando.
- [ ] `npm run typecheck` passando.
- [ ] `npm run build` passando.
- [ ] Comparacao visual desktop/mobile feita.

## Prompt sugerido para `/goal`

Use este texto como ponto de partida no Codex CLI:

```text
/goal Reescrever e refinar o projeto Next.js deste workspace para que ele seja uma migracao fiel do projeto Java/Spring/Thymeleaf original. Use NEXTJS_JAVA_PARITY_REVIEW.md como especificacao. A versao final deve usar boas praticas de Next.js App Router, React, TypeScript e TailwindCSS, corrigir o estado persistido sem hydration mismatch, portar a interface de todas as telas Java com paridade visual e funcional, preservar a regra de negocio do WorldCupManager.java, adicionar testes relevantes e garantir que npm run lint, npm run typecheck, npm run build e os testes passem. Leia os templates Java via git show HEAD:src/main/resources/templates/*.html e compare comportamento com WorldCupManager.java antes de implementar cada tela. Trabalhe em etapas pequenas, valide a cada etapa e nao finalize ate cumprir o checklist final.
```

