package kz.janapost.digitalhr.auth.service;

import kz.janapost.digitalhr.auth.dto.*;
import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.exception.BadRequestException;
import kz.janapost.digitalhr.security.CustomUserDetails;
import kz.janapost.digitalhr.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String token = tokenProvider.generateToken(userDetails.getEmail(), userDetails.getRole());

        return LoginResponse.builder()
                .token(token)
                .role(userDetails.getRole())
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .departmentId(userDetails.getDepartmentId())
                .build();
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .departmentId(request.getDepartmentId())
                .employeeId(request.getEmployeeId())
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    public LoginResponse refresh(RefreshRequest request) {
        String token = request.getToken();
        if (!tokenProvider.validateToken(token)) {
            throw new BadRequestException("Invalid or expired token");
        }
        String email = tokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found for token"));
        if (!user.getIsActive()) {
            throw new BadRequestException("User is inactive");
        }
        String newToken = tokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return LoginResponse.builder()
                .token(newToken)
                .role(user.getRole().name())
                .userId(user.getId())
                .email(user.getEmail())
                .departmentId(user.getDepartmentId())
                .build();
    }

    public UserInfoResponse getCurrentUser(CustomUserDetails userDetails) {
        return UserInfoResponse.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .departmentId(userDetails.getDepartmentId())
                .employeeId(userDetails.getEmployeeId())
                .build();
    }
}
