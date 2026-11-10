# Gerenciamento de Doações SANEM

## 📖 Descrição

O sistema tem como objetivo o gerenciamento de um brechó beneficente que distribui roupas, calçados e utensílios a pessoas necessitadas, tanto por meio de controle de estoque, beneficiários e funcionários, quanto por meio da geração de relatórios pertinentes ao sistema.

## 🎯 Sobre o Sistema

### Principais Funcionalidades

#### 👥 Gestão de Beneficiários

- Cadastrar, aprovar e consultar o histórico de pessoas e famílias atendidas
- Emitir cartões de identificação para agilizar o atendimento
- Controle completo do perfil dos beneficiários
- Inativar (bloquear) e reativar beneficiários já aprovados sem remover o cadastro (status `INACTIVE`)

#### 📍 Pontos de Coleta e Sedes

- Cadastrar, consultar, editar e inativar pontos de coleta parceiros e sedes de entidades assistenciais em Medianeira
- Registrar endereço e horário de funcionamento de cada local
- Visualizar todas as localizações em um mapa interativo (Leaflet + OpenStreetMap)

#### 📣 Demandas Prioritárias (RF20)

- Cadastrar, consultar, atualizar e inativar demandas prioritárias por itens do estoque
- Acompanhar o progresso "recebido X de Y", calculado automaticamente a partir das doações
- Destaque das demandas ativas na Home e na tela de registro de doação

#### 📦 Controle de Doações e Estoque

- Registrar os itens recebidos em doação
- Catalogar produtos com códigos únicos
- Gerenciar a quantidade de produtos disponíveis no estoque
- Controle de entrada e saída de itens

#### 🎁 Distribuição de Itens

- Registrar a retirada de itens por um beneficiário
- Baixa automática no estoque
- Aplicação de regras como limite mensal de retiradas
- Controle de distribuição por período

#### 👨‍💼 Administração e Relatórios

- Gerenciar usuários com diferentes permissões (Atendente e Administrador)
- Gerar relatórios detalhados sobre doações
- Relatórios de distribuições e posição do estoque
- Análise completa da operação

## 🚀 Tecnologias Utilizadas

### Backend

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL (NeonDB)** - Banco de dados (hospedado na nuvem)
- **JWT** - Tokens de autenticação
- **Maven** - Gerenciamento de dependências

### Frontend

- **React 18**
- **React Router DOM** - Roteamento
- **Styled Components** - Estilização
- **React Hook Form** - Gerenciamento de formulários
- **React Query** - Gerenciamento de estado do servidor
- **Axios** - Cliente HTTP
- **React Icons** - Ícones
- **Leaflet / React-Leaflet** - Mapa interativo dos pontos de coleta (OpenStreetMap)

### DevOps

- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers

## 📁 Estrutura do Projeto

```
JavaLovers/
├── backend/                    # Backend Java Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/javalovers/
│   │   │   │   ├── JavaLoversApplication.java
│   │   │   │   ├── common/                 # Config, segurança, utilidades, soft delete
│   │   │   │   └── core/                   # Módulos de domínio
│   │   │   │       ├── beneficiary/        # Beneficiários (CRUD + status INACTIVE)
│   │   │   │       ├── collectionpoint/    # Pontos de coleta e sedes (NOVO)
│   │   │   │       ├── donor/
│   │   │   │       └── ...
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/           # Migrations Flyway (V2..V9)
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                   # Frontend Next.js (App Router)
│   ├── public/
│   ├── src/
│   │   ├── app/
│   │   │   ├── cadastrobeneficiario/       # Beneficiários
│   │   │   ├── pontoscoleta/               # Pontos de coleta + mapa (NOVO)
│   │   │   │   ├── lista/                   # Mapa interativo + tabela
│   │   │   │   ├── editar/[id]/
│   │   │   │   └── PontoColetaForm.js
│   │   │   └── components/
│   │   │       ├── map/CollectionPointsMap.js  # Mapa Leaflet (NOVO)
│   │   │       └── navegation/
│   │   ├── services/api.js
│   │   └── hooks/
│   ├── package.json
│   └── Dockerfile
├── Banco de Dados/            # Scripts do banco de dados
│   ├── Diagrama MER.pdf
│   ├── script-create-tables.sql
│   └── script-insert-values.sql
├── docker-compose.yml         # Orquestração dos containers
├── .gitignore
└── README.md
```

## 🛠️ Configuração e Execução

### Pré-requisitos

