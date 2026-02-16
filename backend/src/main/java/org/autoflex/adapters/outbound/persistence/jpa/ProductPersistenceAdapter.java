package org.autoflex.adapters.outbound.persistence.jpa;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductEntity;
import org.autoflex.adapters.outbound.persistence.jpa.mappers.ProductPersistenceMapper;
import org.autoflex.adapters.outbound.persistence.jpa.repositories.JpaProductRepository;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.domain.Product;

public class ProductPersistenceAdapter implements ProductRepository, JpaSortable {

  @Inject JpaProductRepository jpaProductRepository;
  @Inject ProductPersistenceMapper mapper;

  @Override
  public Product save(Product domain) {
    JpaProductEntity entity;

    if (domain.getId() != null) {
      entity = jpaProductRepository.findById(domain.getId());
      mapper.updateEntityFromDomain(domain, entity);
    } else {
      entity = mapper.toEntity(domain);
    }

    jpaProductRepository.persistAndFlush(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<Product> findByCode(String code) {
    return jpaProductRepository.find("code", code).firstResultOptional().map(mapper::toDomain);
  }

  @Override
  public Optional<Product> findById(Long id) {
    return jpaProductRepository.findByIdOptional(id).map(entity -> mapper.toDomain(entity));
  }

  @Override
  public void delete(Long id) {}

  @Override
  public PagedModel<Product> findAll(SearchQuery query) {
    Sort sort = createSort(query);

    PanacheQuery<JpaProductEntity> panacheQuery =
        jpaProductRepository.findAll(sort).page(Page.of(query.page(), query.size()));

    List<Product> items = panacheQuery.list().stream().map(mapper::toDomain).toList();

    return new PagedModel<>(items, panacheQuery.count(), panacheQuery.pageCount());
  }

  @Override
  public PagedModel<Product> findByName(String name, SearchQuery query) {
    Sort sort = createSort(query);

    PanacheQuery<JpaProductEntity> panacheQuery =
        jpaProductRepository
            .find("lower(name) like concat('%', lower(?1), '%')", sort, name)
            .page(Page.of(query.page(), query.size()));

    List<Product> items = panacheQuery.list().stream().map(mapper::toDomain).toList();

    return new PagedModel<>(items, panacheQuery.count(), panacheQuery.pageCount());
  }
}
