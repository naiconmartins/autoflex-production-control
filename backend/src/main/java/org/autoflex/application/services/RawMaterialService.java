package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.autoflex.domain.entities.RawMaterial;
import org.autoflex.web.dto.*;
import org.autoflex.web.exceptions.ConflictException;
import org.autoflex.web.exceptions.DatabaseException;
import org.autoflex.web.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class RawMaterialService {

    @Transactional
    public RawMaterialResponseDTO insert(RawMaterialRequestDTO dto) {

        if (RawMaterial.find("code", dto.code).firstResultOptional().isPresent())
            throw new ConflictException("Raw material code already exists");

        RawMaterial rawMaterial = new RawMaterial(dto.code, dto.name, dto.stockQuantity);

        rawMaterial.persist();

        return new RawMaterialResponseDTO(rawMaterial);
    }

    @Transactional
    public RawMaterialResponseDTO update(Long id, RawMaterialRequestDTO dto) {
        RawMaterial entity = RawMaterial.findById(id);
        if (entity == null) {
            throw new ResourceNotFoundException("Raw material with id " + id + " not found");
        }

        RawMaterial otherEntity = RawMaterial.find("code", dto.code).firstResult();
        if (otherEntity != null && !otherEntity.getId().equals(entity.getId())) {
            throw new ConflictException("Raw material code already exists");
        }

        entity.setName(dto.name);
        entity.setCode(dto.code);
        entity.setStockQuantity(dto.stockQuantity);

        return new RawMaterialResponseDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial entity = RawMaterial.findById(id);
        if (entity == null) {
            throw new ResourceNotFoundException("Raw material with id " + id + " not found");
        }

        try {
            RawMaterial.deleteById(id);
        } catch (PersistenceException e) {
            throw new DatabaseException("Cannot delete raw material because it is referenced by other records");
        }
    }

    public PageResponseDTO<RawMaterialResponseDTO> findAll(PageRequestDTO dto) {
        Sort sort = "desc".equalsIgnoreCase(dto.direction)
                ? Sort.by(dto.sortBy).descending()
                : Sort.by(dto.sortBy).ascending();

        PanacheQuery<RawMaterial> query = RawMaterial.findAll(sort)
                .page(Page.of(dto.page, dto.size));

        return PageResponseDTO.of(
                query,
                query.list().stream()
                        .map(RawMaterialResponseDTO::new)
                        .toList(),
                dto.page,
                dto.size
        );
    }

    public RawMaterialResponseDTO findById(Long id) {
        RawMaterial entity = RawMaterial.findById(id);

        if (entity == null) throw new ResourceNotFoundException("Raw material with id " + id + " not found");

        return new RawMaterialResponseDTO(entity);
    }
}
