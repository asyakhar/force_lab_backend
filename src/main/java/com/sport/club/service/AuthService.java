
package com.sport.club.service;

import com.sport.club.model.dto.request.LoginRequest;
import com.sport.club.model.dto.request.RefreshTokenRequest;
import com.sport.club.model.dto.request.RegisterRequest;
import com.sport.club.model.dto.response.AuthResponse;
import com.sport.club.model.entity.User;
import com.sport.club.repository.UserRepository;
import com.sport.club.security.JwtService;
import com.sport.club.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final AthleteService athleteService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        
        if (request.getRole() != null && request.getRole().equals("COACH")) {
            user.setRole(User.Role.COACH);
        } else {
            user.setRole(User.Role.ATHLETE);
        }

        User savedUser = userRepository.save(user);

        
        if (user.getRole() == User.Role.ATHLETE) {
            athleteService.createAthleteProfile(savedUser.getId(), request);
        }

        UserDetails userDetails = user;
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserDetails userDetails = user;
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        System.out.println("Запрос на обновление токена: " + request.getRefreshToken());

        String userEmail = refreshTokenService.getEmailByRefreshToken(request.getRefreshToken());
        System.out.println("Найден email: " + userEmail);

        if (userEmail == null) {
            System.out.println("Refresh token не найден или истек");
            throw new RuntimeException("Refresh token недействителен или истек. Войдите заново.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

        refreshTokenService.deleteRefreshToken(request.getRefreshToken());

        UserDetails userDetails = user;
        String accessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }
}