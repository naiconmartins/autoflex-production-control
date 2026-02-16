package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.autoflex.application.commands.ProductCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.gateways.ProductRepository;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.application.usecases.ProductUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.Product;
import org.autoflex.domain.RawMaterial;

@ApplicationScoped
public class ProductServiceImpl implements ProductUseCase {

  @Inject ProductRepository productRepository;
  @Inject RawMaterialRepository rawMaterialRepository;

  @Override
  @Transactional
  public Product insert(ProductCommand cmd) {
    if (productRepository.findByCode(cmd.code()).isPresent()) {
      throw new ConflictException("Product code already exists");
    }

    Product product = new Product(cmd.code(), cmd.name(), cmd.price());

    if (cmd.rawMaterials() != null && !cmd.rawMaterials().isEmpty()) {
      for (var item : cmd.rawMaterials()) {
        RawMaterial rm =
            rawMaterialRepository
                .findById(item.rawMaterialId())
                .orElseThrow(
                    () ->
                        new ResourceNotFoundException(
                            "Raw Material not found: " + item.rawMaterialId()));

        product.addRawMaterial(rm, item.requiredQuantity());
      }
    }

    return productRepository.save(product);
  }

  @Override
  @Transactional
  public Product update(Long id, ProductCommand cmd) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Product with id " + id + " not found"));

    productRepository
        .findByCode(cmd.code())
        .ifPresent(
            other -> {
              if (!other.getId().equals(id)) {
                throw new ConflictException("Product code already exists");
              }
            });

    product.updateData(cmd.code(), cmd.name(), cmd.price());

    product.clearRawMaterials();

    if (cmd.rawMaterials() != null) {
      for (var item : cmd.rawMaterials()) {
        RawMaterial rm =
            rawMaterialRepository
                .findById(item.rawMaterialId())
                .orElseThrow(
                    () ->
                        new ResourceNotFoundException(
                            "Raw Material not found: " + item.rawMaterialId()));

        product.addRawMaterial(rm, item.requiredQuantity());
      }
    }

    return productRepository.save(product);
  }

  @Transactional
  public void delete(Long id) {
    productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

    try {
      productRepository.delete(id);
    } catch (Exception e) {
      throw new DatabaseException(
          "Cannot delete product because it is referenced by other records");
    }
  }

  @Override
  public PagedModel<Product> findAll(SearchQuery query) {
    return productRepository.findAll(query);
  }

  @Override
  public Product findById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
  }

  @Override
  public PagedModel<Product> findByName(String name, SearchQuery query) {
    if (name == null || name.isBlank()) {
      return new PagedModel<>(List.of(), 0, 0);
    }
    return productRepository.findByName(name, query);
  }
}
