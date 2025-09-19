package com.hit.employee_management_spring.domain.dto.request.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequestDto {

    private Integer pageNum = 0;
    private Integer pageSize = 0;

    public int getPageNum() {
        if (pageNum < 1) {
            pageNum = 1;
        }
        return pageNum - 1;
    }

    public int getPageSize() {
        if (pageSize < 1) {
            pageSize = 10;
        }
        return pageSize;
    }
}
