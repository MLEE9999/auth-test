package com.example.demo.controller;

import com.example.demo.dto.PasswordResetConfirmRequest;
import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.PasswordChangeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody @Valid UserRequest req) {
        return userService.registerUser(req);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody @Valid UserRequest req) {
        // User user = userRepository.findByEmail(req.getEmail())
        //         .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));
        User user = userRepository.findByEmail(req.getEmail())
            .orElse(null);
        System.out.println("로그 확인용");
        System.out.println(user);
        System.out.println(user.getEmail());
        
        if(user == null){
            System.out.println("사용자 문제");
            throw new RuntimeException("등록되지 않은 사용자 입니다");
        }
        if(!user.isEmailVerified()){
            System.out.println("이메일 문제");
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            System.out.println("비밀번호 문제");
            throw new RuntimeException("비밀번호 불일치");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new UserResponse(token, refreshToken);
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        return userService.verifyEmail(token);
    }

    // 비밀번호 재설정 요청 API
    @PostMapping("/password-reset/request")
    public String requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        return userService.requestPasswordReset(request);
    }

    // 비밀번호 재설정 확정 API
    @PostMapping("/password-reset/confirm")
    public String confirmPasswordReset(@RequestBody @Valid PasswordResetConfirmRequest request) {
        return userService.confirmPasswordReset(request);
    }

    @PutMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeRequest request,
                                            Authentication authentication) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

}
