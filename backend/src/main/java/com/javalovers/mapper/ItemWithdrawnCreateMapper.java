package com.javalovers.mapper;

import com.javalovers.model.entity.Item;
import com.javalovers.model.dto.ItemWithdrawnFormDTO;
import com.javalovers.model.entity.ItemWithdrawn;
import com.javalovers.model.entity.Withdrawal;
import org.springframework.stereotype.Service;

@Service
public class ItemWithdrawnCreateMapper {

    public ItemWithdrawn convert(ItemWithdrawnFormDTO itemWithdrawnFormDTO, Withdrawal withdrawal, Item item) {

        ItemWithdrawn itemWithdrawn = new ItemWithdrawn();
        itemWithdrawn.setWithdrawal(withdrawal);
        itemWithdrawn.setItem(item);
        itemWithdrawn.setQuantity(itemWithdrawnFormDTO.quantity());

        return itemWithdrawn;
    }
}
