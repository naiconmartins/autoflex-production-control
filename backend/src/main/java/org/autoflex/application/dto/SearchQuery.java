package org.autoflex.application.dto;

public record SearchQuery(int page, int size, String sortBy, String direction) {}
