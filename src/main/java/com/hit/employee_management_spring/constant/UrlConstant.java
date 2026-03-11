package com.hit.employee_management_spring.constant;

public class UrlConstant {

    public static final String BASE_URL = "/api/v1";

    public static class Auth {
        public static final String PRE_FIX = "/auth";
        public static final String LOGIN = PRE_FIX + "/login";
        public static final String REGISTER = PRE_FIX + "/register";
        public static final String LOGOUT = PRE_FIX + "/logout";
    }

    public static class ForgotPassword {
        public static final String PRE_FIX = "/forgot-password";
        public static final String GET_OTP_CODE = PRE_FIX + "/send-otp";
        public static final String VERIFY_OTP_CODE = PRE_FIX + "/verify-otp";
        public static final String CONFIRM_NEW_PASSWORD = PRE_FIX + "/confirm-new-password";
    }

    public static class User {
        public static final String PRE_FIX = "/users";
        public static final String ADD_NEW_USER = PRE_FIX;
        public static final String UPDATE_USER = PRE_FIX;
        public static final String DELETE_USER_BY_ID = PRE_FIX + "/{userId}";
        public static final String GET_ALL_USER = PRE_FIX;
        public static final String GET_USER_BY_ID = PRE_FIX + "/{userId}";
    }

    public static class Department {
        public static final String PRE_FIX = "/departments";
        public static final String CREATE = PRE_FIX;
        public static final String UPDATE = PRE_FIX;
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_ALL = PRE_FIX;
    }

    public static class Position {
        public static final String PRE_FIX = "/positions";
        public static final String CREATE = PRE_FIX;
        public static final String UPDATE = PRE_FIX;
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_DEPARTMENT = PRE_FIX + "/department/{departmentId}";
    }

    public static class Employee {
        public static final String PRE_FIX = "/employees";
        public static final String CREATE = PRE_FIX;
        public static final String UPDATE = PRE_FIX;
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_ALL = PRE_FIX;
    }

    public static final String[] PUBLIC_END_POINTS = {
            BASE_URL + Auth.LOGIN,
            BASE_URL + Auth.REGISTER,
            BASE_URL + ForgotPassword.GET_OTP_CODE,
            BASE_URL + ForgotPassword.VERIFY_OTP_CODE,
            BASE_URL + ForgotPassword.CONFIRM_NEW_PASSWORD,
    };
}
