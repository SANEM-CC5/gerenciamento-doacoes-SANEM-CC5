USE sanem;

-- Inserir categorias padrao se nao existirem
INSERT IGNORE INTO category (name) VALUES 
('Roupas'),
('Calçados'),
('Alimentos'),
('Higiene'),
('Utensílios Domésticos'),
('Cama, Mesa e Banho');

-- Adicionar colunas do RF16 à tabela item
ALTER TABLE item
    ADD COLUMN category_id INT NULL,
    ADD COLUMN size VARCHAR(50) NULL,
    ADD COLUMN conservation_state VARCHAR(50) NULL,
    ADD COLUMN gender VARCHAR(50) NULL;

-- Adicionar chave estrangeira
ALTER TABLE item
    ADD CONSTRAINT fk_item_category
    FOREIGN KEY (category_id) REFERENCES category(category_id)
    ON UPDATE RESTRICT ON DELETE SET NULL;

-- Atualizar itens já existentes com dados condizentes para filtros
UPDATE item SET category_id = (SELECT category_id FROM category WHERE name = 'Alimentos' LIMIT 1), conservation_state = 'Novo', gender = 'Não aplicável' WHERE description LIKE '%Arroz%' OR description LIKE '%Feijão%' OR description LIKE '%Macarrão%' OR description LIKE '%Óleo%' OR description LIKE '%Leite%';
UPDATE item SET category_id = (SELECT category_id FROM category WHERE name = 'Higiene' LIMIT 1), conservation_state = 'Novo', gender = 'Não aplicável' WHERE description LIKE '%Sabonete%' OR description LIKE '%Creme Dental%' OR description LIKE '%Fralda%';
UPDATE item SET size = 'M' WHERE description LIKE '%Fralda%';

-- Adicionar itens variados de vestuário e calçados para teste dos filtros
INSERT IGNORE INTO item (description, stock_quantity, tag_code, category_id, size, conservation_state, gender) VALUES
('Camiseta Algodão Básica', 35, 'ETQ-ROU-001', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), 'M', 'Seminovo', 'Unissex'),
('Calça Jeans Masculina', 20, 'ETQ-ROU-002', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), '42', 'Usado - Bom estado', 'Masculino'),
('Vestido Floral Infantil', 15, 'ETQ-ROU-003', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), '8', 'Seminovo', 'Infantil'),
('Casaco de Lã Feminino', 10, 'ETQ-ROU-004', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), 'G', 'Usado - Bom estado', 'Feminino'),
('Tênis Esportivo Tam 38', 12, 'ETQ-CAL-001', (SELECT category_id FROM category WHERE name = 'Calçados' LIMIT 1), '38', 'Novo', 'Unissex'),
('Sapato Social Masculino', 8, 'ETQ-CAL-002', (SELECT category_id FROM category WHERE name = 'Calçados' LIMIT 1), '41', 'Usado - Bom estado', 'Masculino');
