package org.autoflex.adapters.outbound.persistence.jpa.mappers;

import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaRawMaterialEntity;
import org.autoflex.domain.RawMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface RawMaterialPersistenceMapper {

  RawMaterial toDomain(JpaRawMaterialEntity entity);

  JpaRawMaterialEntity toEntity(RawMaterial dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  void updateEntityFromDomain(RawMaterial domain, @MappingTarget JpaRawMaterialEntity entity);
}
