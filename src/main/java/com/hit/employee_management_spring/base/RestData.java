package com.hit.employee_management_spring.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestData<T> {

    private RestStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private LocalDateTime timestamp;

    public RestData(T data) {
        this.status = RestStatus.SUCCESS;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static RestData error(Object message) {
        return new RestData(RestStatus.ERROR, message, null, LocalDateTime.now());
    }
}
