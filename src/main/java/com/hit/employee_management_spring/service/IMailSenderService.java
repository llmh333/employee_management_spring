package com.hit.employee_management_spring.service;

public interface IMailSenderService {

    public boolean sendMail(String to, String subject, String content);
}
