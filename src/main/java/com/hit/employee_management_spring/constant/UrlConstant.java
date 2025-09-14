package com.hit.employee_management_spring.constant;

public class UrlConstant {

    public static final String BASE_URL = "/api/v1";

    public static class Auth {
        public static final String PRE_FIX = "/auth";
        public static final String REGISTER = PRE_FIX + "/register";
    }

    public static final String[] PUBLIC_END_POINTS = {
            BASE_URL + Auth.REGISTER
    };
}
