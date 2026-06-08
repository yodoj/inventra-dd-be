// package io.ibuprofen.inventra_dd_be.Profile.config;

// import io.ibuprofen.inventra_dd_be.Profile.model.Role;
// import io.ibuprofen.inventra_dd_be.Profile.model.User;
// import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
// import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
// import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
// import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;
// import java.util.UUID;

// @Component
// public class DataInitializer implements CommandLineRunner {

//     @Autowired
//     UserRepository userRepository;

//     @Autowired
//     PasswordEncoder passwordEncoder;

//     @Autowired
//     PengadaanAsetRepository pengadaanAsetRepository;

//     @Override
//     public void run(String... args) throws Exception {
//         seedUsers();
//         seedPengadaanAset();
//     }

//     private void seedUsers() {
//         if (userRepository.count() == 0) {
//             // 1. Superadmin
//             createUser("Superadmin", "superadmin@diandidaktika.sch.id", "admin123", Role.ADMIN, "superadmin", null, null);

//             // 2. Yayasan
//             createUser("Ketua Yayasan", "yayasan@diandidaktika.sch.id", "yayasan123", Role.YAYASAN, "yayasan", null, null);

//             // 3. Per Unit
//             List<String> units = Arrays.asList("KB-TK", "SD", "SMP", "SMA");
//             for (String unit : units) {
//                 String unitLower = unit.toLowerCase();
//                 createUser("Kepsek " + unit, "kepsek." + unitLower + "@diandidaktika.sch.id", "password123", Role.KEPSEK, unit, null, null);
//                 createUser("Sarpras " + unit, "sarpras." + unitLower + "@diandidaktika.sch.id", "password123", Role.SARPRAS, unit, null, null);
//                 createUser("Guru " + unit, "guru." + unitLower + "@diandidaktika.sch.id", "password123", Role.GURU, unit, null, null);
//                 createUser("Siswa " + unit, "siswa." + unitLower + "@diandidaktika.sch.id", "password123", Role.SISWA, unit, generateNISN(), unit);
//             }
//             System.out.println(">>> Dummy users seeded successfully!");
//         }
//     }

//     private void seedPengadaanAset() {
//         if (pengadaanAsetRepository.count() == 0) {
//             List<PengadaanAset> dummyAsets = Arrays.asList(
//                 PengadaanAset.builder()
//                     .namaAset("Kertas Folio")
//                     .kategoriAset(KategoriAset.BARANG_HABIS_PAKAI)
//                     .merk("Sinar Dunia")
//                     .qty(50)
//                     .estimasiHarga(55000L)
//                     .waktuPengadaan(LocalDate.of(2026, 4, 20))
//                     .linkGambar("https://www.static-src.com/wcsstore/Indraprastha/images/catalog/full//catalog-image/96/MTA-144003649/brd-44261_kertas-double-folio-bergaris-sidu-1-lembar_full01-31f4f9a6.jpg")
//                     .unit("SMA").build(),

//                 PengadaanAset.builder()
//                     .namaAset("Spidol Whiteboard")
//                     .kategoriAset(KategoriAset.BARANG_HABIS_PAKAI)
//                     .merk("Snowman")
//                     .qty(12)
//                     .estimasiHarga(120000L)
//                     .waktuPengadaan(LocalDate.of(2026, 4, 20))
//                     .linkGambar("https://example.com/spidol.jpg")
//                     .unit("SMP").build(),

//                 PengadaanAset.builder()
//                     .namaAset("Laptop Core i7")
//                     .kategoriAset(KategoriAset.BARANG_HABIS_PAKAI)
//                     .merk("ASUS")
//                     .qty(2)
//                     .estimasiHarga(15000000L)
//                     .waktuPengadaan(LocalDate.of(2026, 4, 20))
//                     .linkGambar("https://example.com/laptop.jpg")
//                     .unit("SMA").build(),

//                 PengadaanAset.builder()
//                     .namaAset("Kursi Lipat")
//                     .kategoriAset(KategoriAset.BARANG_HABIS_PAKAI)
//                     .merk("Chitose")
//                     .qty(20)
//                     .estimasiHarga(250000L)
//                     .waktuPengadaan(LocalDate.of(2026, 4, 20))
//                     .linkGambar("https://example.com/kursi.jpg")
//                     .unit("SD").build(),

//                 PengadaanAset.builder()
//                     .namaAset("Proyektor Epson")
//                     .kategoriAset(KategoriAset.BARANG_HABIS_PAKAI)
//                     .merk("Epson EB-X400")
//                     .qty(1)
//                     .estimasiHarga(7000000L)
//                     .waktuPengadaan(LocalDate.of(2026, 4, 20))
//                     .linkGambar("https://example.com/proyektor.jpg")
//                     .unit("KB-TK").build()
//             );

//             pengadaanAsetRepository.saveAll(dummyAsets);
//             System.out.println(">>> 5 Dummy pengadaan aset seeded successfully!");
//         }
//     }

//     private void createUser(String name, String email, String password, Role role, String unit, String nisn, String kelas) {
//         User user = User.builder()
//                 .name(name)
//                 .email(email)
//                 .password(passwordEncoder.encode(password))
//                 .role(role)
//                 .unit(unit)
//                 .phoneNumber(generatePhoneNumber())
//                 .nisn(nisn)
//                 .kelas(kelas)
//                 .build();
//         userRepository.save(user);
//     }

//     private String generatePhoneNumber() {
//         long random = System.nanoTime() % 10000000000L;
//         return "08" + String.format("%010d", Math.abs(random));
//     }

//     private String generateNISN() {
//         long random = System.nanoTime() % 100000000000L;
//         return String.format("%11d", Math.abs(random));
//     }
// }