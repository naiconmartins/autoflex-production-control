package org.autoflex.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.entity.RawMaterial;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {
}
