package com.hit.employee_management_spring.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateDataException extends BaseException {

    public DuplicateDataException(String message) {
        super(message);
    }

    public DuplicateDataException(String message, String[] params) {
        super(message, params);
    }
}
