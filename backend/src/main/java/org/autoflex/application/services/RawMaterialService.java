package org.autoflex.application.services;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.autoflex.adapters.inbound.dto.request.PageRequestDTO;
import org.autoflex.adapters.inbound.dto.request.RawMaterialRequestDTO;
import org.autoflex.adapters.inbound.dto.response.PageResponseDTO;
import org.autoflex.adapters.inbound.dto.response.RawMaterialResponseDTO;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.RawMaterial;

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
      RawMaterial.getEntityManager().flush();
    } catch (PersistenceException e) {
      throw new DatabaseException(
          "Cannot delete raw material because it is referenced by other records");
    }
  }

  public PageResponseDTO<RawMaterialResponseDTO> findAll(PageRequestDTO dto) {
    Sort sort =
        "desc".equalsIgnoreCase(dto.direction)
            ? Sort.by(dto.sortBy).descending()
            : Sort.by(dto.sortBy).ascending();

    PanacheQuery<RawMaterial> query = RawMaterial.findAll(sort).page(Page.of(dto.page, dto.size));

    return PageResponseDTO.of(
        query, query.list().stream().map(RawMaterialResponseDTO::new).toList(), dto.page, dto.size);
  }

  public RawMaterialResponseDTO findById(Long id) {
    RawMaterial entity = RawMaterial.findById(id);

    if (entity == null)
      throw new ResourceNotFoundException("Raw material with id " + id + " not found");

    return new RawMaterialResponseDTO(entity);
  }

  public PageResponseDTO<RawMaterialResponseDTO> findByName(String name, PageRequestDTO dto) {
    String sortBy = (dto.sortBy == null || dto.sortBy.isBlank()) ? "name" : dto.sortBy;
    String direction = (dto.direction == null || dto.direction.isBlank()) ? "asc" : dto.direction;
    int page = Math.max(0, dto.page);
    int size = dto.size <= 0 ? 10 : dto.size;

    if (name == null || name.isBlank()) {
      return new PageResponseDTO<>(List.of(), 0L, 0, page, size);
    }

    Sort sort =
        "desc".equalsIgnoreCase(direction)
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    PanacheQuery<RawMaterial> query =
        RawMaterial.find("lower(name) like concat('%', lower(?1), '%')", sort, name)
            .page(Page.of(page, size));

    return PageResponseDTO.of(
        query, query.list().stream().map(RawMaterialResponseDTO::new).toList(), page, size);
  }
}
