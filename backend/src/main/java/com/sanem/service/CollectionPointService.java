package com.sanem.service;

import com.sanem.exception.EntityNotFoundException;
import com.sanem.specification.SearchCriteria;
import com.sanem.specification.SpecificationHelper;
import com.sanem.model.dto.CollectionPointFilterDTO;
import com.sanem.model.dto.CollectionPointFormDTO;
import com.sanem.model.dto.CollectionPointDTO;
import com.sanem.model.entity.CollectionPoint;
import com.sanem.mapper.CollectionPointCreateMapper;
import com.sanem.mapper.CollectionPointDTOMapper;
import com.sanem.mapper.CollectionPointUpdateMapper;
import com.sanem.repository.CollectionPointRepository;
import com.sanem.specification.CollectionPointSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionPointService {

    private final CollectionPointRepository collectionPointRepository;
    private final CollectionPointCreateMapper collectionPointCreateMapper;
    private final CollectionPointDTOMapper collectionPointDTOMapper;
    private final CollectionPointUpdateMapper collectionPointUpdateMapper;

    public CollectionPoint generateCollectionPoint(CollectionPointFormDTO collectionPointFormDTO) {
        return collectionPointCreateMapper.convert(collectionPointFormDTO);
    }

    public void save(CollectionPoint collectionPoint) {
        collectionPointRepository.save(collectionPoint);
    }

    public CollectionPointDTO generateCollectionPointDTO(CollectionPoint collectionPoint) {
        return collectionPointDTOMapper.convert(collectionPoint);
    }

    public CollectionPoint getOrNull(Long id) {
        return collectionPointRepository.findById(id).orElse(null);
    }

    public void updateCollectionPoint(CollectionPoint collectionPoint, CollectionPointFormDTO collectionPointFormDTO) {
        collectionPointUpdateMapper.update(collectionPoint, collectionPointFormDTO);
    }

    @Transactional
    public void delete(CollectionPoint collectionPoint) {
        collectionPoint.softDelete();
        collectionPointRepository.save(collectionPoint);
    }

    public List<CollectionPoint> list(CollectionPointFilterDTO collectionPointFilterDTO) {
        Specification<CollectionPoint> specification = generateSpecification(collectionPointFilterDTO);
        return collectionPointRepository.findAll(specification);
    }

    public Page<CollectionPoint> list(Pageable pageable, CollectionPointFilterDTO collectionPointFilterDTO) {
        Specification<CollectionPoint> specification = generateSpecification(collectionPointFilterDTO);
        return collectionPointRepository.findAll(specification, pageable);
    }

    private Specification<CollectionPoint> generateSpecification(CollectionPointFilterDTO collectionPointFilterDTO) {
        SearchCriteria<String> nameCriteria = SpecificationHelper.generateInnerLikeCriteria("name", collectionPointFilterDTO.name());
        SearchCriteria<String> cityCriteria = SpecificationHelper.generateInnerLikeCriteria("city", collectionPointFilterDTO.city());
        SearchCriteria<String> neighborhoodCriteria = SpecificationHelper.generateInnerLikeCriteria("neighborhood", collectionPointFilterDTO.neighborhood());

        Specification<CollectionPoint> nameSpecification = new CollectionPointSpecification(nameCriteria);
        Specification<CollectionPoint> citySpecification = new CollectionPointSpecification(cityCriteria);
        Specification<CollectionPoint> neighborhoodSpecification = new CollectionPointSpecification(neighborhoodCriteria);

        Specification<CollectionPoint> typeSpecification = (root, query, criteriaBuilder) -> {
            if (collectionPointFilterDTO.type() == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), collectionPointFilterDTO.type());
        };

        Specification<CollectionPoint> notDeletedSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));

        return Specification.where(nameSpecification)
                .and(citySpecification)
                .and(neighborhoodSpecification)
                .and(typeSpecification)
                .and(notDeletedSpecification);
    }

    public Page<CollectionPointDTO> generateCollectionPointDTOPage(Page<CollectionPoint> collectionPointPage) {
        return collectionPointPage.map(this::generateCollectionPointDTO);
    }

    public List<CollectionPointDTO> generateCollectionPointList(List<CollectionPoint> collectionPointList) {
        return collectionPointList.stream().map(collectionPointDTOMapper::convert).toList();
    }

    public CollectionPoint getOrThrowException(Long id) {
        return collectionPointRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("CollectionPoint", id)
        );
    }
}
