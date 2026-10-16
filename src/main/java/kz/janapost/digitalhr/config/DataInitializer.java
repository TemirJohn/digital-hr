package kz.janapost.digitalhr.config;

import kz.janapost.digitalhr.auth.entity.User;
import kz.janapost.digitalhr.auth.repository.UserRepository;
import kz.janapost.digitalhr.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.Optional;
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
        createUserOrUpdate("admin@janapost.kz", "admin123", Role.SUPER_ADMIN, 1L, 1L);
        createUserOrUpdate("hr@janapost.kz", "hr123456", Role.HR, 5L, 11L);
        createUserOrUpdate("director@janapost.kz", "dir12345", Role.DIRECTOR, 1L, 1L);
        createUserOrUpdate("astana@janapost.kz", "city1234", Role.CITY_MANAGER, 8L, 16L);
        createUserOrUpdate("almaty@janapost.kz", "city1234", Role.CITY_MANAGER, 7L, 15L);
        createUserOrUpdate("franchise@janapost.kz", "fran1234", Role.FRANCHISE_OWNER, 12L, 20L);
        createUserOrUpdate("manager@janapost.kz", "mngr1234", Role.FRANCHISE_MANAGER, 12L, 21L);
        createUserOrUpdate("employee@janapost.kz", "empl1234", Role.EMPLOYEE, 12L, 32L);
        log.info("✅ Demo users initialized with employee links");
    }

    private void createUserOrUpdate(String email, String password, Role role, Long departmentId, Long employeeId) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isEmpty()) {
            userRepository.save(User.builder()
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .role(role)
                    .departmentId(departmentId)
                    .employeeId(employeeId)
                    .isActive(true)
                    .build());
            log.info("  Created user: {} / {} ({}) -> employee #{}", email, password, role, employeeId);
        } else {
            User user = existing.get();
            if (user.getEmployeeId() == null) {
                user.setEmployeeId(employeeId);
                userRepository.save(user);
                log.info("  Updated user {} with employee #{}", email, employeeId);
            }
        }
    }
}
