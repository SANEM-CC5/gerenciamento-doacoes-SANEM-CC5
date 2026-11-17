package com.javalovers.controller;

import com.javalovers.util.HttpUtils;
import com.javalovers.model.dto.CollectionPointFilterDTO;
import com.javalovers.model.dto.CollectionPointFormDTO;
import com.javalovers.model.dto.CollectionPointDTO;
import com.javalovers.model.entity.CollectionPoint;
import com.javalovers.service.CollectionPointService;
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
@RequestMapping("/collection-point")
public class CollectionPointController {

    private final CollectionPointService collectionPointService;

    @GetMapping
    public ResponseEntity<Page<CollectionPointDTO>> listPaged(Pageable pageable,
            CollectionPointFilterDTO collectionPointFilterDTO) {
        Page<CollectionPoint> collectionPointPage = collectionPointService.list(pageable, collectionPointFilterDTO);
        Page<CollectionPointDTO> collectionPointDTOPage = collectionPointService
                .generateCollectionPointDTOPage(collectionPointPage);

        return ResponseEntity.ok(collectionPointDTOPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CollectionPointDTO>> list(CollectionPointFilterDTO collectionPointFilterDTO) {
        List<CollectionPoint> collectionPointList = collectionPointService.list(collectionPointFilterDTO);
        List<CollectionPointDTO> collectionPointDTOList = collectionPointService
                .generateCollectionPointList(collectionPointList);

        return ResponseEntity.ok(collectionPointDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionPointDTO> get(@PathVariable Long id) {
        CollectionPoint collectionPoint = collectionPointService.getOrNull(id);
        if (collectionPoint == null)
            return ResponseEntity.notFound().build();

        CollectionPointDTO collectionPointDTO = collectionPointService.generateCollectionPointDTO(collectionPoint);

        return ResponseEntity.ok(collectionPointDTO);
    }

    @PostMapping
    public ResponseEntity<CollectionPointDTO> create(@RequestBody @Valid CollectionPointFormDTO collectionPointFormDTO,
            UriComponentsBuilder uriComponentsBuilder) {
        CollectionPoint collectionPoint = collectionPointService.generateCollectionPoint(collectionPointFormDTO);
        collectionPointService.save(collectionPoint);

        URI uri = HttpUtils.createURI(uriComponentsBuilder, "collection-point", collectionPoint.getCollectionPointId());

        return ResponseEntity.created(uri).body(collectionPointService.generateCollectionPointDTO(collectionPoint));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
            @RequestBody @Valid CollectionPointFormDTO collectionPointFormDTO) {
        CollectionPoint collectionPoint = collectionPointService.getOrNull(id);
        if (collectionPoint == null)
            return ResponseEntity.notFound().build();

        collectionPointService.updateCollectionPoint(collectionPoint, collectionPointFormDTO);

        collectionPointService.save(collectionPoint);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        CollectionPoint collectionPoint = collectionPointService.getOrNull(id);
        if (collectionPoint == null)
            return ResponseEntity.notFound().build();

        collectionPointService.delete(collectionPoint);

        return ResponseEntity.noContent().build();
    }
}
