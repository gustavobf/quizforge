# quizforge-fed

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

# QuizForge Frontend

React 19, TypeScript estrito, Material UI, React Router e TanStack Query.
Fontes DM Sans e Manrope hospedadas no proprio build, sem chamadas externas.

## Executar

Node.js 22.18+ ou 24.12+ e backend na porta 8081.

```sh
npm ci
npm run dev
```

Aplicacao: http://localhost:3000. A URL padrao da API e `/api`, encaminhada
pelo Vite ao backend. O Nginx faz o mesmo encaminhamento em Docker.
Consulte `.env.example` para configurar `VITE_API_URL` e `API_PROXY_TARGET`.
Nunca coloque segredos em variaveis `VITE_*`, pois sao publicas no navegador.

## Organizacao

```text
src/
  components/exam/  Componentes especificos do simulado
  components/ui/    Componentes de apresentacao reutilizaveis
  contexts/         Contratos e contextos React
  hooks/            Hooks de consumo dos contextos
  layouts/          Estrutura de navegacao
  pages/            Telas associadas as rotas
  providers/        Estado compartilhado e ciclo da autenticacao
  services/         Contratos HTTP e cliente Axios
  styles/           Tema Material UI e estilos globais
  test/             Fixtures e configuracao dos testes unitarios
  types/            Tipos da API por dominio
  utils/            Funcoes puras e persistencia local
e2e/                Testes Playwright de desktop e celular
```

Pastas usam `kebab-case`. Componentes, paginas, layouts e providers usam
`PascalCase.tsx`; hooks usam `use-nome.ts` e exportam `useNome`;
servicos usam `dominio.service.ts`; tipos usam `dominio.types.ts`.
Funcoes e variaveis usam `camelCase`; interfaces e tipos usam `PascalCase`.
Classes CSS usam `kebab-case`. Componentes funcionais sao preferidos a classes.
Testes `*.test.ts(x)` ficam ao lado do modulo testado. Imports internos usam `@/`.
Nao adicionar abstrações, classes ou barrels sem uma necessidade concreta.

## Verificar

```sh
npm run type-check
npm run lint
npm run format:check
npm test
npm run build
npm run test:e2e
```

`npm run format` aplica a formatacao padronizada. Os testes E2E padrao usam
respostas controladas, sem banco. `QUIZFORGE_E2E_LIVE=1` habilita tambem o fluxo
com API real em `http://localhost:8082`. Esse fluxo requer a planilha gerada pelo
teste Java em `../quizforge-srv/target/validation-questions.xlsx`.
O Playwright inicia seu proprio Vite na porta 3001 com `.env.validation`.
Em uma maquina nova, execute `npx playwright install chromium` antes dos E2E.

## Comportamento

A home e as demais rotas privadas exigem token. A renovacao de sessao e unica
para chamadas simultaneas; falhas rejeitam todas as chamadas pendentes.
Perguntas, respostas e posicao do simulado sao salvas por usuario neste navegador.
O painel de perguntas pode ser recolhido e a barra de finalizacao fica fixa.
O cancelamento chama `DELETE /exams/{id}` antes de remover o progresso local.
Tentativas canceladas nao entram no historico ou nas estatisticas.
Resultados separam certas, erradas e nao respondidas, com filtros para cada grupo.
Ao alterar a senha, uma nova autenticacao e exigida.

O backend agora persiste as alternativas selecionadas. Respostas de tentativas
antigas que nunca foram persistidas nao podem ser recuperadas retroativamente.
