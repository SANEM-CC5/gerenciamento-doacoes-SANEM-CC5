package com.javalovers.core.item.mapper;

import com.javalovers.core.category.repository.CategoryRepository;
import com.javalovers.core.item.domain.dto.request.ItemFormDTO;
import com.javalovers.core.item.domain.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemUpdateMapper {

    private final CategoryRepository categoryRepository;

    public void update(Item item, ItemFormDTO itemFormDTO){
        item.setDescription(itemFormDTO.description());
        String tag = itemFormDTO.tagCode();
        if (tag != null && tag.trim().isEmpty()) {
            tag = null;
        }
        item.setTagCode(tag);

        if (itemFormDTO.categoryId() != null) {
            item.setCategory(categoryRepository.findById(itemFormDTO.categoryId()).orElse(null));
        } else {
            item.setCategory(null);
        }

        item.setSize(itemFormDTO.size());
        item.setConservationState(itemFormDTO.conservationState());
        item.setGender(itemFormDTO.gender());
    }
}
