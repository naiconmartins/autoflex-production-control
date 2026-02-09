package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.autoflex.domain.entities.Product;
import org.autoflex.domain.entities.ProductRawMaterial;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.ProductRawMaterialRequestDTO;
import org.autoflex.web.dto.ProductRawMaterialResponseDTO;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class ProductRawMaterialService {

    @Transactional
    public ProductRawMaterialResponseDTO add(Long productId, ProductRawMaterialRequestDTO dto) {
        Product product = getProduct(productId);
        RawMaterial rawMaterial = getRawMaterial(dto.id);

        if (ProductRawMaterial.find("product = ?1 and rawMaterial = ?2", product, rawMaterial).firstResultOptional().isPresent()) {
            throw new ConflictException("Raw material already linked to product");
        }

        ProductRawMaterial link = new ProductRawMaterial(product, rawMaterial, dto.requiredQuantity);
        link.persist();

        return new ProductRawMaterialResponseDTO(link);
    }

    public List<ProductRawMaterialResponseDTO> listByProduct(Long productId) {
        Product product = getProduct(productId);

        List<ProductRawMaterial> links = ProductRawMaterial.find("product", product).list();

        return links.stream().map(ProductRawMaterialResponseDTO::new).toList();
    }

    @Transactional
    public ProductRawMaterialResponseDTO updateRequiredQuantity(Long productId, Long rawMaterialId, ProductRawMaterialRequestDTO dto) {
        Product product = getProduct(productId);
        RawMaterial rawMaterial = getRawMaterial(rawMaterialId);

        ProductRawMaterial link = getLink(product, rawMaterial);
        link.setRequiredQuantity(dto.requiredQuantity);

        return new ProductRawMaterialResponseDTO(link);
    }

    @Transactional
    public void remove(Long productId, Long rawMaterialId) {
        Product product = getProduct(productId);
        RawMaterial rawMaterial = getRawMaterial(rawMaterialId);

        ProductRawMaterial link = getLink(product, rawMaterial);
        link.delete();
    }

    private Product getProduct(Long productId) {
        Product product = Product.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product with id " + productId + " not found");
        }
        return product;
    }

    private RawMaterial getRawMaterial(Long rawMaterialId) {
        RawMaterial rawMaterial = RawMaterial.findById(rawMaterialId);
        if (rawMaterial == null) {
            throw new ResourceNotFoundException("Raw material with id " + rawMaterialId + " not found");
        }
        return rawMaterial;
    }

    private ProductRawMaterial getLink(Product product, RawMaterial rawMaterial) {
        ProductRawMaterial link = ProductRawMaterial.find("product = ?1 and rawMaterial = ?2", product, rawMaterial).firstResult();
        if (link == null) {
            throw new ResourceNotFoundException("Raw material link not found for product " + product.getId());
        }
        return link;
    }
}
