# Atualizações necessárias nos diagramas

> Os diagramas atuais (`Diagrama de classes.pdf`, `Diagrama de caso de uso.pdf`,
> `Diagrama MER.pdf` e `docs/database/Diagrama Modelo Entidade-Relacionamento.png`) são arquivos
> binários exportados de ferramenta de modelagem e **não podem ser editados diretamente no
> repositório**. Este documento descreve exatamente o que deve ser alterado em cada diagrama para
> refletir as funcionalidades adicionadas (RF12 – Pontos de Coleta; e status `INACTIVE` de
> beneficiários – RF11), para que a pessoa responsável aplique na ferramenta original e reexporte.

Data: 2026-10-20

---

## 1. Diagrama de Classes (`Diagrama de classes.pdf`)

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

## 2. Diagrama de Casos de Uso (`Diagrama de caso de uso.pdf`)

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

## 3. Diagrama MER / Entidade-Relacionamento (`Diagrama MER.pdf` e `docs/database/Diagrama Modelo Entidade-Relacionamento.png`)

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
> `composicao_familiar` (ver `docs/database/Tabelas e Relacionamentos - DDL.sql`, já alinhado ao
> código).

---

## Referências de implementação
- Backend: `backend/src/main/java/com/javalovers/core/collectionpoint/` (entidade, enum, DTOs,
  mapper, repository, specification, service, controller).
- Migrations: `backend/src/main/resources/db/migration/V8__create_collection_point.sql` e
  `V9__add_inactive_beneficiary_status.sql`.
- Frontend: `frontend/src/app/pontoscoleta/` e `frontend/src/app/components/map/CollectionPointsMap.js`.
- DDL/DML aligned: `docs/database/Tabelas e Relacionamentos - DDL.sql` e
  `docs/database/Seed Dados Tabelas - DML.sql`.

---

## RF19 – Notificação de estoque mínimo (2026-11-02)

### Diagrama de Classes
- `Item`: novo atributo `minimumStock: Long` (opcional) e operação derivada `isLowStock(): boolean`
  (`minimumStock != null && stockQuantity <= minimumStock`).
- Nova classe `StockAlert`: `stockAlertId`, `item: Item`, `stockQuantity: Long`,
  `minimumStock: Long`, `status: StockAlertStatus`, `createdAt`, `resolvedAt`;
  operações `resolve()`, `isOpen()`. Associação `StockAlert *——1 Item`.
- Novo enum `StockAlertStatus`: `OPEN`, `RESOLVED`.
- Nova classe `Notification`: `notificationId`, `user: AppUser`, `stockAlert: StockAlert`,
  `type: NotificationType`, `title`, `message`, `createdAt`, `readAt`; operação `markAsRead()`.
  Associações `Notification *——1 AppUser` e `Notification *——0..1 StockAlert`.
- Novo enum `NotificationType`: `LOW_STOCK`.
- Serviços: `StockAlertService.evaluate(Item)` (chamado por `InventoryService.processTransaction`
  e por `ItemService` ao criar/editar/excluir item) e `NotificationService`.

### Diagrama de Caso de Uso
- Novo caso de uso **"Receber alerta de estoque mínimo"** — atores Administrador e Funcionário;
  `<<extend>>` de "Registrar retirada", "Registrar doação" (estorno) e "Ajustar estoque".
- Novo caso de uso **"Configurar estoque mínimo do item"** — `<<include>>` em "Cadastrar/editar item".

### MER
- `item`: nova coluna `minimum_stock INT NULL`.
- Nova entidade `stock_alert` (FK `item_id` → `item`).
- Nova entidade `notification` (FK `user_id` → `app_user`, FK opcional `stock_alert_id` → `stock_alert`).
