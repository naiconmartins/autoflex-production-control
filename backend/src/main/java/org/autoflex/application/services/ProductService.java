package org.autoflex.application.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.*;
import org.autoflex.domain.entities.Product;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import io.quarkus.panache.common.Page;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import java.util.Optional;

@ApplicationScoped
public class ProductService {

    @Transactional
    public ProductResponseDTO insert(ProductRequestDTO dto) {

        if (Product.find("code", dto.code).firstResult() != null)
            throw new ConflictException("Product code already exists");

        Product product = new Product(dto.code, dto.name, dto.price);

        product.persist();

        return getProductResponseDTO(dto, product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product entity = Product.findById(id);

        if (entity == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }

        Product otherEntity = Product.find("code", dto.code).firstResult();
        if (otherEntity != null && !otherEntity.getId().equals(entity.getId())) {
            throw new ConflictException("Product code already exists");
        }

        entity.setName(dto.name);
        entity.setPrice(dto.price);
        entity.setCode(dto.code);

        entity.getRawMaterials().clear();

        return getProductResponseDTO(dto, entity);
    }

    @Transactional
    public void delete(Long id) {
        Product entity = Product.findById(id);
        if (entity == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }

        try {
            Product.deleteById(id);
        } catch (PersistenceException e) {
            throw new DatabaseException("Cannot delete product because it is referenced by other records");
        }
    }

    public PageResponseDTO<ProductResponseDTO> findAll(PageRequestDTO dto) {
        Sort sort = "desc".equalsIgnoreCase(dto.direction)
                ? Sort.by(dto.sortBy).descending()
                : Sort.by(dto.sortBy).ascending();

        PanacheQuery<Product> query = Product.findAll(sort)
                .page(Page.of(dto.page, dto.size));

        return PageResponseDTO.of(
                query,
                query.list().stream()
                        .map(ProductResponseDTO::new)
                        .toList(),
                dto.page,
                dto.size
        );
    }

    public ProductResponseDTO findById(Long id) {
        Product entity = Product.findById(id);

        if (entity == null) throw new ResourceNotFoundException("Product with id " + id + " not found");

        return new ProductResponseDTO(entity);
    }

    @NonNull
    private ProductResponseDTO getProductResponseDTO(ProductRequestDTO dto, Product entity) {
        for (ProductRawMaterialRequestDTO productRawMaterial : dto.rawMaterials) {
            Optional<RawMaterial> rawMaterialOpt = RawMaterial.findByIdOptional(productRawMaterial.rawMaterialId);

            if (rawMaterialOpt.isEmpty())
                throw new ResourceNotFoundException("Raw material with id " + productRawMaterial.rawMaterialId + " not found");

            ProductRawMaterial prodRawMaterial = new ProductRawMaterial();

            prodRawMaterial.setProduct(entity);
            prodRawMaterial.setRawMaterial(rawMaterialOpt.get());
            prodRawMaterial.setRequiredQuantity(productRawMaterial.requiredQuantity);

            entity.addRawMaterial(prodRawMaterial);
        }

        return new ProductResponseDTO(entity);
    }
}
