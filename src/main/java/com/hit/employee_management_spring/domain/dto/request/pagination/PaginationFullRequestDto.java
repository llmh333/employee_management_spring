package com.hit.employee_management_spring.domain.dto.request.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationFullRequestDto extends PaginationSortRequestDto {

    private String keywords = "";

    public String getKeyWords() {
        return keywords.trim();
    }
}
