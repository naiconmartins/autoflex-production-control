package org.autoflex.adapters.outbound.persistence.jpa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaUserEntity;
import org.autoflex.adapters.outbound.persistence.jpa.mappers.UserPersistenceMapper;
import org.autoflex.adapters.outbound.persistence.jpa.repositories.JpaUserRepository;
import org.autoflex.application.gateways.UserRepository;
import org.autoflex.domain.User;

@ApplicationScoped
public class UserPersistenceAdapter implements UserRepository {

  @Inject JpaUserRepository jpaUserRepository;
  @Inject UserPersistenceMapper mapper;

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaUserRepository.find("email", email).firstResultOptional().map(mapper::toDomain);
  }

  @Override
  public User save(User user) {
    JpaUserEntity entity = mapper.toEntity(user);
    jpaUserRepository.persist(entity);
    return mapper.toDomain(entity);
  }
}
