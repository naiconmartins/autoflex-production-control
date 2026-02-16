package org.autoflex.application.gateways;

import java.util.List;
import java.util.Optional;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.domain.Product;

public interface ProductRepository {
  Product save(Product product);

  Optional<Product> findByCode(String code);

  Optional<Product> findById(Long id);

  void delete(Long id);

  PagedModel<Product> findAll(SearchQuery query);

  PagedModel<Product> findByName(String name, SearchQuery query);

  List<Product> findAllOrderedByPriceDesc();
}
