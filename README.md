# Autoflex Production Control

> ⚠️ **Projeto em Desenvolvimento** - Este projeto ainda está em fase de desenvolvimento.
> 
> **Pendências:**
> - Sistema de recomendação de produção
> - Testes de integração no backend
> - Testes unitários e de integração no frontend

Uma aplicação web moderna para gerenciamento de estoque industrial, controle de matérias-primas e otimização do planejamento de produção baseado no estoque disponível.

## Sobre o Projeto

Este sistema auxilia indústrias no controle de estoque de produção, gerenciando produtos, matérias-primas e suas relações. Oferece sugestões inteligentes de produção com base nos níveis de estoque atuais, priorizando produtos de maior valor para maximizar o resultado da produção.

## Funcionalidades

- **Gerenciamento de Produtos**: Operações CRUD completas para produtos incluindo código, nome e valor
- **Gerenciamento de Matérias-Primas**: Controle de matérias-primas com código, nome e quantidades em estoque
- **Composição de Produtos**: Associação de matérias-primas aos produtos e definição das quantidades necessárias
- **Planejamento de Produção**: Cálculo automático de quais produtos podem ser fabricados com o estoque disponível
- **Otimização por Valor**: Priorização das sugestões de produção pelo valor dos produtos
- **Autenticação de Usuários**: Acesso seguro com autenticação via Bearer token

## Tecnologias Utilizadas

### Backend
- **Quarkus**: Framework Java de alta performance
- **PostgreSQL**: Banco de dados relacional robusto
- **Autenticação JWT**: Segurança com Bearer token
- **API RESTful**: Separação completa entre backend e frontend
- **Testes Unitários**: Cobertura abrangente de testes

### Frontend
- **Next.js**: Framework React moderno
- **Redux**: Gerenciamento de estado
- **Zod**: Validação de schemas
- **shadcn/ui**: Componentes de interface bonitos e acessíveis
- **Design Responsivo**: Funciona perfeitamente em todos os dispositivos

## Como Começar

### Pré-requisitos
- Java 21+
- Node.js 18+
- PostgreSQL

### Configuração do Backend
```bash
cd backend
./mvnw quarkus:dev
```

### Configuração do Frontend
```bash
cd frontend
npm install
npm run dev
```

## Arquitetura

A aplicação segue uma arquitetura moderna API-first com separação completa entre backend e frontend, permitindo escalabilidade e implantação independente de cada camada.

## Licença

MIT