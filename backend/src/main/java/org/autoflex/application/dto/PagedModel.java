package org.autoflex.application.dto;

import java.util.List;

public record PagedModel<T>(List<T> items, long totalElements, int totalPages) {}
