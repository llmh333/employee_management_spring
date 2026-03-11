package com.hit.employee_management_spring.service;

public interface IRedisService {

    boolean put(Object key, Object value);

    boolean delete(Object key);
}
