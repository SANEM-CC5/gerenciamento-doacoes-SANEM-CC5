package com.javalovers.modules.item.infrastructure.web.controller;

import com.javalovers.shared.utils.HttpUtils;
import com.javalovers.modules.item.application.dto.request.ItemFilterDTO;
import com.javalovers.modules.item.application.dto.request.ItemFormDTO;
import com.javalovers.modules.item.application.dto.response.ItemDTO;
import com.javalovers.modules.item.application.dto.response.ItemLabelDTO;
import com.google.zxing.WriterException;

import java.io.IOException;
import com.javalovers.modules.item.domain.entity.Item;
import com.javalovers.modules.item.application.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> listPaged(Pageable pageable, ItemFilterDTO itemFilterDTO) {
        Page<Item> itemPage = itemService.list(pageable, itemFilterDTO);
        Page<ItemDTO> itemDTOPage = itemService.generateItemDTOPage(itemPage);

        return ResponseEntity.ok(itemDTOPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemDTO>> list(ItemFilterDTO itemFilterDTO) {
        List<Item> itemList = itemService.list(itemFilterDTO);
        List<ItemDTO> itemDTOList = itemService.generateItemDTOList(itemList);

        return ResponseEntity.ok(itemDTOList);
    }

    @GetMapping("/{id}")
        public ResponseEntity<ItemDTO> get(@PathVariable Long id) {
        Item item = itemService.getOrNull(id);
        if(item == null) return ResponseEntity.notFound().build();

        ItemDTO itemDTO = itemService.generateItemDTO(item);

        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping
    public ResponseEntity<ItemDTO> create(@RequestBody @Valid ItemFormDTO itemFormDTO, UriComponentsBuilder uriComponentsBuilder) {
        Item item = itemService.createAndSave(itemFormDTO);

        URI uri = HttpUtils.createURI(uriComponentsBuilder, "item", item.getItemId());

        return ResponseEntity.created(uri).body(itemService.generateItemDTO(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid ItemFormDTO itemFormDTO) {
        Item item = itemService.getOrNull(id);
        if(item == null) return ResponseEntity.notFound().build();

        itemService.updateItem(item, itemFormDTO);

        itemService.save(item);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Item item = itemService.getOrNull(id);
        if(item == null) return ResponseEntity.notFound().build();

        itemService.delete(item);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/label")
    public ResponseEntity<ItemLabelDTO> getItemLabel(@PathVariable Long id) {
        Item item = itemService.getOrNull(id);
        if(item == null) return ResponseEntity.notFound().build();

        try {
            ItemLabelDTO labelDTO = itemService.generateItemLabel(id);
            return ResponseEntity.ok(labelDTO);
        } catch (WriterException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
