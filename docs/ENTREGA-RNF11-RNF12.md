# Entrega da equipe – RNF11 e RNF12 (e correção da `main`)

Cada item está em uma **branch própria** e é enviado em um **Pull Request separado**.

| Ordem | Branch | O que faz |
|---|---|---|
| 1 | `fix/docs-conflitos-e-edicao-item` | Completa a correção da `main` (commit `feba0e7`): resolve as marcas de conflito que restaram na documentação e corrige a edição de item |
| 2 | `feat/rnf11-tour-guiado` | **RNF11** – tour guiado das principais operações e dicas iniciais (criada a partir da branch 1) |
| 3 | `feat/rnf12-criptografia-beneficiarios` | **RNF12** – criptografia dos documentos e dados pessoais dos beneficiários (criada a partir da branch 1) |

As branches 2 e 3 são independentes entre si. Elas partem da branch 1 porque cada uma acrescenta seu RNF em
`docs/requisitos.md`, arquivo que na `main` ainda tinha marcas de conflito.

---

## 1. Ajustes pendentes da `main` (pré-requisito)

A `main` atual (`feba0e7`) já resolveu os conflitos de código entre o RF19 (estoque mínimo) e a V8 (atributos de
roupa) e já converteu as migrations V10 e V11 para PostgreSQL. Ficaram três pendências:

- **`docs/requisitos.md`**: ainda tinha marcas de conflito (`<<<<<<< HEAD`). Resolvido mantendo RF19 e RF20.
- **`docs/database/Tabelas e Relacionamentos - DDL.sql`**: tinha marcas de conflito e as tabelas do RF19/RF20 em
  sintaxe MySQL. Resolvido e convertido para PostgreSQL; o arquivo agora executa inteiro no PostgreSQL.
- **`estoque/editar/[id]`**: ao salvar, apagava tamanho, gênero, tipo, estado e localização do item. Agora preserva.

As migrations V10 e V11 da `main` **não foram alteradas**: elas podem já ter sido aplicadas no banco da equipe, e
qualquer mudança nelas faria o Flyway recusar a inicialização.

---

## 2. RNF11 – Interface de fácil operação para voluntários

> A interface gráfica deve ser de fácil operacionalização para voluntários sem treinamento avançado.

