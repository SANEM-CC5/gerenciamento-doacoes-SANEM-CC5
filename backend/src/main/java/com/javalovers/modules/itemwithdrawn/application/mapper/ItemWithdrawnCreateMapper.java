package com.javalovers.modules.itemwithdrawn.application.mapper;

import com.javalovers.modules.item.domain.entity.Item;
import com.javalovers.modules.itemwithdrawn.application.dto.request.ItemWithdrawnFormDTO;
import com.javalovers.modules.itemwithdrawn.domain.entity.ItemWithdrawn;
import com.javalovers.modules.withdrawal.domain.entity.Withdrawal;
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
