package com.javalovers.modules.itemwithdrawn.application.service;

import com.javalovers.shared.exception.EntityNotFoundException;
import com.javalovers.modules.item.domain.entity.Item;
import com.javalovers.modules.item.infrastructure.persistence.repository.ItemRepository;
import com.javalovers.modules.itemwithdrawn.application.dto.request.ItemWithdrawnFormDTO;
import com.javalovers.modules.itemwithdrawn.application.dto.response.ItemWithdrawnDTO;
import com.javalovers.modules.itemwithdrawn.domain.entity.ItemWithdrawn;
import com.javalovers.modules.itemwithdrawn.application.mapper.ItemWithdrawnCreateMapper;
import com.javalovers.modules.itemwithdrawn.application.mapper.ItemWithdrawnDTOMapper;
import com.javalovers.modules.itemwithdrawn.infrastructure.persistence.repository.ItemWithdrawnRepository;
import com.javalovers.modules.withdrawal.domain.entity.Withdrawal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemWithdrawnService {

    private final ItemWithdrawnRepository itemWithdrawnRepository;
    private final ItemRepository itemRepository;
    private final ItemWithdrawnDTOMapper itemWithdrawnDTOMapper;
    private final ItemWithdrawnCreateMapper itemWithdrawnCreateMapper;

    public ItemWithdrawn generateItemWithdrawn(ItemWithdrawnFormDTO itemWithdrawnFormDTO, Withdrawal withdrawal, Item item) {
        return itemWithdrawnCreateMapper.convert(itemWithdrawnFormDTO, withdrawal, item);
    }

    public void save(ItemWithdrawn itemWithdrawn) {
        itemWithdrawnRepository.save(itemWithdrawn);
    }

    public ItemWithdrawnDTO generateItemWithdrawnDTO(ItemWithdrawn itemWithdrawn) {
        return itemWithdrawnDTOMapper.convert(itemWithdrawn);
    }

    public ItemWithdrawn getOrNull(Long id) {
        return itemWithdrawnRepository.findById(id).orElse(null);
    }

    public ItemWithdrawn getOrThrowException(Long id) {
        return itemWithdrawnRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ItemWithdrawn", id)
        );
    }

    public void delete(ItemWithdrawn itemWithdrawn) {
        itemWithdrawnRepository.delete(itemWithdrawn);
    }

}
