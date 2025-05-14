package com.chsbk.pioner_pixel.controller;

import com.chsbk.pioner_pixel.entities.User;
import com.chsbk.pioner_pixel.repository.UserRepository;
import com.chsbk.pioner_pixel.security.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private static final String AUTH_COOKIE = "AuthToken";

    private final UserRepository          userRepo;
    private final JwtUtil                 jwtUtil;
    private final BCryptPasswordEncoder   passwordEncoder;

    record LoginRq(@NotBlank String login,
                   @NotBlank String password) { }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRq rq) {

        log.info("Auth request | login='{}'", rq.login());

        User user = findByLogin(rq.login());
        checkPassword(user, rq.password());

        String token = jwtUtil.generateToken(user.getId());

        ResponseCookie cookie = ResponseCookie
                .from(AUTH_COOKIE, token)
                .httpOnly(true)
                .path("/")
                .build();

        log.info("Auth success | userId={} login='{}'", user.getId(), rq.login());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = AUTH_COOKIE, required = false) String token) {

        safeUserId(token).ifPresent(
                id -> log.info("Logout       | userId={}", id));

        ResponseCookie expired = ResponseCookie
                .from(AUTH_COOKIE, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, expired.toString())
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }

    private User findByLogin(String login) {

        Optional<User> byEmail = userRepo.findByEmails_Email(login);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }

        Optional<User> byPhone = userRepo.findByPhones_Phone(login);
        if (byPhone.isPresent()) {
            return byPhone.get();
        }

        log.warn("Auth failed  | login='{}' reason=user_not_found", login);
        throw new IllegalArgumentException("user not found");
    }

    private void checkPassword(User user, String rawPassword) {

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if (!matches) {
            log.warn("Auth failed  | login='{}' userId={} reason=bad_credentials",
                    user.getEmails(),
                    user.getId());
            throw new IllegalArgumentException("bad credentials");
        }
    }

    private Optional<Long> safeUserId(String token) {

        if (token == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(jwtUtil.getUserId(token));
        }
        catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
