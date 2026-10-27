package com.javalovers.core.item;

import com.javalovers.core.category.domain.entity.Category;
import com.javalovers.core.inventory.service.InventoryService;
import com.javalovers.core.item.domain.dto.request.ItemFilterDTO;
import com.javalovers.core.item.domain.dto.request.ItemFormDTO;
import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.item.mapper.ItemCreateMapper;
import com.javalovers.core.item.mapper.ItemDTOMapper;
import com.javalovers.core.item.mapper.ItemUpdateMapper;
import com.javalovers.core.item.repository.ItemRepository;
import com.javalovers.core.item.service.ItemService;
import com.javalovers.core.item.util.QRCodeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemCreateMapper itemCreateMapper;

    @Mock
    private ItemDTOMapper itemDTOMapper;

    @Mock
    private ItemUpdateMapper itemUpdateMapper;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("RF16: list com ItemFilterDTO deve consultar repositório passando Specification com filtros")
    void testListWithRF16FilterDTO() {
        ItemFilterDTO filter = new ItemFilterDTO(
                "Camiseta",
                1L,
                100L,
                1L,
                "Roupas",
                "M",
                "Seminovo",
                "Unissex"
        );

        Item item = new Item();
        item.setItemId(1L);
        item.setDescription("Camiseta Algodão");
        item.setSize("M");
        item.setConservationState("Seminovo");
        item.setGender("Unissex");

        when(itemRepository.findAll(any(Specification.class))).thenReturn(List.of(item));

        List<Item> result = itemService.list(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Camiseta Algodão", result.get(0).getDescription());
        verify(itemRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("RF16: createAndSave deve salvar item com atributos completos do RF16")
    void testCreateAndSaveWithRF16Fields() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("Roupas");

        ItemFormDTO formDTO = new ItemFormDTO(
                "Jaqueta de Couro",
                5L,
                "TAG-JAQ-01",
                1L,
                "G",
                "Usado - Bom estado",
                "Masculino"
        );

        Item itemToSave = new Item();
        itemToSave.setDescription("Jaqueta de Couro");
        itemToSave.setTagCode("TAG-JAQ-01");
        itemToSave.setCategory(category);
        itemToSave.setSize("G");
        itemToSave.setConservationState("Usado - Bom estado");
        itemToSave.setGender("Masculino");
        itemToSave.setStockQuantity(5L);

        when(itemCreateMapper.convert(formDTO)).thenReturn(itemToSave);
        when(itemRepository.save(itemToSave)).thenReturn(itemToSave);

        Item saved = itemService.createAndSave(formDTO);

        assertNotNull(saved);
        assertEquals("Jaqueta de Couro", saved.getDescription());
        assertEquals("G", saved.getSize());
        assertEquals("Usado - Bom estado", saved.getConservationState());
        assertEquals("Masculino", saved.getGender());
        assertEquals(category, saved.getCategory());
        verify(itemRepository).save(itemToSave);
    }
}
