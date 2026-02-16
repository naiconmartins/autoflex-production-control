package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.autoflex.application.commands.ProductRawMaterialCommand;
import org.autoflex.application.gateways.ProductRawMaterialRepository;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.application.usecases.ProductRawMaterialUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.Product;
import org.autoflex.domain.ProductRawMaterial;
import org.autoflex.domain.RawMaterial;

@ApplicationScoped
public class ProductRawMaterialServiceImpl implements ProductRawMaterialUseCase {

  @Inject ProductRawMaterialRepository repository;
  @Inject ProductRepository productRepository;
  @Inject RawMaterialRepository rawMaterialRepository;

  @Override
  @Transactional
  public ProductRawMaterial add(Long productId, ProductRawMaterialCommand cmd) {
    Product product = fetchProduct(productId);
    RawMaterial rawMaterial = fetchRawMaterial(cmd.rawMaterialId());

    repository
        .findByProductAndRawMaterial(productId, cmd.rawMaterialId())
        .ifPresent(
            link -> {
              throw new ConflictException("Raw material already linked to product");
            });

    ProductRawMaterial link = new ProductRawMaterial(product, rawMaterial, cmd.requiredQuantity());
    return repository.save(link);
  }

  @Override
  public List<ProductRawMaterial> listByProduct(Long productId) {
    fetchProduct(productId);
    return repository.listByProduct(productId);
  }

  @Override
  @Transactional
  public ProductRawMaterial updateRequiredQuantity(
      Long productId, Long rawMaterialId, ProductRawMaterialCommand cmd) {
    ProductRawMaterial link =
        repository
            .findByProductAndRawMaterial(productId, rawMaterialId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        String.format(
                            "Association not found between Product %d and Raw Material %d",
                            productId, rawMaterialId)));

    link.setRequiredQuantity(cmd.requiredQuantity());
    return repository.save(link);
  }

  @Override
  @Transactional
  public void remove(Long productId, Long rawMaterialId) {
    ProductRawMaterial link =
        repository
            .findByProductAndRawMaterial(productId, rawMaterialId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        String.format(
                            "Association not found between Product ID %d and Raw Material ID %d",
                            productId, rawMaterialId)));

    repository.delete(link.getId());
  }

  private Product fetchProduct(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
  }

  private RawMaterial fetchRawMaterial(Long id) {
    return rawMaterialRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Raw material with id " + id + " not found"));
  }
}
