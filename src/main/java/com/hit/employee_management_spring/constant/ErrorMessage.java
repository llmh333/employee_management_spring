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
    }
}
