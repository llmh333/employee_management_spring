package com.hit.employee_management_spring.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private String message;
    private String[] params;
    private HttpStatus status;


    public NotFoundException(String message) {
        this.message = message;
        this.status = HttpStatus.NOT_FOUND;
    }

    public NotFoundException(String message, String[] params) {
        this.message = message;
        this.params = params;
        this.status = HttpStatus.NOT_FOUND;
    }
}
