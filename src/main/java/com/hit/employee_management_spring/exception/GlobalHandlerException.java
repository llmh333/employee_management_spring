package com.hit.employee_management_spring.exception;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.constant.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalHandlerException {

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((fieldError) -> {
           String message = messageSource.getMessage(fieldError.getDefaultMessage(),  null, LocaleContextHolder.getLocale());
           errors.put(fieldError.getField(), message);
        });

        return ApiResponseUtil.error(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DuplicateDataException.class)
    public ResponseEntity<?> handleDuplicateDataException(DuplicateDataException ex) {

        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        return ApiResponseUtil.error(message, ex.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {

        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        return ApiResponseUtil.error(message, ex.getStatus());
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getParams(), LocaleContextHolder.getLocale());
        return ApiResponseUtil.error(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        ex.printStackTrace();
        String message = messageSource.getMessage(ErrorMessage.INTERNAL_SERVER_ERROR, null, LocaleContextHolder.getLocale());
        return  ApiResponseUtil.error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
