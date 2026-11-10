package com.javalovers.modules.itemdonated.application.service;

import com.javalovers.shared.exception.EntityNotFoundException;
import com.javalovers.modules.donation.domain.entity.Donation;
import com.javalovers.modules.item.domain.entity.Item;
import com.javalovers.modules.item.infrastructure.persistence.repository.ItemRepository;
import com.javalovers.modules.itemdonated.application.dto.request.ItemDonatedFormDTO;
import com.javalovers.modules.itemdonated.application.dto.response.ItemDonatedDTO;
import com.javalovers.modules.itemdonated.domain.entity.ItemDonated;
import com.javalovers.modules.itemdonated.application.mapper.ItemDonatedCreateMapper;
import com.javalovers.modules.itemdonated.application.mapper.ItemDonatedDTOMapper;
import com.javalovers.modules.itemdonated.infrastructure.persistence.repository.ItemDonatedRepository;
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
