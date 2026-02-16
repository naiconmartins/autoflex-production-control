package org.autoflex.adapters.outbound.persistence.jpa.mappers;

import java.util.List;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductEntity;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductRawMaterialEntity;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.mapstruct.*;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

  @Mapping(target = "rawMaterials", qualifiedByName = "toDomainRawMaterialList")
  Product toDomain(JpaProductEntity entity);

  @Mapping(target = "rawMaterials", qualifiedByName = "toEntityRawMaterialList")
  JpaProductEntity toEntity(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "rawMaterials", qualifiedByName = "toEntityRawMaterialList")
  void updateEntityFromDomain(Product domain, @MappingTarget JpaProductEntity entity);

  @Named("toDomainRawMaterial")
  @Mapping(target = "product", ignore = true)
  ProductRawMaterial toDomainRawMaterial(JpaProductRawMaterialEntity entity);

  @Named("toDomainRawMaterialList")
  @IterableMapping(qualifiedByName = "toDomainRawMaterial")
  List<ProductRawMaterial> toDomainRawMaterialList(List<JpaProductRawMaterialEntity> entities);

  @Named("toEntityRawMaterial")
  @Mapping(target = "product", ignore = true)
  JpaProductRawMaterialEntity toEntityRawMaterial(ProductRawMaterial prm);

  @Named("toEntityRawMaterialList")
  @IterableMapping(qualifiedByName = "toEntityRawMaterial")
  List<JpaProductRawMaterialEntity> toEntityRawMaterialList(List<ProductRawMaterial> rawMaterials);

  @AfterMapping
  default void linkParentInDomain(@MappingTarget Product product) {
    if (product.getRawMaterials() != null) {
      for (ProductRawMaterial item : product.getRawMaterials()) {
        item.setProduct(product);
      }
    }
  }

  @AfterMapping
  default void linkParentInEntity(@MappingTarget JpaProductEntity product) {
    if (product.getRawMaterials() != null) {
      for (JpaProductRawMaterialEntity item : product.getRawMaterials()) {
        item.setProduct(product);
      }
    }
  }
}
