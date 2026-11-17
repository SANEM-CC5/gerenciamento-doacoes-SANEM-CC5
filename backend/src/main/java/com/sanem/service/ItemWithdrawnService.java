package com.sanem.service;

import com.sanem.exception.EntityNotFoundException;
import com.sanem.model.entity.Item;
import com.sanem.repository.ItemRepository;
import com.sanem.model.dto.ItemWithdrawnFormDTO;
import com.sanem.model.dto.ItemWithdrawnDTO;
import com.sanem.model.entity.ItemWithdrawn;
import com.sanem.mapper.ItemWithdrawnCreateMapper;
import com.sanem.mapper.ItemWithdrawnDTOMapper;
import com.sanem.repository.ItemWithdrawnRepository;
import com.sanem.model.entity.Withdrawal;
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
