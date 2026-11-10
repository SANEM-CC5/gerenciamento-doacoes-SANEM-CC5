package com.javalovers.core.item.service;
import com.javalovers.core.inventory.service.InventoryService;
import com.javalovers.core.inventory.domain.enums.TransactionType;
import com.javalovers.core.item.domain.dto.request.ItemFilterDTO;
import com.javalovers.core.item.domain.dto.request.ItemFormDTO;
import com.javalovers.core.item.domain.dto.response.ItemDTO;
import com.javalovers.core.item.domain.dto.response.ItemLabelDTO;
import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.item.mapper.ItemCreateMapper;
import com.javalovers.core.item.mapper.ItemDTOMapper;
import com.javalovers.core.item.mapper.ItemUpdateMapper;
import com.javalovers.core.item.repository.ItemRepository;
import com.javalovers.core.item.util.QRCodeService;
import com.javalovers.core.stockalert.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import com.google.zxing.WriterException;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemCreateMapper itemCreateMapper;
    private final ItemDTOMapper itemDTOMapper;
    private final ItemUpdateMapper itemUpdateMapper;
    private final QRCodeService qrCodeService;
    private final InventoryService inventoryService;
    private final StockAlertService stockAlertService;

    public Item generateItem(ItemFormDTO itemFormDTO) {
        return itemCreateMapper.convert(itemFormDTO);
    }

    @Transactional
    public Item createAndSave(ItemFormDTO itemFormDTO) {
        validateMinimumStock(itemFormDTO);
        Item item = itemCreateMapper.convert(itemFormDTO);
        item = itemRepository.save(item);

        Long requestedInitialStock = itemFormDTO.stockQuantity() != null ? itemFormDTO.stockQuantity() : 0L; 

        if (requestedInitialStock > 0) {
            inventoryService.processTransaction(item, requestedInitialStock, TransactionType.MANUAL_ADJUSTMENT_IN, null);
        } else {
            // Item criado sem saldo, mas com mínimo configurado, já nasce em alerta
            stockAlertService.evaluate(item);
        }

        return item;
    }

    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    public ItemDTO generateItemDTO(Item item) {
        return itemDTOMapper.convert(item);
    }

    public Item getOrNull(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Item getOrThrowException(Long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new com.javalovers.common.exception.EntityNotFoundException("item", id));
    }

    @Transactional
    public void updateItem(Item item, ItemFormDTO itemFormDTO) {
        Long currentStock = item.getStockQuantity() != null ? item.getStockQuantity() : 0L;
        Long requestedStock = itemFormDTO.stockQuantity() != null ? itemFormDTO.stockQuantity() : 0L;

        if (requestedStock < 0) {
            throw new IllegalArgumentException("O estoque do item não pode ser um valor negativo.");
        }
        validateMinimumStock(itemFormDTO);

        itemUpdateMapper.update(item, itemFormDTO); 

        if (!currentStock.equals(requestedStock)) {
            if (requestedStock > currentStock) {
                Long difference = requestedStock - currentStock;
                inventoryService.processTransaction(item, difference, TransactionType.MANUAL_ADJUSTMENT_IN, null);
            } else {
                Long difference = currentStock - requestedStock;
                inventoryService.processTransaction(item, difference, TransactionType.MANUAL_ADJUSTMENT_OUT, null);
            }
        }

        itemRepository.save(item); 

        // RF19: o mínimo pode ter sido alterado sem mudança de saldo
        stockAlertService.evaluate(item);
    }

    private void validateMinimumStock(ItemFormDTO itemFormDTO) {
        if (itemFormDTO.minimumStock() != null && itemFormDTO.minimumStock() < 0) {
            throw new IllegalArgumentException("O estoque mínimo não pode ser um valor negativo.");
        }
    }

    @Transactional
    public void delete(Item item) {
        // Marca como excluído antes de zerar o saldo: assim a verificação de estoque
        // mínimo (RF19) não notifica sobre um item que está sendo removido e
        // resolve qualquer alerta que estivesse aberto para ele.
        item.softDelete();
        Long currentStock = item.getStockQuantity();

        if (currentStock != null && currentStock > 0) {
            inventoryService.processTransaction(
                item, 
                currentStock, 
                TransactionType.MANUAL_ADJUSTMENT_OUT,
                null
            );

            item.setStockQuantity(0L); 
        } else {
            stockAlertService.evaluate(item);
        }

        itemRepository.save(item);
    }

    public List<Item> list(ItemFilterDTO itemFilterDTO) {
        Specification<Item> itemSpecification = generateSpecification(itemFilterDTO);
        return itemRepository.findAll(itemSpecification);
    }

    public Page<Item> list(Pageable pageable, ItemFilterDTO itemFilterDTO) {
        Specification<Item> itemSpecification = generateSpecification(itemFilterDTO);
        return itemRepository.findAll(itemSpecification, pageable);
    }

    private Specification<Item> generateSpecification(ItemFilterDTO itemFilterDTO) {
        Specification<Item> notDeletedSpecification = (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("deletedAt"));
        Specification<Item> searchSpecification = (root, query, criteriaBuilder) -> {
            if (itemFilterDTO.searchTerm() == null || itemFilterDTO.searchTerm().trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + itemFilterDTO.searchTerm().toLowerCase() + "%";
            
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("tagCode")), pattern)
            );
        };

        Specification<Item> quantitySpecification = (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();
            if (itemFilterDTO.minQuantity() != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.greaterThanOrEqualTo(root.get("stockQuantity"), itemFilterDTO.minQuantity()));
            }
            if (itemFilterDTO.maxQuantity() != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.lessThanOrEqualTo(root.get("stockQuantity"), itemFilterDTO.maxQuantity()));
            }
            return predicates;
        };

