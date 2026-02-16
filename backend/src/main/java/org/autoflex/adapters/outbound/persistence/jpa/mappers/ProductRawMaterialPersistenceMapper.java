package org.autoflex.adapters.outbound.persistence.jpa.mappers;

import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductEntity;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductRawMaterialEntity;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi")
public interface ProductRawMaterialPersistenceMapper {

  @Mapping(target = "product", qualifiedByName = "toDomainProductShallow")
  ProductRawMaterial toDomain(JpaProductRawMaterialEntity entity);

  @Mapping(target = "product", qualifiedByName = "toEntityProductShallow")
  JpaProductRawMaterialEntity toEntity(ProductRawMaterial prm);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", qualifiedByName = "toEntityProductShallow")
  void updateEntityFromDomain(
      ProductRawMaterial domain, @MappingTarget JpaProductRawMaterialEntity entity);

  @Named("toDomainProductShallow")
  @Mapping(target = "rawMaterials", ignore = true)
  Product toDomainProductShallow(JpaProductEntity entity);

  @Named("toEntityProductShallow")
  @Mapping(target = "rawMaterials", ignore = true)
  JpaProductEntity toEntityProductShallow(Product product);
}
