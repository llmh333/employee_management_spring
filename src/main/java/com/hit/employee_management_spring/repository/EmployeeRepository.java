package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUserId(String userId);
    boolean existsByEmployeeCode(String employeeCode);

    @Query("SELECT e FROM Employee e JOIN e.user u JOIN e.position p JOIN p.department d " +
           "WHERE (:keyword IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);
}
