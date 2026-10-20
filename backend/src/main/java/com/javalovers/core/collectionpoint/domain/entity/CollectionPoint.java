package com.javalovers.core.collectionpoint.domain.entity;

import com.javalovers.common.entity.SoftDeletable;
import com.javalovers.core.collectionpoint.domain.enums.CollectionPointType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "collection_point")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionPoint implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_point_id")
    private Long collectionPointId;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotNull
    @Convert(converter = CollectionPointType.CollectionPointTypeConverter.class)
    @Column(name = "type")
    private CollectionPointType type;

    @Column(name = "street")
    private String street;

    @Column(name = "number")
    private String number;

    @Column(name = "neighborhood")
    private String neighborhood;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "operating_hours")
    private String operatingHours;

    @Column(name = "phone")
    private String phone;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
