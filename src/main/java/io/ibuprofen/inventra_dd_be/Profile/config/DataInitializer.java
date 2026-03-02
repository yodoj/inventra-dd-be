package io.ibuprofen.inventra_dd_be.Profile.config;

import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            // 1. Superadmin
            createUser("Superadmin", "superadmin@diandidaktika.sch.id", "admin123", Role.ADMIN, "superadmin", null, null);

            // 2. Yayasan
            createUser("Ketua Yayasan", "yayasan@diandidaktika.sch.id", "yayasan123", Role.YAYASAN, "yayasan", null, null);

            // 3. Per Unit
            List<String> units = Arrays.asList("KB-TK", "SD", "SMP", "SMA");
            for (String unit : units) {
                String unitLower = unit.toLowerCase();
                createUser("Kepsek " + unit, "kepsek." + unitLower + "@diandidaktika.sch.id", "password123", Role.KEPSEK, unit, null, null);
                createUser("Sarpras " + unit, "sarpras." + unitLower + "@diandidaktika.sch.id", "password123", Role.SARPRAS, unit, null, null);
                createUser("Guru " + unit, "guru." + unitLower + "@diandidaktika.sch.id", "password123", Role.GURU, unit, null, null);
                createUser("Siswa " + unit, "siswa." + unitLower + "@diandidaktika.sch.id", "password123", Role.SISWA, unit, generateNISN(), unit);
            }
            System.out.println(">>> Dummy users seeded successfully!");
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
    }

    private String generatePhoneNumber() {
        long random = System.nanoTime() % 10000000000L;
        return "08" + String.format("%010d", Math.abs(random));
    }

    private String generateNISN() {
        long random = System.nanoTime() % 100000000000L;
        return String.format("%11d", Math.abs(random));
    }
}