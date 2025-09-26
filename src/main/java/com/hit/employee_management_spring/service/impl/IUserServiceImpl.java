package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.*;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PagingMetadata;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.Role;
import com.hit.employee_management_spring.domain.entity.TokenBlacklist;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.entity.UserSession;
import com.hit.employee_management_spring.domain.mapper.UserMapper;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.ForbiddenException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.RoleRepository;
import com.hit.employee_management_spring.repository.TokenBlacklistRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.repository.UserSessionRepository;
import com.hit.employee_management_spring.security.UserPrincipal;
import com.hit.employee_management_spring.service.IUserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hit.employee_management_spring.constant.ErrorMessage.Validation.FIELD_NOT_BLANK;

@Service
@RequiredArgsConstructor
@Log4j2
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionRepository userSessionRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto addNewUser(RegisterUserRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateDataException(ErrorMessage.User.USERNAME_ALREADY_EXIST);
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateDataException(ErrorMessage.User.EMAIL_ALREADY_EXIST);
        }

        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new BadRequestException(ErrorMessage.Validation.PASSWORD_NOT_MATCH);
        }

        Role defaultRole = roleRepository.findByName(RoleConstant.ROLE_USER.name());
        List<Role> roles = new ArrayList<>();
        roles.add(defaultRole);

        User newUser = userMapper.toUser(requestDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(roles);

        return userMapper.toUserResponseDto(userRepository.save(newUser));
    }

    @Override
    public UserResponseDto changePassword(String email, String newPassword, String confirmNewPassword) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_EMAIL, new String[]{email});
        }

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!user.getId().equals(userPrincipal.getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN);
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException(ErrorMessage.Auth.BOTH_NEW_PASSWORD_NOT_MATCH);
        }

        userRepository.save(user);

        user.setPassword(passwordEncoder.encode(newPassword));
        List<UserSession> userSessionList = userSessionRepository.findAllByUser(user);
        userSessionList.forEach(userSession -> {
            TokenBlacklist accessTokenBlacklist = new TokenBlacklist();
            accessTokenBlacklist.setToken(userSession.getAccessToken());
            accessTokenBlacklist.setTypeToken(TypeToken.ACCESS_TOKEN);
            accessTokenBlacklist.setExpiryDate(LocalDateTime.now().plusDays(7));

            TokenBlacklist refreshTokenBlacklist = new TokenBlacklist();
            refreshTokenBlacklist.setToken(userSession.getAccessToken());
            refreshTokenBlacklist.setTypeToken(TypeToken.ACCESS_TOKEN);
            refreshTokenBlacklist.setExpiryDate(LocalDateTime.now().plusDays(7));

            tokenBlacklistRepository.save(accessTokenBlacklist);
            tokenBlacklistRepository.save(refreshTokenBlacklist);

            userSessionRepository.delete(userSession);
        });

        return null;
    }

    @Override
    public boolean deleteByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_ID, new String[]{userId});
        }

        userRepository.deleteById(userId);

        return true;
    }

    @Override
    public UserResponseDto getUserById(String userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_ID, new String[]{userId})
        );

        return userMapper.toUserResponseDto(user);
    }

    @Override
    public PaginationResponseDto getAllUser(PaginationFullRequestDto requestDto) {

        int pageNum = requestDto.getPageNum();
        int pageSize = requestDto.getPageSize();
        String keyword = requestDto.getKeyWords();

        Sort sort;
        if (requestDto.getIsAscending()) {
            sort = Sort.by(requestDto.getSortBy(SortByConstant.USER)).ascending();
        } else {
            sort = Sort.by(requestDto.getSortBy(SortByConstant.USER)).descending();
        }
        Pageable pageable = (Pageable) PageRequest.of(pageNum, pageSize, sort);

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDto> userResponseDtoList = userPage.getContent().stream().map(userMapper::toUserResponseDto).toList();

        PagingMetadata pagingMetadata = new PagingMetadata();
        pagingMetadata.setPageNum(pageNum);
        pagingMetadata.setPageSize(pageSize);
        pagingMetadata.setTotalPages(userPage.getTotalPages());
        pagingMetadata.setTotalElements(userResponseDtoList.stream().count());
        pagingMetadata.setSortBy(requestDto.getSortBy());
        pagingMetadata.setSortType(requestDto.getIsAscending() ? SortType.ASC.name() : SortType.DESC.name());

        return new PaginationResponseDto(pagingMetadata, userResponseDtoList);
    }

    @Override
    public UserResponseDto updateUser(UpdateUserRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_ID, new String[]{requestDto.getId()})
        );

        if (!requestDto.getFirstName().trim().isEmpty()) {
            user.setFirstName(requestDto.getFirstName());
        }

        if (!requestDto.getLastName().trim().isEmpty()) {
            user.setLastName(requestDto.getLastName());
        }

        if (!requestDto.getGender().trim().isEmpty()) {
            if (Gender.FEMALE.name().equals(requestDto.getGender())) {
                user.setGender(Gender.FEMALE);
            } else if (Gender.MALE.name().equals(requestDto.getGender())) {
                user.setGender(Gender.MALE);
            } else {
                user.setGender(Gender.OTHER);
            }
        }

        return userMapper.toUserResponseDto(userRepository.save(user));

    }
}
