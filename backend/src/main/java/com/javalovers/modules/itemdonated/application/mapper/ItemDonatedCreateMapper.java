package com.javalovers.modules.itemdonated.application.mapper;

import com.javalovers.modules.donation.domain.entity.Donation;
import com.javalovers.modules.item.domain.entity.Item;
import com.javalovers.modules.itemdonated.application.dto.request.ItemDonatedFormDTO;
import com.javalovers.modules.itemdonated.domain.entity.ItemDonated;
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
