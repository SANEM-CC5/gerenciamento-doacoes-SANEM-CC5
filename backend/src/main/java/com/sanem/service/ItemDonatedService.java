package com.sanem.service;

import com.sanem.exception.EntityNotFoundException;
import com.sanem.model.entity.Donation;
import com.sanem.model.entity.Item;
import com.sanem.repository.ItemRepository;
import com.sanem.model.dto.ItemDonatedFormDTO;
import com.sanem.model.dto.ItemDonatedDTO;
import com.sanem.model.entity.ItemDonated;
import com.sanem.mapper.ItemDonatedCreateMapper;
import com.sanem.mapper.ItemDonatedDTOMapper;
import com.sanem.repository.ItemDonatedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemDonatedService {

    private final ItemDonatedRepository itemDonatedRepository;
    private final ItemRepository itemRepository;
    private final ItemDonatedDTOMapper itemDonatedDTOMapper;
    private final ItemDonatedCreateMapper itemDonatedCreateMapper;

    public ItemDonated generateItemDonated(ItemDonatedFormDTO itemDonatedFormDTO, Donation donation, Item item) {
        return itemDonatedCreateMapper.convert(itemDonatedFormDTO, donation, item);
    }

    public void save(ItemDonated itemDonated) {
        itemDonatedRepository.save(itemDonated);
    }

    public ItemDonatedDTO generateItemDonatedDTO(ItemDonated itemDonated) {
        return itemDonatedDTOMapper.convert(itemDonated);
    }

    public ItemDonated getOrNull(Long id) {
        return itemDonatedRepository.findById(id).orElse(null);
    }

    public ItemDonated getOrThrowException(Long id) {
        return itemDonatedRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ItemDonated", id)
        );
    }

}
