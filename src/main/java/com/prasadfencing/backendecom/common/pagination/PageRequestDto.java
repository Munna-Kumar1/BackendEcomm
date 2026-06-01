package com.prasadfencing.backendecom.common.pagination;

import lombok.Data;

@Data
public class PageRequestDto {

    private int page = 0;

    private int size = 10;

    private String sortBy = "createdAt";

    private String sortDir = "desc";
}
