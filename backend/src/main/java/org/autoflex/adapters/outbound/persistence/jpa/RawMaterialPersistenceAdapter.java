package org.autoflex.adapters.outbound.persistence.jpa;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaRawMaterialEntity;
import org.autoflex.adapters.outbound.persistence.jpa.mappers.RawMaterialPersistenceMapper;
import org.autoflex.adapters.outbound.persistence.jpa.repositories.JpaRawMaterialRepository;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.domain.RawMaterial;

public class RawMaterialPersistenceAdapter implements RawMaterialRepository, JpaSortable {

  @Inject JpaRawMaterialRepository jpaRawMaterialRepository;
  @Inject RawMaterialPersistenceMapper mapper;

  @Override
  public RawMaterial save(RawMaterial domain) {
    JpaRawMaterialEntity entity;

    if (domain.getId() != null) {
      entity = jpaRawMaterialRepository.findById(domain.getId());
      mapper.updateEntityFromDomain(domain, entity);
    } else {
      entity = mapper.toEntity(domain);
    }

    jpaRawMaterialRepository.persistAndFlush(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<RawMaterial> findByCode(String code) {
    return jpaRawMaterialRepository.find("code", code).firstResultOptional().map(mapper::toDomain);
  }

  @Override
  public Optional<RawMaterial> findById(Long id) {
    return jpaRawMaterialRepository.findByIdOptional(id).map(entity -> mapper.toDomain(entity));
  }

  @Override
  public void delete(Long id) {}

  @Override
  public PagedModel<RawMaterial> findAll(SearchQuery query) {
    Sort sort = createSort(query);

    PanacheQuery<JpaRawMaterialEntity> panacheQuery =
        jpaRawMaterialRepository.findAll(sort).page(Page.of(query.page(), query.size()));

    List<RawMaterial> items = panacheQuery.list().stream().map(mapper::toDomain).toList();

    return new PagedModel<>(items, panacheQuery.count(), panacheQuery.pageCount());
  }

  @Override
  public PagedModel<RawMaterial> findByName(String name, SearchQuery query) {
    Sort sort = createSort(query);

    PanacheQuery<JpaRawMaterialEntity> panacheQuery =
        jpaRawMaterialRepository
            .find("lower(name) like concat('%', lower(?1), '%')", sort, name)
            .page(Page.of(query.page(), query.size()));

    List<RawMaterial> items = panacheQuery.list().stream().map(mapper::toDomain).toList();

    return new PagedModel<>(items, panacheQuery.count(), panacheQuery.pageCount());
  }

  @Override
  public List<RawMaterial> listAllRawMaterials() {
    return jpaRawMaterialRepository.listAll().stream().map(mapper::toDomain).toList();
  }
}
