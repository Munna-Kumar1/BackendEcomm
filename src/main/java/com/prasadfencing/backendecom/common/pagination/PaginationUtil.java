package com.prasadfencing.backendecom.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public class PaginationUtil {

    public static <T> PageResponse<T> toPage(Page<T> page) {

        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}