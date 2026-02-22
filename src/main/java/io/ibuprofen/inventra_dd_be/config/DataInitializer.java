package io.ibuprofen.inventra_dd_be.config;

import io.ibuprofen.inventra_dd_be.model.Role;
import io.ibuprofen.inventra_dd_be.model.User;
import io.ibuprofen.inventra_dd_be.repository.UserRepository;
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
            createUser("Superadmin", "superadmin@diandidaktika.sch.id", "admin123", Role.ADMIN, "Global", null, null);

            // 2. Yayasan
            createUser("Ketua Yayasan", "yayasan@diandidaktika.sch.id", "yayasan123", Role.YAYASAN, "Pusat", null, null);

            // 3. Per Unit (TK, SD, SMP, SMA)
            List<String> units = Arrays.asList("TK", "SD", "SMP", "SMA");

            for (String unit : units) {
                String unitLower = unit.toLowerCase();

                // Kepsek
                createUser("Kepsek " + unit, "kepsek." + unitLower + "@diandidaktika.sch.id", "password123",
                        Role.KEPSEK, unit, null, null);

                // Sarpras
                createUser("Sarpras " + unit, "sarpras." + unitLower + "@diandidaktika.sch.id", "password123",
                        Role.SARPRAS, unit, null, null);

                // Guru
                createUser("Guru " + unit, "guru." + unitLower + "@diandidaktika.sch.id", "password123", Role.GURU,
                        unit, null, null);

                // Siswa (dengan NISN dan Kelas)
                createUser("Siswa " + unit, "siswa." + unitLower + "@diandidaktika.sch.id", "password123", Role.SISWA,
                        unit, generateNISN(), unit);
            }

            System.out.println("Dummy users seeded successfully!");
        }
    }

    private void createUser(String name, String email, String password, Role role, String unit, String nisn, String kelas) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .unit(unit)
                .phoneNumber(generatePhoneNumber())
                .nisn(nisn)
                .kelas(kelas)
                .build();
        userRepository.save(user);
        System.out.println("Created: " + email + " | Role: " + role + " | Unit: " + unit);
    }

    private String generatePhoneNumber() {
        long random = System.nanoTime() % 10000000000L;
        return "08" + String.format("%010d", random);
    }

    private String generateNISN() {
        long random = System.nanoTime() % 100000000000L;
        return String.format("%11d", random);
    }
}