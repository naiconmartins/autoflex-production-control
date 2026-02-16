package org.autoflex.adapters.outbound.persistence.jpa;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.autoflex.adapters.outbound.persistence.jpa.entities.JpaProductRawMaterialEntity;
import org.autoflex.adapters.outbound.persistence.jpa.mappers.ProductRawMaterialPersistenceMapper;
import org.autoflex.adapters.outbound.persistence.jpa.repositories.JpaProductRawMaterialRepository;
import org.autoflex.application.gateways.ProductRawMaterialRepository;
import org.autoflex.domain.ProductRawMaterial;

public class ProductRawMaterialPersistenceAdapter implements ProductRawMaterialRepository {

  @Inject JpaProductRawMaterialRepository jpaProductRawMaterialRepository;
  @Inject ProductRawMaterialPersistenceMapper mapper;

  @Override
  public ProductRawMaterial save(ProductRawMaterial domain) {
    JpaProductRawMaterialEntity entity;

    if (domain.getId() != null) {
      entity = jpaProductRawMaterialRepository.findById(domain.getId());
      mapper.updateEntityFromDomain(domain, entity);
    } else {
      entity = mapper.toEntity(domain);
    }

    jpaProductRawMaterialRepository.persistAndFlush(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<ProductRawMaterial> findByProductAndRawMaterial(
      Long productId, Long rawMaterialId) {
    return jpaProductRawMaterialRepository
        .find("product.id = ?1 and rawMaterial.id = ?2", productId, rawMaterialId)
        .firstResultOptional()
        .map(mapper::toDomain);
  }

  @Override
  public void delete(Long id) {
    jpaProductRawMaterialRepository.deleteById(id);
  }

  @Override
  public Optional<ProductRawMaterial> findById(Long id) {
    return jpaProductRawMaterialRepository.findByIdOptional(id).map(mapper::toDomain);
  }

  @Override
  public List<ProductRawMaterial> listByProduct(Long productId) {
    return jpaProductRawMaterialRepository.find("product.id", productId).list().stream()
        .map(mapper::toDomain)
        .toList();
  }
}
