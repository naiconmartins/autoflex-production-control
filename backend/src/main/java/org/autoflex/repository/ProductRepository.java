package org.autoflex.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.autoflex.entity.Product;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
}
