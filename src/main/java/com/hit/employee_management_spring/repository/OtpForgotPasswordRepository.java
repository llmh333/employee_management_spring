package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.entity.OtpForgotPassword;
import com.hit.employee_management_spring.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OtpForgotPasswordRepository extends JpaRepository<OtpForgotPassword,Long> {

    OtpForgotPassword findByOtpCode(String otpCode);

    OtpForgotPassword findByUser(User user);

    OtpForgotPassword findByTokenChangePassword(String tokenChangePassword);

    @Transactional
    @Procedure(procedureName = "sp_delete_expired_otp_code")
    void deleteExpiredOtpCode();
}
