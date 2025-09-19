package com.hit.employee_management_spring.constant;

public class ErrorMessage {

    public static final String INTERNAL_SERVER_ERROR = "exception.internal.server";

    public static class Validation {
        public static final String USERNAME = "exception.validation.username";
        public static final String PASSWORD = "exception.validation.password";
        public static final String EMAIL = "exception.validation.email";
        public static final String FIELD_NOT_BLANK = "exception.validation.not.blank.field";
        public static final String PASSWORD_NOT_MATCH = "exception.validation.both.password.not.match";
    }

    public static class User {
        public static final String USERNAME_ALREADY_EXIST = "exception.already.exist.username";
        public static final String EMAIL_ALREADY_EXIST = "exception.already.exist.email";
        public static final String NOT_FOUND_BY_USERNAME = "exception.user.not.found.username";
        public static final String NOT_FOUND_BY_EMAIL = "exception.user.not.found.email";
    }

    public static class Auth {
        public static final String USERNAME_OR_PASSWORD_WRONG = "exception.auth.username.or.password.wrong";
        public static final String UNAUTHENTICATED = "exception.auth.unauthenticated";
        public static final String DELAY_GET_OTP = "exception.auth.delay.get.otp";
        public static final String OTP_CODE_NOT_FOUND = "exception.auth.otp.code.not.found";
        public static final String OLD_PASSWORD_WRONG = "exception.auth.old.password.wrong";
        public static final String BOTH_NEW_PASSWORD_NOT_MATCH = "exception.auth.both.password.not.match";
    }
}
