package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.PageRequestDTO;
import org.autoflex.web.dto.PageResponseDTO;
import org.autoflex.web.dto.ProductRequestDTO;
import org.autoflex.web.dto.ProductResponseDTO;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ProductService {

    @Transactional
    public ProductResponseDTO insert(ProductRequestDTO dto) {
        if (Product.find("code", dto.code).firstResultOptional().isPresent()) {
            throw new ConflictException("Product code already exists");
        }

        Product product = new Product(dto.code, dto.name, dto.price);
        product.persist();

        return new ProductResponseDTO(product);
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

        entity.setCode(dto.code);
        entity.setName(dto.name);
        entity.setPrice(dto.price);

        entity.getRawMaterials().clear();
        Product.getEntityManager().flush();

        if (dto.rawMaterials != null && !dto.rawMaterials.isEmpty()) {
            for (var itemDto : dto.rawMaterials) {
                RawMaterial rm = RawMaterial.findById(itemDto.id);
                if (rm == null) {
                    throw new ResourceNotFoundException("Raw Material not found with id: " + itemDto.id);
                }

                ProductRawMaterial association = new ProductRawMaterial(entity, rm, itemDto.requiredQuantity);
                entity.addRawMaterial(association);
            }
        }

        return new ProductResponseDTO(entity);
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

        PanacheQuery<Product> query = Product.findAll(sort).page(Page.of(dto.page, dto.size));

        return PageResponseDTO.of(
                query,
                query.list().stream().map(ProductResponseDTO::new).toList(),
                dto.page,
                dto.size
        );
    }

    public ProductResponseDTO findById(Long id) {
        Product entity = Product.findById(id);
        if (entity == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
        return new ProductResponseDTO(entity);
    }
}
