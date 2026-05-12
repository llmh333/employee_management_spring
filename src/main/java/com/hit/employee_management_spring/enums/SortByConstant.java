package com.hit.employee_management_spring.enums;

import lombok.Getter;
import com.hit.employee_management_spring.constant.ISortBy;

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
    },
    EMPLOYEE {
        @Override
        public String getSortBy(String sortBy) {
            switch (sortBy) {
                case "employeeCode":
                    return "employee_code";
                case "hireDate":
                    return "hire_date";
                case "lastModifiedAt":
                    return "last_modified_at";
                default:
                    return "id";
            }
        }
    }
}
