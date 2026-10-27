USE sanem;

-- Desvincular itens caso algum aponte para categorias legadas em inglês
UPDATE item SET category_id = NULL 
WHERE category_id IN (
    SELECT category_id FROM (
        SELECT category_id FROM category WHERE name IN ('Food', 'Clothing', 'Hygiene')
    ) AS temp_cats
);

-- Remover categorias legadas em inglês do seed inicial
DELETE FROM category WHERE name IN ('Food', 'Clothing', 'Hygiene');
