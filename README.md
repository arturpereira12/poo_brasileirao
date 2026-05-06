# WC 2026 Next.js

Simulador da Copa do Mundo 2026 reescrito em Next.js.

## Como rodar

```bash
npm install
npm run dev
```

Abra `http://localhost:3000`.

## Scripts

- `npm run dev`: servidor local de desenvolvimento.
- `npm run build`: build de producao do Next.js.
- `npm run typecheck`: checagem TypeScript.

## Funcionalidades

- 48 selecoes carregadas de `data/national_teams.json`.
- Inicio e reinicio da copa.
- Fase de grupos com tres rodadas, classificacao e criterios de desempate.
- Classificacao dos oito melhores terceiros colocados.
- Chaveamento do mata-mata, incluindo terceiro lugar e final.
- Simulacao de placares, prorrogacao, penaltis e artilharia.
- Paginas de grupos, chaveamento, partidas, estatisticas, selecoes e detalhe de selecao.
- Estado do torneio mantido apenas durante a sessao aberta; ao fechar e abrir de novo, a copa reinicia.
