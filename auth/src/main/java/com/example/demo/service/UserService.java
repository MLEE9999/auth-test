package com.example.demo.service;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.PasswordResetConfirmRequest;

import com.example.demo.entity.EmailVerificationToken;
import com.example.demo.entity.User;
import com.example.demo.repository.EmailVerificationTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public String registerUser(UserRequest req) {
        if(userRepository.findByEmail(req.getEmail()).isPresent()){
            return "이미 가입된 이메일입니다.";
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .emailVerified(false)
                .build();
        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        emailVerificationTokenRepository.save(verificationToken);

        try {
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (Exception e) {
            System.err.println("이메일 발송 실패: " + e.getMessage());
        }
        return "회원가입 완료, 이메일 인증 메일을 확인하세요.";
    }

    @Transactional
    public String verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "토큰이 만료되었습니다.";
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);

        return "이메일 인증 성공";
    }

    @Transactional
    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

        if (user.isEmailVerified()) {
            return "이미 인증된 이메일입니다.";
        }

        // 기존 토큰 삭제 (있다면)
        emailVerificationTokenRepository.deleteByUser(user);

        String newToken = UUID.randomUUID().toString();
        EmailVerificationToken newVerificationToken = EmailVerificationToken.builder()
                .token(newToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        emailVerificationTokenRepository.save(newVerificationToken);

        try {
            emailService.sendVerificationEmail(user.getEmail(), newToken);
        } catch (Exception e) {
            System.err.println("이메일 재발송 실패: " + e.getMessage());
            return "이메일 재발송에 실패했습니다.";
        }

        return "새로운 인증 메일을 발송했습니다.";
    }

    @Transactional
    public String requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

        // 기존에 있던 토큰 있으면 삭제
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1)) // 1시간 유효기간
                .build();
        passwordResetTokenRepository.save(resetToken);

        // 이메일 발송 (이메일 템플릿과 URL은 상황에 맞게 변경)
        String resetUrl = "https://8080-mlee9999-authtest-q05q86trbs2.ws-us120.gitpod.io/auth/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

        return "비밀번호 재설정 메일을 발송했습니다. 이메일을 확인하세요.";
    }

    @Transactional
    public String confirmPasswordReset(PasswordResetConfirmRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "토큰이 만료되었습니다.";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        return "비밀번호가 성공적으로 변경되었습니다.";
    }

    public void changePassword(String email, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }   

}
