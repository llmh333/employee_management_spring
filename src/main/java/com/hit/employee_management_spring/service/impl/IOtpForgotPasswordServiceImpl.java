package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.cache.OtpCache;
import com.hit.employee_management_spring.domain.cache.OtpTokenCache;
import com.hit.employee_management_spring.domain.dto.request.ConfirmNewPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.dto.response.VerifiedOtpCodeResponseDto;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.OtpCacheRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.service.IMailSenderService;
import com.hit.employee_management_spring.service.IOtpForgotPasswordService;
import com.hit.employee_management_spring.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class IOtpForgotPasswordServiceImpl implements IOtpForgotPasswordService {

    private static final long OTP_TTL_SECONDS = 300; // 5 minutes
    private static final long TOKEN_TTL_SECONDS = 300; // 5 minutes

    private final OtpCacheRepository otpCacheRepository;
    private final UserRepository userRepository;
    private final IMailSenderService mailSenderService;
    private final IUserService userService;

    @Override
    public String generateOtpCode(User user) {
        OtpCache existing = otpCacheRepository.getOtp(user.getEmail());
        if (existing != null) {
            throw new DuplicateDataException(ErrorMessage.Auth.DELAY_GET_OTP,
                    new String[]{existing.getExpiryAt().toString()});
        }
        SecureRandom secureRandom = new SecureRandom();
        int otpInt = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(otpInt);
    }

    @Override
    public OtpResponseDto sendOtpCode(String receivedEmail) {
        User user = userRepository.findByEmail(receivedEmail);
        if (user == null) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_EMAIL, new String[]{receivedEmail});
        }
        String otp = generateOtpCode(user);
        log.info("Generated OTP for email: {}", receivedEmail);

        String subject = "OTP Forgot Password";
        String content = "Your OTP code is: " + otp + ". It expires in 5 minutes.";
        boolean sent = mailSenderService.sendMail(receivedEmail, subject, content);

        if (sent) {
            LocalDateTime expiryAt = LocalDateTime.now().plusSeconds(OTP_TTL_SECONDS);
            OtpCache otpCache = OtpCache.builder()
                    .email(receivedEmail)
                    .otpCode(otp)
                    .expiryAt(expiryAt)
                    .build();
            otpCacheRepository.saveOtp(receivedEmail, otpCache, OTP_TTL_SECONDS);
            return OtpResponseDto.builder()
                    .otpCode(otp)
                    .expiryDateOtpCode(expiryAt)
                    .build();
        }
        return null;
    }

    @Override
    public VerifiedOtpCodeResponseDto verifyOtpCode(String email, String otpCode) {
        OtpCache otpCache = otpCacheRepository.getOtp(email);
        if (otpCache == null) {
            throw new NotFoundException(ErrorMessage.Auth.OTP_CODE_NOT_FOUND, new String[]{otpCode});
        }
        if (!otpCache.getOtpCode().equals(otpCode)) {
            throw new BadRequestException(ErrorMessage.Auth.OTP_CODE_NOT_FOUND);
        }
        // TTL handles expiry, but double-check
        if (LocalDateTime.now().isAfter(otpCache.getExpiryAt())) {
            otpCacheRepository.deleteOtp(email);
            throw new BadRequestException(ErrorMessage.Auth.OTP_CODE_EXPIRED);
        }

        String token = UUID.randomUUID().toString() + System.currentTimeMillis();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(TOKEN_TTL_SECONDS);

        OtpTokenCache tokenCache = OtpTokenCache.builder()
                .email(email)
                .expiryAt(tokenExpiry)
                .build();
        otpCacheRepository.saveToken(token, tokenCache, TOKEN_TTL_SECONDS);
        otpCacheRepository.deleteOtp(email); // invalidate OTP after use

        return new VerifiedOtpCodeResponseDto(token, tokenExpiry);
    }

    @Override
    public boolean confirmNewPassword(ConfirmNewPasswordRequestDto requestDto) {
        String token = requestDto.getTokenChangePassword();
        String newPassword = requestDto.getNewPassword();
        String confirmNewPassword = requestDto.getConfirmNewPassword();

        OtpTokenCache tokenCache = otpCacheRepository.getToken(token);
        if (tokenCache == null) {
            throw new NotFoundException(ErrorMessage.Auth.OTP_CODE_NOT_FOUND, new String[]{token});
        }
        if (LocalDateTime.now().isAfter(tokenCache.getExpiryAt())) {
            otpCacheRepository.deleteToken(token);
            throw new BadRequestException(ErrorMessage.Auth.OTP_CODE_EXPIRED);
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException(ErrorMessage.Validation.PASSWORD_NOT_MATCH);
        }
        userService.changePassword(tokenCache.getEmail(), newPassword, confirmNewPassword);
        otpCacheRepository.deleteToken(token);
        return true;
    }
}
