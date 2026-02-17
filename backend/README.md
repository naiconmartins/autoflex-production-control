# Autoflex Production Control - Backend API

API backend para controle de produção usando **Java 21 + Quarkus + PostgreSQL**.

## Escopo do projeto

Este serviço gerencia:
- Produtos
- Matérias-primas
- Associação produto x matéria-prima (BOM)
- Sugestão de capacidade de produção com base no estoque atual, priorizando produtos de maior valor

## Stack tecnológica

- Java 21
- Quarkus 3.x
- Hibernate ORM com Panache
- PostgreSQL
- Autenticação JWT (SmallRye JWT)
- Maven
- Testes unitários e de integração com JUnit + RestAssured

## Arquitetura

O backend segue **Arquitetura Hexagonal (Ports and Adapters)** para separar regra de negócio de detalhes de entrada/saída.

### Núcleo da aplicação

- `org.autoflex.domain`: entidades e comportamentos de domínio
- `org.autoflex.application.commands`: contratos de entrada dos casos de uso com validações
- `org.autoflex.application.usecases`: portas de entrada (contratos da aplicação)
- `org.autoflex.application.services`: implementação dos casos de uso
- `org.autoflex.application.gateways`: portas de saída (contratos para persistência, segurança e infraestrutura)

### Adaptadores

- `org.autoflex.adapters.inbound`:
  - `resources`: endpoints REST
  - `dto`: contratos HTTP (request/response)
  - `mappers`: conversão entre HTTP e aplicação
- `org.autoflex.adapters.outbound`:
  - `persistence.jpa`: implementação dos gateways com JPA/Panache

### Tratamento de erros

- `org.autoflex.common.exceptions`: exceções de negócio/aplicação
- `org.autoflex.common.mappers`: mapeamento de exceções para respostas HTTP padronizadas

### Fluxo resumido

`Resource -> Mapper -> UseCase(Service) -> Gateway -> Adapter Outbound (JPA) -> Banco`

## Requisitos funcionais (Backend)

- `RF001` CRUD de produtos: implementado em `ProductResource`
- `RF002` CRUD de matérias-primas: implementado em `RawMaterialResource`
- `RF003` CRUD de associação produto/matéria-prima: implementado em `ProductRawMaterialResource`
- `RF004` Consulta de produtos produzíveis com estoque disponível: implementado em `ProductionCapacityResource`

## Requisitos não funcionais (Backend)

- `RNF002` Arquitetura API: backend exposto como REST API
- `RNF004` Persistência em banco relacional: PostgreSQL via JDBC
- `RNF005` Framework backend: Quarkus
- `RNF007` Código, tabelas e colunas em inglês

Observação: requisitos de frontend (`RNF001`, `RNF003`, `RNF006`, `RF005`-`RF008`) são atendidos na aplicação frontend.

## Regra de negócio da capacidade de produção

Endpoint `GET /production-capacity`:
- Ordena os produtos pelo **maior preço**
- Calcula quantas unidades podem ser produzidas com o estoque disponível
- Consome estoque de forma simulada conforme a alocação dos produtos
- Retorna quantidade produzível por produto e valor total geral

## Endpoints da API

### Auth
- `POST /auth/login`

### Usuários
- `POST /user` (ADMIN)
- `GET /user/me` (USER, ADMIN)

### Produtos
- `POST /products`
- `PUT /products/{id}`
- `DELETE /products/{id}`
- `GET /products`
- `GET /products/search?name=...`
- `GET /products/{id}`

### Matérias-primas
- `POST /raw-materials`
- `PUT /raw-materials/{id}`
- `DELETE /raw-materials/{id}`
- `GET /raw-materials`
- `GET /raw-materials/search?name=...`
- `GET /raw-materials/{id}`

### Associação Produto x Matéria-prima
- `POST /products/{productId}/raw-materials`
- `GET /products/{productId}/raw-materials`
- `PUT /products/{productId}/raw-materials/{rawMaterialId}`
- `DELETE /products/{productId}/raw-materials/{rawMaterialId}`

### Plano de produção
- `GET /production-capacity`

## Segurança

- JWT obrigatório nas rotas protegidas
- Rota pública: `POST /auth/login`
- Autorização por perfil com `@RolesAllowed`

## Como executar localmente

### 1. Subir em modo desenvolvimento

```bash
./mvnw quarkus:dev
```

URL padrão da API: `http://localhost:8080`

### 2. Carga inicial de dados

Os dados de seed são carregados por:
- `src/main/resources/import.sql`

## Postman Collection

Link da collection no repositório:
- [autoflex.postman_collection.json](./autoflex.postman_collection.json)

Ela inclui:
- Todas as rotas do backend
- Variáveis de ambiente (`baseUrl`, ids, credenciais)
- Script automático no `Auth > Login` para salvar o JWT no ambiente:

```javascript
if (responseCode.code >= 200 && responseCode.code < 300) {
   var json = JSON.parse(responseBody);
   postman.setEnvironmentVariable('token', json.accessToken);
}
```

Credenciais padrão na collection:
- `adminEmail = adm@autoflex.com`
- `adminPassword = adm`

## Testes

Rodar testes unitários:

```bash
./mvnw test
```

Rodar testes de integração:

```bash
./mvnw verify -DskipITs=false
```

## Build

Gerar pacote da aplicação:

```bash
./mvnw package
```

Executar aplicação empacotada:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```
