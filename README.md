# Autoflex Production Control

Solução do teste prático da Autoflex para controle de produção industrial, com arquitetura `API-first` separando backend e frontend.

## Status

Desafio concluído, incluindo:
- CRUD de produtos (`RF001` + `RF005`)
- CRUD de matérias-primas (`RF002` + `RF006`)
- Associação produto x matéria-prima (`RF003` + `RF007`)
- Cálculo de capacidade de produção priorizando maior valor (`RF004` + `RF008`)
- Testes unitários e de integração no backend
- Testes de componente e E2E no frontend (Cypress)

## Arquitetura do Monorepo

- `backend/`: API em Java 21 + Quarkus + PostgreSQL + JWT, seguindo arquitetura hexagonal
- `frontend/`: aplicação web em Next.js 16 + React 19 + Redux Toolkit + Cypress

Documentação detalhada por módulo:
- Backend: [`backend/README.md`](backend/README.md)
- Frontend: [`frontend/README.md`](frontend/README.md)

## Requisitos não funcionais atendidos

- `RNF001`: aplicação web compatível com principais navegadores
- `RNF002`: separação entre backend e frontend via API REST
- `RNF003`: frontend responsivo
- `RNF004`: persistência em banco relacional (PostgreSQL)
- `RNF005`: backend com framework (Quarkus)
- `RNF006`: frontend com React/Redux
- `RNF007`: código e estrutura em inglês

## Como executar localmente

### Pré-requisitos

- Java 21+
- Node.js 20+
- pnpm
- PostgreSQL disponível para o backend

### 1) Backend

```bash
cd backend
./mvnw quarkus:dev
```

API padrão: `http://localhost:8080`

Observações:
- A configuração e credenciais do banco estão em `backend/src/main/resources/application.properties`.
- Os dados iniciais são carregados por `backend/src/main/resources/import.sql`.

### 2) Frontend

```bash
cd frontend
cp env-example .env
pnpm install
pnpm dev
```

Aplicação padrão: `http://localhost:3000`

## Testes

### Backend

```bash
cd backend
./mvnw test
./mvnw verify -DskipITs=false
```

### Frontend

```bash
cd frontend
npx cypress run --component
npx cypress run --e2e
```

## Credenciais padrão (seed local)

- Email: `adm@autoflex.com`
- Senha: `adm`

## Screenshots

### Login
![Login](frontend/docs/screenshots/login.png)

### Production Capacity
![Production Capacity](frontend/docs/screenshots/production-capacity.png)
