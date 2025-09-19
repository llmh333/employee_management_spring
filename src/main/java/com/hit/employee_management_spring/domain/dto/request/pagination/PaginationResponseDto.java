package com.hit.employee_management_spring.domain.dto.request.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationResponseDto<T> {

    private PagingMetadata metadata;
    private List<T> items;
}
