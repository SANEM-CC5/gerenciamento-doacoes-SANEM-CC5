package com.javalovers.core.item.mapper;

import com.javalovers.core.category.repository.CategoryRepository;
import com.javalovers.core.item.domain.dto.request.ItemFormDTO;
import com.javalovers.core.item.domain.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemCreateMapper {

    private final CategoryRepository categoryRepository;

    public Item convert(ItemFormDTO itemFormDTO){
        Item item = new Item();
        item.setDescription(itemFormDTO.description());
        item.setStockQuantity(0L); 
        String tag = itemFormDTO.tagCode();
        if (tag != null && tag.trim().isEmpty()) {
            tag = null;
        }
        item.setTagCode(tag);

        if (itemFormDTO.categoryId() != null) {
            categoryRepository.findById(itemFormDTO.categoryId())
                    .ifPresent(item::setCategory);
        }

        item.setSize(itemFormDTO.size());
        item.setConservationState(itemFormDTO.conservationState());
        item.setGender(itemFormDTO.gender());

        return item;
    }
}
