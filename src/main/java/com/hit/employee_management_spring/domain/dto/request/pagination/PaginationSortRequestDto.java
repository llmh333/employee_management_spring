package com.hit.employee_management_spring.domain.dto.request.pagination;

import com.hit.employee_management_spring.constant.SortByConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationSortRequestDto extends PaginationRequestDto{

    private String sortBy = "";
    private Boolean isAscending = Boolean.FALSE;

    public String getSortBy(SortByConstant constant) {
        return constant.getSortBy(sortBy);
    }
}
