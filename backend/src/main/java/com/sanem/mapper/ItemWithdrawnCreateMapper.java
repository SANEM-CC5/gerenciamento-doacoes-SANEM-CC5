package com.sanem.mapper;

import com.sanem.model.entity.Item;
import com.sanem.model.dto.ItemWithdrawnFormDTO;
import com.sanem.model.entity.ItemWithdrawn;
import com.sanem.model.entity.Withdrawal;
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
