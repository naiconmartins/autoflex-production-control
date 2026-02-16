package org.autoflex.adapters.outbound.persistence.jpa.mappers;

import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductEntity;
import org.autoflex.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

  Product toDomain(JpaProductEntity entity);

  JpaProductEntity toEntity(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  void updateEntityFromDomain(Product domain, @MappingTarget JpaProductEntity entity);
}
