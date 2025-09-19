package com.hit.employee_management_spring.service.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.ConfirmNewPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.dto.response.VerifiedOtpCodeResponseDto;
import com.hit.employee_management_spring.domain.entity.OtpForgotPassword;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.mapper.OtpForgotPasswordMapper;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.OtpForgotPasswordRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.security.JwtTokenProvider;
import com.hit.employee_management_spring.service.IMailSenderService;
import com.hit.employee_management_spring.service.IOtpForgotPasswordService;
import com.hit.employee_management_spring.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Log4j2
public class IOtpForgotPasswordServiceImpl implements IOtpForgotPasswordService {

    private final OtpForgotPasswordRepository  otpForgotPasswordRepository;
    private final UserRepository userRepository;
    private final IMailSenderService mailSenderService;
    private final IUserService userService;
    private final OtpForgotPasswordMapper otpForgotPasswordMapper;

    @Override
    public String generateOtpCode(User user) {

        OtpForgotPassword otpForgotPassword = otpForgotPasswordRepository.findByUser(user);
        if (otpForgotPassword != null) {
            if (LocalDateTime.now().isAfter(otpForgotPassword.getExpiryDateOtpCode())) {
                otpForgotPasswordRepository.delete(otpForgotPassword);
            } else {
                throw new DuplicateDataException(ErrorMessage.Auth.DELAY_GET_OTP, new String[]{otpForgotPassword.getExpiryDateOtpCode().toString()});
            }

        }
        log.info("Generating Otp Code for email: {} ", user.getEmail());
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        return String.valueOf(random.nextInt(999999));
    }

    @Override
    public OtpResponseDto sendOtpCode(String receivedEmail) {

        User user = userRepository.findByEmail(receivedEmail);
        if (user == null) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_EMAIL, new String[]{receivedEmail});
        }

        String otp = generateOtpCode(user);
        log.info("Generated OTP Code {} for email: {} ", otp, receivedEmail);

        String subject = "OTP Forgot Password";
        String content = "The OTP Code is: " + otp;
        boolean resultSendEmail = mailSenderService.sendMail(receivedEmail, subject, content);
        if (resultSendEmail) {
            OtpForgotPassword newOtpForgotPassword = new OtpForgotPassword();
            newOtpForgotPassword.setOtpCode(otp);
            newOtpForgotPassword.setExpiryDateOtpCode(LocalDateTime.now().plusMinutes(5));
            newOtpForgotPassword.setVerifiedOtpCode(false);
            newOtpForgotPassword.setUser(user);
            otpForgotPasswordRepository.save(newOtpForgotPassword);

            return otpForgotPasswordMapper.toOtpResponseDto(newOtpForgotPassword);
        }
        return null;
    }

    @Override
    public VerifiedOtpCodeResponseDto verifyOtpCode(String otpCode) {

        OtpForgotPassword otpForgotPassword = otpForgotPasswordRepository.findByOtpCode(otpCode);
        if (otpForgotPassword == null) {
            throw new NotFoundException(ErrorMessage.Auth.OTP_CODE_NOT_FOUND, new String[]{otpCode});
        }

        if (otpForgotPassword.getVerifiedOtpCode() == Boolean.TRUE) {
            throw new BadRequestException(ErrorMessage.Auth.UNAUTHENTICATED);
        }

        if (otpForgotPassword.getOtpCode().equals(otpCode)) {
            String tokenChangePassword = UUID.randomUUID().toString() + System.currentTimeMillis();
            LocalDateTime expiryDateToken = LocalDateTime.now().plusMinutes(5);
            otpForgotPassword.setVerifiedOtpCode(true);
            otpForgotPassword.setTokenChangePassword(tokenChangePassword);
            otpForgotPassword.setExpiryDateToken(expiryDateToken);

            otpForgotPasswordRepository.save(otpForgotPassword);
            return new VerifiedOtpCodeResponseDto(tokenChangePassword, expiryDateToken);
        }

        return null;
    }

    @Override
    public boolean confirmNewPassword(ConfirmNewPasswordRequestDto confirmNewPasswordRequestDto) {

        String token = confirmNewPasswordRequestDto.getTokenChangePassword();
        String newPassword = confirmNewPasswordRequestDto.getNewPassword();
        String confirmNewPassword = confirmNewPasswordRequestDto.getConfirmNewPassword();
        OtpForgotPassword otpForgotPassword = otpForgotPasswordRepository.findByTokenChangePassword(confirmNewPasswordRequestDto.getTokenChangePassword());
        if (otpForgotPassword == null) {
            throw new NotFoundException(ErrorMessage.Auth.OTP_CODE_NOT_FOUND, new String[]{token});
        }

        if (otpForgotPassword.getTokenChangePassword().equals(token)) {
            if (newPassword.equals(confirmNewPassword)) {
                userService.changePassword(otpForgotPassword.getUser().getEmail(), newPassword, confirmNewPassword);
                otpForgotPasswordRepository.delete(otpForgotPassword);
                return true;
            }
        }

        return false;
    }
}
