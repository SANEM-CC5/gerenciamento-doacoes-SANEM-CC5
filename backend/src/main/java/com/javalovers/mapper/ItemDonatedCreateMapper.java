package com.javalovers.mapper;

import com.javalovers.model.entity.Donation;
import com.javalovers.model.entity.Item;
import com.javalovers.model.dto.ItemDonatedFormDTO;
import com.javalovers.model.entity.ItemDonated;
import org.springframework.stereotype.Service;

@Service
public class ItemDonatedCreateMapper {

    public ItemDonated convert(ItemDonatedFormDTO itemDonatedFormDTO, Donation donation, Item item) {
        ItemDonated itemDonated = new ItemDonated();
        itemDonated.setDonation(donation);
        itemDonated.setItem(item);
        itemDonated.setQuantity(itemDonatedFormDTO.quantity());

        return itemDonated;
    }
}
