package org.autoflex.adapters.outbound.persistence.jpa.mappers;

import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaUserEntity;
import org.autoflex.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserPersistenceMapper {
  User toDomain(JpaUserEntity entity);

  JpaUserEntity toEntity(User domain);
}
