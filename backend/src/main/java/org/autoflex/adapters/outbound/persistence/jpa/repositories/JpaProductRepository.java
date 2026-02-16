package org.autoflex.adapters.outbound.persistence.jpa.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductEntity;

@ApplicationScoped
public class JpaProductRepository implements PanacheRepository<JpaProductEntity> {}
