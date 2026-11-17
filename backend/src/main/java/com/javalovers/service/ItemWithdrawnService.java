package com.javalovers.service;

import com.javalovers.exception.EntityNotFoundException;
import com.javalovers.model.entity.Item;
import com.javalovers.repository.ItemRepository;
import com.javalovers.model.dto.ItemWithdrawnFormDTO;
import com.javalovers.model.dto.ItemWithdrawnDTO;
import com.javalovers.model.entity.ItemWithdrawn;
import com.javalovers.mapper.ItemWithdrawnCreateMapper;
import com.javalovers.mapper.ItemWithdrawnDTOMapper;
import com.javalovers.repository.ItemWithdrawnRepository;
import com.javalovers.model.entity.Withdrawal;
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
