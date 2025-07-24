package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "이메일 인증 요청";
        String verificationUrl = "https://8080-mlee9999-authtest-q05q86trbs2.ws-us120.gitpod.io/auth/verify-email?token=" + token;
        String message = "아래 링크를 클릭해서 이메일 인증을 완료하세요:\n" + verificationUrl;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        try {
            mailSender.send(mailMessage);
            System.out.println("✅ Email sent to: " + toEmail);
            System.out.println("🔗 인증 URL: " + verificationUrl);
            System.out.println("🔑 토큰: " + token);
        } catch (Exception e) {
            System.err.println("❌ Email send failed: " + e.getMessage());
            System.out.println("🔗 인증 URL: " + verificationUrl);
            throw new RuntimeException("이메일 전송 실패");
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String subject = "비밀번호 재설정 요청";
        String resetUrl = "https://8080-mlee9999-authtest-q05q86trbs2.ws-us120.gitpod.io/auth/reset-password?token=" + token; // 실제 도메인으로 바꿔주세요
        String message = "아래 링크를 클릭해 비밀번호를 재설정하세요:\n" + resetUrl;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        try {
            mailSender.send(mailMessage);
            System.out.println("✅ Password reset email sent to: " + toEmail);
            System.out.println("🔗 인증 URL 2 : " + resetUrl);
        } catch (Exception e) {
            System.err.println("❌ Password reset email send failed: " + e.getMessage());
            System.out.println("🔗 인증 URL 2 : " + resetUrl);
            throw new RuntimeException("이메일 전송 실패", e);
        }

        System.out.println("비밀번호 재설정 이메일 발송 완료: " + toEmail);
    }

}
