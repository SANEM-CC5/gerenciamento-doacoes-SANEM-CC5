package com.javalovers.core.item;

import com.javalovers.core.category.domain.entity.Category;
import com.javalovers.core.category.repository.CategoryRepository;
import com.javalovers.core.item.domain.dto.request.ItemFormDTO;
import com.javalovers.core.item.domain.dto.response.ItemDTO;
import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.item.mapper.ItemCreateMapper;
import com.javalovers.core.item.mapper.ItemDTOMapper;
import com.javalovers.core.item.mapper.ItemUpdateMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ItemCreateMapper itemCreateMapper;

    @InjectMocks
    private ItemUpdateMapper itemUpdateMapper;

    private final ItemDTOMapper itemDTOMapper = new ItemDTOMapper();

    @Test
    @DisplayName("RF16: Deve converter ItemFormDTO para Item com categoria, tamanho, estado e gênero")
    void testItemCreateMapperWithRF16Fields() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("Roupas");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        ItemFormDTO formDTO = new ItemFormDTO(
                "Camiseta Algodão",
                10L,
                "TAG-001",
                1L,
                "M",
                "Seminovo",
                "Unissex"
        );

        Item item = itemCreateMapper.convert(formDTO);

        assertNotNull(item);
        assertEquals("Camiseta Algodão", item.getDescription());
        assertEquals("TAG-001", item.getTagCode());
        assertEquals(category, item.getCategory());
        assertEquals("M", item.getSize());
        assertEquals("Seminovo", item.getConservationState());
        assertEquals("Unissex", item.getGender());
    }

    @Test
    @DisplayName("RF16: Deve atualizar Item com novos atributos de categoria, tamanho, estado e gênero")
    void testItemUpdateMapperWithRF16Fields() {
        Category category = new Category();
        category.setCategoryId(2L);
        category.setName("Calçados");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        Item item = new Item();
        item.setDescription("Antigo");
        item.setSize("P");
        item.setConservationState("Usado");

        ItemFormDTO formDTO = new ItemFormDTO(
                "Tênis Esportivo",
                20L,
                "TAG-002",
                2L,
                "40",
                "Novo",
                "Masculino"
        );

        itemUpdateMapper.update(item, formDTO);

        assertEquals("Tênis Esportivo", item.getDescription());
        assertEquals("TAG-002", item.getTagCode());
        assertEquals(category, item.getCategory());
        assertEquals("40", item.getSize());
        assertEquals("Novo", item.getConservationState());
        assertEquals("Masculino", item.getGender());
    }

    @Test
    @DisplayName("RF16: Deve converter Item para ItemDTO incluindo categoria, tamanho, estado e gênero")
    void testItemDTOMapperWithRF16Fields() {
        Category category = new Category();
        category.setCategoryId(3L);
        category.setName("Higiene");

        Item item = new Item();
        item.setItemId(10L);
        item.setDescription("Sabonete");
        item.setStockQuantity(50L);
        item.setTagCode("TAG-003");
        item.setCategory(category);
        item.setSize("90g");
        item.setConservationState("Novo");
        item.setGender("Não aplicável");

        ItemDTO dto = itemDTOMapper.convert(item);

        assertNotNull(dto);
        assertEquals(10L, dto.itemId());
        assertEquals("Sabonete", dto.description());
        assertEquals(50L, dto.stockQuantity());
        assertEquals("TAG-003", dto.tagCode());
        assertNotNull(dto.category());
        assertEquals(3L, dto.category().categoryId());
        assertEquals("Higiene", dto.category().name());
        assertEquals("90g", dto.size());
        assertEquals("Novo", dto.conservationState());
        assertEquals("Não aplicável", dto.gender());
    }
}
