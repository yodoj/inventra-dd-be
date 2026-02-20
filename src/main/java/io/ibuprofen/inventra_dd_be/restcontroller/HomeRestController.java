package io.ibuprofen.inventra_dd_be.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/home")
public class HomeRestController {

    @GetMapping
    public ResponseEntity<?> getHomeDetails() {
        Map<String, Object> response = new HashMap<>();

        // Stats
        Map<String, String> stats = new HashMap<>();
        stats.put("totalAsetSekolah", "1,254");
        stats.put("unitTerintegrasi", "4");
        stats.put("peminjamanAktif", "156");
        response.put("stats", stats);

        // FAQ
        List<Map<String, Object>> faqs = new ArrayList<>();

        Map<String, Object> faq1 = new HashMap<>();
        faq1.put("category", "Manajemen Aset");
        faq1.put("question", "Apa saja kategori aset yang tersedia?");
        faq1.put("answer",
                "INVENTRA DD memiliki 4 kategori aset: \n- Barang Habis Pakai\n- Barang Tidak Habis Pakai\n- Ruang Kelas\n- Ruangan Non Kelas");
        faqs.add(faq1);

        Map<String, Object> faq2 = new HashMap<>();
        faq2.put("category", "Manajemen Aset");
        faq2.put("question", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        faq2.put("answer",
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam.");
        faqs.add(faq2);

        Map<String, Object> faq3 = new HashMap<>();
        faq3.put("category", "Peminjaman Aset");
        faq3.put("question", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        faq3.put("answer",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.");
        faqs.add(faq3);

        Map<String, Object> faq4 = new HashMap<>();
        faq4.put("category", "Pengadaan Barang");
        faq4.put("question", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        faq4.put("answer",
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        faqs.add(faq4);

        response.put("faqs", faqs);

        return ResponseEntity.ok(response);
    }
}
