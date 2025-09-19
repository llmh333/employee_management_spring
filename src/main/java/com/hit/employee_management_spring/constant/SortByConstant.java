package com.hit.employee_management_spring.constant;

import lombok.Getter;

@Getter
public enum SortByConstant implements ISortBy{
    USER {
        @Override
        public String getSortBy(String sortBy) {
            switch (sortBy) {
                case "firstName":
                    return "first_name";
                case "lastName":
                    return "last_name";
                case "lastModifiedAt":
                    return "last_modified_at";
                default:
                    return "id";
            }
        }
    }
}
