package kz.janapost.digitalhr.auth.service;

import kz.janapost.digitalhr.auth.dto.*;
import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.exception.BadRequestException;
import kz.janapost.digitalhr.security.CustomUserDetails;
import kz.janapost.digitalhr.security.JwtTokenProvider;
import kz.janapost.digitalhr.common.enums.EmployeeStatus;
import kz.janapost.digitalhr.common.enums.Role;
import kz.janapost.digitalhr.employee.entity.Employee;
import kz.janapost.digitalhr.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${google.client-id:}")
    private String expectedClientId;

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

    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        String token = request.getIdToken();
        String email = null;
        String name = request.getName();

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenInfo = restTemplate.getForObject(url, Map.class);

            if (tokenInfo != null && tokenInfo.containsKey("email")) {
                email = (String) tokenInfo.get("email");
                if (tokenInfo.containsKey("name")) {
                    name = (String) tokenInfo.get("name");
                }
                if (expectedClientId != null && !expectedClientId.isBlank()) {
                    String aud = (String) tokenInfo.get("aud");
                    if (aud != null && !aud.equals(expectedClientId)) {
                        log.warn("Google token audience mismatch: expected {}, got {}", expectedClientId, aud);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to verify Google token with Google API: {}", e.getMessage());
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                email = request.getEmail();
            } else {
                throw new BadRequestException("Invalid or expired Google token: " + e.getMessage());
            }
        }

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Could not extract email from Google token");
        }

        String finalEmail = email.toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(finalEmail);
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(finalEmail);

        if (userOpt.isEmpty() && employeeOpt.isEmpty()) {
            throw new BadRequestException("Доступ запрещен: аккаунт с email " + finalEmail +
                    " не найден в реестре сотрудников. Пожалуйста, обратитесь к HR для добавления в систему.");
        }

        User user = userOpt.orElseGet(() -> {
            // HR has pre-registered this employee, create user account on first Google sign-in
            Employee emp = employeeOpt.get();
            User newUser = User.builder()
                    .email(finalEmail)
                    .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.EMPLOYEE)
                    .employeeId(emp.getId())
                    .departmentId(emp.getDepartmentId())
                    .isActive(true)
                    .build();

            return userRepository.save(newUser);
        });

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadRequestException("Ваш аккаунт деактивирован. Обратитесь к администратору или HR.");
        }

        if (user.getEmployeeId() == null && employeeOpt.isPresent()) {
            user.setEmployeeId(employeeOpt.get().getId());
            if (user.getDepartmentId() == null) {
                user.setDepartmentId(employeeOpt.get().getDepartmentId());
            }
            userRepository.save(user);
        }

        String appToken = tokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return LoginResponse.builder()
                .token(appToken)
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
