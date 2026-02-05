package org.autoflex.web.dto;

import io.quarkus.hibernate.orm.panache.PanacheQuery;

import java.util.List;

public class PageResponseDTO<T> {
    public List<T> items;
    public long totalElements;
    public int totalPages;
    public int page;
    public int size;

    public static <T> PageResponseDTO<T> of(
            PanacheQuery<?> query,
            List<T> items,
            int page,
            int size
    ) {
        PageResponseDTO<T> r = new PageResponseDTO<>();
        r.items = items;
        r.totalElements = query.count();
        r.totalPages = query.pageCount();
        r.page = page;
        r.size = size;
        return r;
    }
}
