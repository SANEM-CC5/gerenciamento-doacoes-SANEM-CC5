-- =====================================================================
-- V12.1: Seed complementar RF16 (itens de vestuário/calçados e categorização)
-- Aplicado apenas no profile "dev" (classpath:db/seed).
-- =====================================================================

-- Atualizar itens base inseridos no V2_seed_data com categoria, gênero e conservação
UPDATE item
SET category_id = COALESCE(category_id, (SELECT category_id FROM category WHERE name = 'Alimentos' LIMIT 1)),
    conservation_state = COALESCE(conservation_state, 'Novo'),
    condition_status = COALESCE(condition_status, 'Novo'),
    gender = COALESCE(gender, 'Não aplicável')
WHERE description LIKE '%Arroz%'
   OR description LIKE '%Feijão%'
   OR description LIKE '%Macarrão%'
   OR description LIKE '%Óleo%'
   OR description LIKE '%Leite%';

UPDATE item
SET category_id = COALESCE(category_id, (SELECT category_id FROM category WHERE name = 'Higiene' LIMIT 1)),
    conservation_state = COALESCE(conservation_state, 'Novo'),
    condition_status = COALESCE(condition_status, 'Novo'),
    gender = COALESCE(gender, 'Não aplicável')
WHERE description LIKE '%Sabonete%'
   OR description LIKE '%Creme Dental%'
   OR description LIKE '%Fralda%';

UPDATE item
SET size = COALESCE(size, 'M')
WHERE description LIKE '%Fralda%';

-- Itens variados de vestuário e calçados para teste dos filtros RF16/Time2
INSERT INTO item (description, stock_quantity, tag_code, category_id, size, conservation_state, condition_status, gender, type, location)
VALUES
    ('Camiseta Algodão Básica', 35, 'ETQ-ROU-001', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), 'M', 'Seminovo', 'Bom', 'Unissex', 'Camisa', 'Medianeira'),
    ('Calça Jeans Masculina', 20, 'ETQ-ROU-002', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), '42', 'Usado - Bom estado', 'Bom', 'Masculino', 'Calça', 'Medianeira'),
    ('Vestido Floral Infantil', 15, 'ETQ-ROU-003', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), '8', 'Seminovo', 'Bom', 'Infantil', 'Vestido', 'Medianeira'),
    ('Casaco de Lã Feminino', 10, 'ETQ-ROU-004', (SELECT category_id FROM category WHERE name = 'Roupas' LIMIT 1), 'G', 'Usado - Bom estado', 'Bom', 'Feminino', 'Casaco', 'Medianeira'),
    ('Tênis Esportivo Tam 38', 12, 'ETQ-CAL-001', (SELECT category_id FROM category WHERE name = 'Calçados' LIMIT 1), '38', 'Novo', 'Novo', 'Unissex', 'Calçado', 'Medianeira'),
    ('Sapato Social Masculino', 8, 'ETQ-CAL-002', (SELECT category_id FROM category WHERE name = 'Calçados' LIMIT 1), '41', 'Usado - Bom estado', 'Bom', 'Masculino', 'Calçado', 'Medianeira')
ON CONFLICT (tag_code) DO NOTHING;
