package com.hit.employee_management_spring.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {

    public static ResponseEntity<RestData<?>> success(Object data) {
        return success(data, HttpStatus.OK);
    }

    public static ResponseEntity<RestData<?>> success(Object data, HttpStatus httpStatus) {
        RestData<?> restData = new RestData<>(data);
        return  new ResponseEntity<>(restData, httpStatus);
    }

    public static ResponseEntity<RestData<?>> error(Object message, HttpStatus httpStatus) {
        RestData<?> restData = RestData.error(message);
        return new ResponseEntity<>(restData, httpStatus);
    }

}
