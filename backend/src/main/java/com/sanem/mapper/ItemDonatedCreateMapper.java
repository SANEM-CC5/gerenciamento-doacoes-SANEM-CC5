package com.sanem.mapper;

import com.sanem.model.entity.Donation;
import com.sanem.model.entity.Item;
import com.sanem.model.dto.ItemDonatedFormDTO;
import com.sanem.model.entity.ItemDonated;
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
