package com.hit.employee_management_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class EmployeeManagementSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementSpringApplication.class, args);
    }

}
