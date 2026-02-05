package org.autoflex.infra.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.domain.entities.RawMaterial;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {
}
