package org.autoflex.adapters.outbound.persistence.jpa.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaUserEntity;

public class JpaUserRepository implements PanacheRepository<JpaUserEntity> {}
