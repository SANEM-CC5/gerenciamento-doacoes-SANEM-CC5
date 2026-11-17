package com.javalovers.service;

import com.javalovers.exception.EntityNotFoundException;
import com.javalovers.model.entity.Donation;
import com.javalovers.model.entity.Item;
import com.javalovers.repository.ItemRepository;
import com.javalovers.model.dto.ItemDonatedFormDTO;
import com.javalovers.model.dto.ItemDonatedDTO;
import com.javalovers.model.entity.ItemDonated;
import com.javalovers.mapper.ItemDonatedCreateMapper;
import com.javalovers.mapper.ItemDonatedDTOMapper;
import com.javalovers.repository.ItemDonatedRepository;
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
