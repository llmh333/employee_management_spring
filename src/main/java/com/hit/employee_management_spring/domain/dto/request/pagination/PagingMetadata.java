package com.hit.employee_management_spring.domain.dto.request.pagination;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingMetadata {

    private Long totalElements;
    private Integer totalPages;
    private Integer pageNum;
    private Integer pageSize;
    private String sortBy;
    private String sortType;
}
