package com.hit.employee_management_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class EmployeeManagementSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementSpringApplication.class, args);
    }

}
