package org.autoflex.application.usecases;

import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.domain.Product;

public interface ProductUseCase {

  Product insert(ProductCommand cmd);

  Product update(Long id, ProductCommand cmd);

  void delete(Long id);

  Product findById(Long id);

  PagedModel<Product> findAll(SearchQuery query);

  PagedModel<Product> findByName(String name, SearchQuery query);
}
