package org.autoflex.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.autoflex.application.commands.RawMaterialCommand;
import org.autoflex.application.dto.PagedModel;
import org.autoflex.application.dto.SearchQuery;
import org.autoflex.application.gateways.RawMaterialRepository;
import org.autoflex.application.usecases.RawMaterialUseCase;
import org.autoflex.common.exceptions.ConflictException;
import org.autoflex.common.exceptions.DatabaseException;
import org.autoflex.common.exceptions.ResourceNotFoundException;
import org.autoflex.domain.RawMaterial;

@ApplicationScoped
public class RawMaterialServiceImpl implements RawMaterialUseCase {

  @Inject RawMaterialRepository rawMaterialRepository;

  @Transactional
  public RawMaterial insert(RawMaterialCommand cmd) {
    rawMaterialRepository
        .findByCode(cmd.code())
        .ifPresent(
            rm -> {
              throw new ConflictException("Raw material code already exists");
            });

    RawMaterial rawMaterial = new RawMaterial(cmd.code(), cmd.name(), cmd.stockQuantity());

    return rawMaterialRepository.save(rawMaterial);
  }

  @Transactional
  public RawMaterial update(Long id, RawMaterialCommand cmd) {
    RawMaterial existing =
        rawMaterialRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Raw material with id " + id + " not found"));

    rawMaterialRepository
        .findByCode(cmd.code())
        .filter(rm -> !rm.getId().equals(id))
        .ifPresent(
            rm -> {
              throw new ConflictException("Raw material code already exists");
            });

    existing.setCode(cmd.code());
    existing.setName(cmd.name());
    existing.setStockQuantity(cmd.stockQuantity());

    return rawMaterialRepository.save(existing);
  }

  @Transactional
  public void delete(Long id) {
    rawMaterialRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Raw material with id " + id + " not found"));

    try {
      rawMaterialRepository.delete(id);
    } catch (Exception e) {
      throw new DatabaseException(
          "Cannot delete raw material because it is referenced by other records");
    }
  }

  public PagedModel<RawMaterial> findAll(SearchQuery query) {
    return rawMaterialRepository.findAll(query);
  }

  public RawMaterial findById(Long id) {
    return rawMaterialRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Raw material with id " + id + " not found"));
  }

  public PagedModel<RawMaterial> findByName(String name, SearchQuery query) {
    return rawMaterialRepository.findByName(name, query);
  }
}
