package com.prasadfencing.backendecom.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String otp){

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Your Verification Code for Prasad Fencing Enterprises");
        message.setText("Hi there,\n" +
                "\n" +
                "Thank you for choosing Prasad Fencing Enterprises. To complete your login or request, please use the following One-Time Password (OTP):\n" +
                "\n" +
                "["+otp+"]\n" +
                "This code is valid for the next 10 minutes. For your security, please do not share this code with anyone.\n" +
                "\n" +
                "If you did not request this code, please ignore this email or contact our support team.\n" +
                "\n" +
                "Best regards,\n" +
                "The Prasad Fencing Team");

        mailSender.send(message);
    }
}