<<<<<<< HEAD
        // RF19: somente itens com mínimo configurado e saldo <= mínimo
        Specification<Item> lowStockSpecification = (root, query, criteriaBuilder) -> {
            if (!Boolean.TRUE.equals(itemFilterDTO.lowStock())) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("minimumStock")),
                criteriaBuilder.lessThanOrEqualTo(root.get("stockQuantity"), root.get("minimumStock"))
            );
=======
        Specification<Item> attributeSpecification = (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();
            if (hasValue(itemFilterDTO.size())) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("size"), itemFilterDTO.size()));
            }
            if (hasValue(itemFilterDTO.gender())) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("gender"), itemFilterDTO.gender()));
            }
            if (hasValue(itemFilterDTO.type())) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("type"), itemFilterDTO.type()));
            }
            if (hasValue(itemFilterDTO.conditionStatus())) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("conditionStatus"), itemFilterDTO.conditionStatus()));
            }
            if (hasValue(itemFilterDTO.location())) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("location"), itemFilterDTO.location()));
            }
            return predicates;
>>>>>>> origin/main
        };

        return Specification.where(searchSpecification)
                .and(quantitySpecification)
<<<<<<< HEAD
                .and(lowStockSpecification)
=======
                .and(attributeSpecification)
>>>>>>> origin/main
                .and(notDeletedSpecification);
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public Page<ItemDTO> generateItemDTOPage(Page<Item> itemPage) {
        return itemPage.map(this::generateItemDTO);
    }

    public List<ItemDTO> generateItemDTOList(List<Item> itemList) {
        return itemList.stream().map(itemDTOMapper::convert).toList();
    }

    @Transactional
    public String generateTagCode(Long itemId) {
        Item item = getOrNull(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item não encontrado");
        }

        if (item.getTagCode() != null && !item.getTagCode().isEmpty()) {
            return item.getTagCode();
        }

        String tagCode = generateUniqueTagCode();

        // Garantir que o código seja único
        while (itemRepository.findByTagCode(tagCode).isPresent()) {
            tagCode = generateUniqueTagCode();
        }

        item.setTagCode(tagCode);
        itemRepository.save(item);

        return tagCode;
    }

    private String generateUniqueTagCode() {
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String timestampPart = String.valueOf(System.currentTimeMillis()).substring(8);
        return "ITEM-" + uuidPart + timestampPart;
    }

    public String generateQRCodeForItem(Long itemId) throws WriterException, IOException {
        Item item = getOrNull(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item não encontrado");
        }

        String tagCode = item.getTagCode();
        if (tagCode == null || tagCode.isEmpty()) {
            tagCode = generateTagCode(itemId);
        }

        String qrData = String.format("ITEM:%s|DESC:%s", tagCode, item.getDescription());
        return qrCodeService.generateQRCodeBase64(qrData);
    }

    public ItemLabelDTO generateItemLabel(Long itemId) throws WriterException, IOException {
        Item item = getOrNull(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item não encontrado");
        }

        String tagCode = item.getTagCode();
        if (tagCode == null || tagCode.isEmpty()) {
            tagCode = generateTagCode(itemId);
        }

        String qrCodeBase64 = generateQRCodeForItem(itemId);

        return new ItemLabelDTO(
                item.getItemId(),
                item.getDescription(),
                tagCode,
                qrCodeBase64);
    }

}
