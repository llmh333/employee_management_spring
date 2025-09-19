package com.hit.employee_management_spring.domain.mapper;

import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.entity.OtpForgotPassword;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OtpForgotPasswordMapper {

    OtpResponseDto toOtpResponseDto(OtpForgotPassword otpForgotPassword);

}
