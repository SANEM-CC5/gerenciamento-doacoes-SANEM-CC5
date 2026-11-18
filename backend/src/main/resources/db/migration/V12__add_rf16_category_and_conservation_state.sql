-- =====================================================================
-- V12: Atributos RF16 (categoria e estado de conservação) para itens
-- (PostgreSQL)
-- =====================================================================

-- Categorias padrão do sistema (catálogo de referência)
INSERT INTO category (name) VALUES
    ('Roupas'),
    ('Calçados'),
    ('Alimentos'),
    ('Higiene'),
    ('Utensílios Domésticos'),
    ('Cama, Mesa e Banho')
ON CONFLICT (name) DO NOTHING;

-- Adicionar colunas do RF16 (compatível com as colunas já criadas na V8)
ALTER TABLE item
    ADD COLUMN IF NOT EXISTS category_id INTEGER NULL,
    ADD COLUMN IF NOT EXISTS conservation_state VARCHAR(50) NULL;

ALTER TABLE item
    ALTER COLUMN size TYPE VARCHAR(50),
    ALTER COLUMN gender TYPE VARCHAR(50);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_item_category'
    ) THEN
        ALTER TABLE item
            ADD CONSTRAINT fk_item_category
            FOREIGN KEY (category_id) REFERENCES category(category_id)
            ON UPDATE RESTRICT ON DELETE SET NULL;
    END IF;
END $$;

-- Desvincular e remover categorias legadas em inglês, caso existam
UPDATE item
SET category_id = NULL
WHERE category_id IN (
    SELECT category_id FROM category WHERE name IN ('Food', 'Clothing', 'Hygiene')
);

DELETE FROM category WHERE name IN ('Food', 'Clothing', 'Hygiene');