**Plugin utilizado:** [driver.js](https://driverjs.com) (licença MIT, sem dependências). Foi escolhido no lugar de
Shepherd.js e Intro.js porque estes usam licença AGPL.

**O que foi feito**
- **Boas-vindas com dicas iniciais:** no primeiro acesso à Home, uma janela apresenta quatro dicas e convida para o tour.
- **Tour geral:** apresenta o menu, Doações, Retirada, Estoque, Beneficiários, Demandas, o sino de avisos e a Ajuda.
- **Tours por tela:** Estoque, Registrar doação, Registrar retirada, Beneficiários, Doadores, Demandas e as listas de
  Doações e Retiradas.
- **Dica de primeira visita:** ao abrir uma dessas telas pela primeira vez, aparece o cartão "Primeira vez aqui?" com
  uma dica curta e o botão "Ver tour desta tela".
- **Botão "Tour" no topo:** reabre, a qualquer momento, o tour da tela atual.
- **Ajuda → Tours guiados:** lista todos os tours (abre a tela certa e já inicia) e tem o botão "Reexibir dicas iniciais".
- O progresso fica salvo **por usuário** no navegador: cada voluntário vê as dicas uma única vez.
- Acessibilidade: navegação por teclado (setas, Esc), foco visível e respeito a "reduzir movimento" do sistema.

**Arquivos principais:** `frontend/src/components/onboarding/` (roteiros em `tours.js`). As telas só receberam
atributos `data-tour="..."`, que não mudam visual nem comportamento.

---

## 3. RNF12 – Criptografia dos dados pessoais dos beneficiários

> Os documentos pessoais dos beneficiários devem ser criptografados e cadastrados em banco de dados (conforme LGPD).

### Situação encontrada
`beneficiary.cpf`, `phone` e `socioeconomic_data` eram gravados **em texto puro**. O campo socioeconômico dos dados
de exemplo traz informações como "Acamado" e "Idoso, recebe BPC", que podem ser **dado sensível de saúde**.

### Fundamentação (Lei 13.709/2018 – LGPD)
- **Art. 5º, I e II:** CPF e telefone são dados pessoais; dados de saúde são dados pessoais **sensíveis**.
- **Art. 6º, VII (princípio da segurança):** o agente de tratamento deve usar medidas técnicas e administrativas para
  proteger os dados contra acessos não autorizados.
- **Art. 46:** obriga a adoção dessas medidas, e o §2º exige que sejam pensadas desde a concepção do sistema
  (*privacy by design*).
- **Art. 48, §3º:** ao avaliar a gravidade de um incidente, considera-se se os dados estavam **ininteligíveis** para
  terceiros. A criptografia é justamente o que torna um vazamento do banco ininteligível.
- **ANPD – Guia de Segurança da Informação para Agentes de Tratamento de Pequeno Porte:** recomenda criptografia
  dos dados armazenados.

Observação: a LGPD não obriga uma técnica específica; ela exige medidas **adequadas**. A criptografia é a medida
técnica recomendada para dados pessoais e sensíveis armazenados.

### O que foi feito
- **AES-256-GCM** (confidencialidade e integridade) para CPF/CRNM, telefone e dados socioeconômicos. O banco guarda
  `enc:v1:...`; a aplicação continua trabalhando com o valor em claro, sem nenhuma mudança nas telas.
- **Índice cego (`cpf_hash`, HMAC-SHA256):** permite buscar CPF exato e manter a regra de **CPF único**, sem guardar o
  CPF em claro. A antiga restrição `UNIQUE (cpf)` foi trocada por `UNIQUE (cpf_hash)`. CPF duplicado agora gera a
  mensagem "Já existe um beneficiário cadastrado com este CPF/CRNM.".
- **Migration V12:** ajusta as colunas e cria o índice.
- **Dados antigos:** na inicialização, a aplicação criptografa automaticamente os registros que ainda estão em texto
  puro (`PersonalDataEncryptionMigrator`). O processo é idempotente e não grava os valores em log.
- **Chave fora do banco e do código:** variável `SANEM_ENCRYPTION_KEY`. Sem ela a aplicação **não inicia**. O perfil
  `dev` tem uma chave de desenvolvimento pública, só para testes.
- **Busca:** CPF completo é buscado no banco pelo índice; buscas parciais (parte do CPF, telefone) são feitas sobre os
  valores já descriptografados.
- O nome continua em claro: ele é necessário para a operação diária (busca, carteirinha, relatórios) e está protegido
  pelo controle de acesso (RNF02).

**Testes:** `FieldEncryptorTest` (11 testes): cifra aleatória, ida e volta com acentos, detecção de adulteração,
chave errada, índice determinístico e normalização de CPF com ou sem máscara.

---

## Como rodar localmente

**Pré-requisitos:** Git e Docker Desktop aberto.

> Use um **banco local** para avaliar as branches. Se apontar para o banco compartilhado da equipe no Neon, as
> migrations V10, V11 e V12 serão aplicadas nele, e quem estiver em uma branch antiga não conseguirá iniciar o backend.

### 1. Banco PostgreSQL local
```bash
docker run -d --name sanem-postgres -e POSTGRES_DB=sanem -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:16
```

### 2. Arquivo `.env` na raiz do projeto
```
SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/sanem
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=dev
```
No perfil `dev` não é preciso informar `SANEM_ENCRYPTION_KEY`.

### 3. Subir a aplicação
```bash
git checkout feat/rnf11-tour-guiado        # ou feat/rnf12-criptografia-beneficiarios
docker compose up -d --build
```
Frontend: http://localhost:3000 · API: http://localhost:8080/api

### 4. Ao trocar de branch, recrie o banco
O RNF12 tem a migration V12, e as outras branches não. Para evitar erro do Flyway:
```bash
docker compose down
docker rm -f sanem-postgres
# repita o passo 1 e depois o passo 3
```

### 5. Testes automatizados
```powershell
docker run --rm -v "${PWD}\backend:/app" -w /app maven:3.9-eclipse-temurin-17 mvn test
```
Esperado: `BUILD SUCCESS` – 17 testes na branch do RNF11 e 28 na do RNF12.

### Usuários de exemplo (perfil `dev`)
| Usuário | Senha | Perfil |
|---|---|---|
| `isadora` | `Isadora@2026` | Administrator |
| `isis` | `Isis@2026` | Attendant |
| `victor` | `Victor@2026` | Evaluator |

---

## Roteiro de demonstração

### RNF11 (branch `feat/rnf11-tour-guiado`)
1. Entre como **isadora**: aparece a janela de boas-vindas com as dicas. Clique em **"Fazer o tour guiado"** e avance pelos passos.
2. Abra **Estoque**: aparece o cartão "Primeira vez em Estoque?". Clique em **"Ver tour desta tela"**.
3. Em qualquer tela, clique em **"Tour"** no topo: o tour daquela tela recomeça.
4. Abra **Ajuda → Tours guiados** e clique em **"Registrar retirada"**: o sistema abre a tela e inicia o tour.
5. Para repetir a demonstração do zero, clique em **"Reexibir dicas iniciais"** na Ajuda, ou entre com outro usuário
   (ex.: **isis**). Cada usuário tem as próprias dicas.

### RNF12 (branch `feat/rnf12-criptografia-beneficiarios`)
1. Confira no log que os dados de exemplo foram criptografados na inicialização:
   ```bash
   docker logs javalovers-backend 2>&1 | findstr RNF12        # Linux/macOS: grep RNF12
   ```
   Esperado: `RNF12: dados pessoais de 10 beneficiário(s) foram criptografados.`
2. Veja como os dados ficam **no banco**:
   ```bash
   docker exec -it sanem-postgres psql -U postgres -d sanem -c "SELECT full_name, left(cpf,35) AS cpf, left(phone,25) AS telefone, left(cpf_hash,16) AS cpf_hash FROM beneficiary LIMIT 5;"
   ```
   O CPF e o telefone aparecem como `enc:v1:...`.
3. No sistema, abra **Beneficiários**: os CPFs aparecem normalmente, porque a aplicação descriptografa ao ler.
4. Filtre por parte do CPF (ex.: `873`) e depois pelo CPF completo: as duas buscas funcionam.
5. Tente cadastrar um beneficiário com um CPF que já existe: aparece "Já existe um beneficiário cadastrado com este CPF/CRNM.".
6. Cadastre um beneficiário novo e repita a consulta do passo 2: ele também está criptografado.
7. **Comparação (antes):** na branch do RNF11, a mesma consulta mostra o CPF e o telefone em texto puro.

---

## Ao juntar tudo na `main`
- Aprove primeiro a correção (branch 1). Os PRs do RNF11 e do RNF12 usam a branch 1 como base; depois que ela for
  mergeada e excluída, o GitHub muda a base deles para a `main` automaticamente.
- Único conflito previsto entre RNF11 e RNF12: uma linha em `docs/requisitos.md` (cada um acrescenta seu RNF depois do
  RNF07). Para resolver, mantenha as duas linhas.
- **Produção:** antes de subir o RNF12, gere a chave (`openssl rand -base64 32`), configure `SANEM_ENCRYPTION_KEY` no
  ambiente e **guarde-a em local seguro**. Sem a chave, os dados criptografados não podem ser recuperados.
