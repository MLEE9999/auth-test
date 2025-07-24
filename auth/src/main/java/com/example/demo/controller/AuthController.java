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
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest req) {
        try {
            userService.registerUser(req);
            return ResponseEntity.ok("회원가입 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest req) {
        User user = userRepository.findByEmail(req.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("등록되지 않은 사용자 입니다");
        }
        if (!user.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("이메일 인증이 완료되지 않았습니다.");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("비밀번호 불일치");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseEntity.ok(new UserResponse(token, refreshToken));
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        return userService.verifyEmail(token);
    }

    // 비밀번호 재설정 요청 API
    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        try {
            String result = userService.requestPasswordReset(request);
            System.out.println("Password reset result: " + result);  // 로그 추가
            return ResponseEntity.ok(result);  // 200 OK
        } catch (RuntimeException e) {
            // 예외 메시지를 클라이언트에 전달, 상태 코드는 400 Bad Request 등으로 설정
            System.out.println("Password reset error: " + e.getMessage());  // 로그 추가
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 비밀번호 재설정 확정 API
    @PostMapping("/password-reset/confirm")
    public String confirmPasswordReset(@RequestBody @Valid PasswordResetConfirmRequest request) {
        return userService.confirmPasswordReset(request);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeRequest request,
                                            Authentication authentication) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

}
