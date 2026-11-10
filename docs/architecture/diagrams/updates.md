# Atualizações necessárias nos diagramas

> Os diagramas atuais (`class-diagram.pdf`, `use-case-diagram.pdf`,
> `docs/database/entity-relationship-diagram.pdf` e
> `docs/database/entity-relationship-diagram.png`) são arquivos
> binários exportados de ferramenta de modelagem e **não podem ser editados diretamente no
> repositório**. Este documento descreve exatamente o que deve ser alterado em cada diagrama para
> refletir as funcionalidades adicionadas (RF12 – Pontos de Coleta; e status `INACTIVE` de
> beneficiários – RF11), para que a pessoa responsável aplique na ferramenta original e reexporte.

Data: 2026-10-20

---

## 1. Diagrama de Classes (`class-diagram.pdf`)

### Adicionar a classe `CollectionPoint`
Atributos:
- `collectionPointId: Long`
- `name: String`
- `type: CollectionPointType`
- `street: String`
- `number: String`
- `neighborhood: String`
- `city: String`
- `state: String`
- `zipCode: String`
- `latitude: Double`
- `longitude: Double`
- `operatingHours: String`
- `phone: String`
- `deletedAt: LocalDateTime` (soft delete — implementa a interface `SoftDeletable`, como as
  demais entidades)

Operações (via camada de serviço `CollectionPointService`): criar, listar/consultar, atualizar e
inativar (soft delete).

### Adicionar o enum `CollectionPointType`
Valores: `PARTNER_COLLECTION_POINT`, `ASSISTANCE_ENTITY_HQ`.
Associação: `CollectionPoint` —— `type` ——> `CollectionPointType`.

> `CollectionPoint` é uma entidade independente: **não possui relacionamentos** com Beneficiário,
> Doação, Estoque etc.

### Alterar o enum `BeneficiaryStatus`
Adicionar o valor `INACTIVE` à enumeração existente
(`PENDING`, `APPROVED`, `REJECTED`, **`INACTIVE`**).

---

## 2. Diagrama de Casos de Uso (`use-case-diagram.pdf`)

Atores: **Administrador** e **Funcionário** (ambos autenticados).

### Novos casos de uso (associados a Administrador e Funcionário)
- **Cadastrar ponto de coleta / sede**
- **Consultar pontos de coleta no mapa** (visualização das localizações em mapa interativo)
- **Editar ponto de coleta / sede**
- **Inativar ponto de coleta / sede** (exclusão lógica)

### Novos casos de uso de beneficiário (associados ao Administrador)
- **Inativar beneficiário aprovado** (bloquear, alterando status para `INACTIVE`)
- **Reativar beneficiário inativo** (status volta para `APPROVED`)

Esses dois complementam os casos já existentes de "Aprovar" e "Reprovar" cadastro (RF11).

---

## 3. Diagrama MER / Entidade-Relacionamento (`docs/database/entity-relationship-diagram.pdf` e `docs/database/entity-relationship-diagram.png`)

### Adicionar a entidade/tabela `collection_point`
Colunas:
- `collection_point_id` (PK, BIGINT, auto incremento)
- `name` (VARCHAR, NOT NULL)
- `type` (ENUM: `PARTNER_COLLECTION_POINT`, `ASSISTANCE_ENTITY_HQ`, NOT NULL)
- `street`, `number`, `neighborhood`, `city`, `state`, `zip_code` (VARCHAR)
- `latitude` (DOUBLE, NOT NULL)
- `longitude` (DOUBLE, NOT NULL)
- `operating_hours` (VARCHAR)
- `phone` (VARCHAR)
- `deleted_at` (TIMESTAMP, nulo — soft delete)

> Entidade **sem chaves estrangeiras** (independente das demais tabelas).

### Ajustar a tabela `beneficiary`
O domínio de `beneficiary_status` passa a incluir `INACTIVE`:
`ENUM('PENDING', 'APPROVED', 'REJECTED', 'INACTIVE')`.

> Observação: o diagrama deve refletir o esquema **realmente implementado** (MySQL). A tabela
> `beneficiary` **não** possui as colunas `address`, `data_nascimento`, `sexo` ou
> `composicao_familiar` (ver `docs/database/schema.sql`, já alinhado ao
> código).

---

## Referências de implementação
- Backend: `backend/src/main/java/com/javalovers/modules/collectionpoint/` (camadas
  `domain`, `application` e `infrastructure`).
- Migrations: `backend/src/main/resources/db/migration/V8__create_collection_point.sql` e
  `V9__add_inactive_beneficiary_status.sql`.
- Frontend: `frontend/src/features/collection-points/`, com a rota pública preservada em
  `frontend/src/app/pontoscoleta/`.
- DDL/DML aligned: `docs/database/schema.sql` e
  `docs/database/seed-data.sql`.
