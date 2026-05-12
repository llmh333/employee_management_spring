package com.hit.employee_management_spring.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus
public abstract class BaseException extends RuntimeException {
    private String message;
    private String[] params;
    private HttpStatus status;

    protected BaseException(String message) {
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
    }

    protected BaseException(String message, String[] params) {
        this.message = message;
        this.params = params;
    }
}
