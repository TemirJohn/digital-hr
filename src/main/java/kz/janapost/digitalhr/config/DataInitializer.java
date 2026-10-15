package kz.janapost.digitalhr.config;

import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserIfNotExists("admin@janapost.kz", "admin123", Role.SUPER_ADMIN, 1L);
        createUserIfNotExists("hr@janapost.kz", "hr123456", Role.HR, 5L);
        createUserIfNotExists("director@janapost.kz", "dir12345", Role.DIRECTOR, 1L);
        createUserIfNotExists("astana@janapost.kz", "city1234", Role.CITY_MANAGER, 8L);
        createUserIfNotExists("almaty@janapost.kz", "city1234", Role.CITY_MANAGER, 7L);
        createUserIfNotExists("franchise@janapost.kz", "fran1234", Role.FRANCHISE_OWNER, 12L);
        createUserIfNotExists("manager@janapost.kz", "mngr1234", Role.FRANCHISE_MANAGER, 12L);
        createUserIfNotExists("employee@janapost.kz", "empl1234", Role.EMPLOYEE, 12L);
        log.info("✅ Demo users initialized (passwords in DataInitializer.java)");
    }

    private void createUserIfNotExists(String email, String password, Role role, Long departmentId) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .role(role)
                    .departmentId(departmentId)
                    .isActive(true)
                    .build());
            log.info("  Created user: {} / {} ({})", email, password, role);
        }
    }
}