- Java 17 ou superior
- Node.js 18 ou superior
- Uma conta/banco PostgreSQL no [NeonDB](https://neon.tech) (connection string)
- Docker e Docker Compose (opcional)

### Execução com Docker (Recomendado)

1. **Clone o repositório**

    ```bash
    git clone <url-do-repositorio>
    cd gerenciamento-doacoes-SANEM-CC5
    ```

2. **Configure as credenciais do banco (NeonDB)**
    - Copie `.env.example` para `.env` e preencha com a connection string do Neon
      (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
    - O `.env` não é versionado (está no `.gitignore`).

3. **Execute com Docker Compose**

    ```bash
    docker-compose up -d --build
    ```

    A Flyway cria o esquema e aplica o seed no banco Neon automaticamente no startup do backend.

4. **Acesse a aplicação**
    - Frontend: http://localhost:3000
    - Backend API: http://localhost:8080/api
    - Banco: PostgreSQL hospedado no NeonDB (nuvem)

### Execução Manual

#### Backend

1. **Navegue para a pasta do backend**

    ```bash
    cd backend
    ```

2. **Configure o banco de dados (NeonDB)**
    - Crie um projeto/banco PostgreSQL no NeonDB e copie a connection string.
    - Defina as variáveis de ambiente (ou use o `.env` na raiz):
      `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
    - A Flyway cria o esquema e aplica o seed automaticamente na primeira execução.

3. **Execute a aplicação**

    ```bash
    export SPRING_DATASOURCE_URL="jdbc:postgresql://<host-neon>/<db>?sslmode=require"
    export SPRING_DATASOURCE_USERNAME="<usuario>"
    export SPRING_DATASOURCE_PASSWORD="<senha>"

    mvn clean package
    java -jar target/javalovers-backend-1.0.0.jar
    ```

#### Frontend

1. **Navegue para a pasta do frontend**

    ```bash
    cd frontend
    ```

2. **Instale as dependências**

    ```bash
    npm install
    ```

3. **Execute a aplicação**
    ```bash
    npm start
    ```

## 🔧 Configuração do Banco de Dados

O banco é **PostgreSQL hospedado no NeonDB**. Não há banco local: o backend conecta direto no
Neon, e o esquema + seed são aplicados pela **Flyway** no startup
(`backend/src/main/resources/db/migration/V1__init_schema.sql` e `V2__seed_data.sql`).

### Configuração das Credenciais

As credenciais vêm de variáveis de ambiente (nunca commitadas). Copie `.env.example` para `.env`
e preencha com os dados do Neon:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host-neon>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<usuario-neon>
SPRING_DATASOURCE_PASSWORD=<senha-neon>
```

> O Neon **exige SSL** — mantenha `?sslmode=require` na URL.
> O `application.yml` já lê essas variáveis (com fallback para um Postgres local em dev).

### Dados de exemplo (dev) x produção

O comportamento do seed é controlado pelo profile do Spring (`SPRING_PROFILES_ACTIVE`):

- **`dev`** — a Flyway aplica o schema (`db/migration`) **e** o seed de exemplo (`db/seed`:
  usuários, doadores, beneficiários, pontos de coleta). Use no banco de desenvolvimento do Neon.
- **`prod`** (ou qualquer valor diferente de `dev`) — a Flyway aplica **somente o schema**;
  nenhum dado é inserido na primeira subida. Os usuários/dados são cadastrados manualmente.

Assim, o banco de **produção** do Neon sobe limpo, e o de **desenvolvimento** já vem populado
para testes. (A configuração de limite de retirada é criada automaticamente pela aplicação quando
necessária, então produção funciona sem seed.)

## 📚 Funcionalidades

### Implementadas

- ✅ Estrutura base do backend Spring Boot
- ✅ Configuração de segurança com Spring Security
- ✅ Interface React moderna e responsiva
- ✅ Páginas de Login e Cadastro
- ✅ Dashboard do usuário
- ✅ Configuração de CORS
- ✅ Containerização com Docker

### Em Desenvolvimento

#### 👥 Gestão de Beneficiários

- 🔄 CRUD completo de beneficiários
- 🔄 Sistema de aprovação de cadastros
- 🔄 Inativação/reativação de beneficiários aprovados (status `INACTIVE`)
- 🔄 Geração de cartões de identificação
- 🔄 Histórico de atendimentos

#### 📍 Pontos de Coleta e Sedes

- 🔄 CRUD de pontos de coleta parceiros e sedes de entidades assistenciais
- 🔄 Endereço e horário de funcionamento por local
- 🔄 Mapa interativo (Leaflet + OpenStreetMap) com as localizações

#### 📦 Controle de Estoque

- 🔄 Cadastro de itens doados
- 🔄 Sistema de códigos únicos
- 🔄 Controle de entrada e saída
- 🔄 Alertas de estoque baixo

#### 🎁 Sistema de Distribuição

- 🔄 Registro de retiradas
- 🔄 Controle de limites mensais
- 🔄 Baixa automática no estoque
- 🔄 Validação de regras de distribuição

#### 👨‍💼 Administração

- 🔄 Sistema de permissões (Atendente/Administrador)
- 🔄 Gestão de usuários
- 🔄 Relatórios de doações
- 🔄 Relatórios de distribuições
- 🔄 Dashboard administrativo

#### 🔧 Infraestrutura

- 🔄 Sistema de autenticação JWT
- 🔄 Integração frontend-backend
- 🔄 Validação de formulários
- 🔄 Testes unitários e de integração

## 🧪 Testes

### Backend

```bash
cd backend
mvn test
```

### Frontend

```bash
cd frontend
npm test
```

## 📦 Build para Produção

### Backend

```bash
cd backend
mvn clean package
```

### Frontend

```bash
cd frontend
npm run build
```
