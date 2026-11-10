package com.javalovers.modules.collectionpoint.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Tipo de local cadastrado no mapa: ponto de coleta parceiro ou
 * sede de entidade assistencial.
 */
public enum CollectionPointType {
    PARTNER_COLLECTION_POINT("PARTNER_COLLECTION_POINT"),
    ASSISTANCE_ENTITY_HQ("ASSISTANCE_ENTITY_HQ");

    private final String databaseValue;

    CollectionPointType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public static CollectionPointType fromDatabaseValue(String databaseValue) {
        for (CollectionPointType type : CollectionPointType.values()) {
            if (type.databaseValue.equals(databaseValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + databaseValue);
    }

    @Converter
    public static class CollectionPointTypeConverter implements AttributeConverter<CollectionPointType, String> {
        @Override
        public String convertToDatabaseColumn(CollectionPointType type) {
            return type != null ? type.getDatabaseValue() : null;
        }

        @Override
        public CollectionPointType convertToEntityAttribute(String databaseValue) {
            return databaseValue != null ? CollectionPointType.fromDatabaseValue(databaseValue) : null;
        }
    }
}
