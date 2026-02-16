package org.autoflex.adapters.outbound.persistence.jpa.mappers;

import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductRawMaterialEntity;
import org.autoflex.domain.ProductRawMaterial;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

public interface ProductRawMaterialPersistenceMapper {

  ProductRawMaterial toDomain(JpaProductRawMaterialEntity entity);

  JpaProductRawMaterialEntity toEntity(ProductRawMaterial prm);

  @Mapping(target = "id", ignore = true)
  void updateEntityFromDomain(ProductRawMaterial domain, @MappingTarget JpaProductRawMaterialEntity entity);
}
