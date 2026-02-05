package org.autoflex.infra.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.domain.entities.Product;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
}
