package io.ibuprofen.inventra_dd_be.Profile.config;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {

            // 1. Superadmin
            createUser("Superadmin", "superadmin@diandidaktika.sch.id", "admin123", Role.ADMIN, "Global");

            // 2. Yayasan
            createUser("Ketua Yayasan", "yayasan@diandidaktika.sch.id", "yayasan123", Role.YAYASAN, "Pusat");

            // 3. Per Unit (TK, SD, SMP, SMA)
            List<String> units = Arrays.asList("TK", "SD", "SMP", "SMA");

            for (String unit : units) {
                String unitLower = unit.toLowerCase();

                // Kepsek
                createUser("Kepsek " + unit, "kepsek." + unitLower + "@diandidaktika.sch.id", "password123",
                        Role.KEPSEK, unit);

                // Sarpras
                createUser("Sarpras " + unit, "sarpras." + unitLower + "@diandidaktika.sch.id", "password123",
                        Role.SARPRAS, unit);

                // Guru
                createUser("Guru " + unit, "guru." + unitLower + "@diandidaktika.sch.id", "password123", Role.GURU,
                        unit);

                // Siswa
                createUser("Siswa " + unit, "siswa." + unitLower + "@diandidaktika.sch.id", "password123", Role.SISWA,
                        unit);
            }

            System.out.println("Dummy users seeded successfully!");
        }
    }

    private void createUser(String name, String email, String password, Role role, String unit) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .unit(unit)
                .build();
        userRepository.save(user);
        System.out.println("Created: " + email + " | Role: " + role + " | Unit: " + unit);
    }
}