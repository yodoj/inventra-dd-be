package io.ibuprofen.inventra_dd_be.Profile.restcontroller;

import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/home")
public class HomeRestController {

    @Autowired
    private io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository asetBarangRepository;

    @Autowired
    private io.ibuprofen.inventra_dd_be.Aset.repository.AsetRuanganRepository asetRuanganRepository;

    @Autowired
    private io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository peminjamanAsetRepository;

    @GetMapping
    public ResponseEntity<?> getHomeDetails() {
        Map<String, Object> responseData = new HashMap<>();

        long totalAsetBarang = asetBarangRepository.count();
        long totalAsetRuangan = asetRuanganRepository.count();
        long totalAset = totalAsetBarang + totalAsetRuangan;

        long peminjamanAktif = peminjamanAsetRepository.countByStatusPeminjaman(
                io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman.DISETUJUI
        );

        Map<String, String> stats = new HashMap<>();
        // format totalAset with commas
        stats.put("totalAsetSekolah", String.format("%,d", totalAset).replace(',', '.'));
        stats.put("unitTerintegrasi", "4");
        stats.put("peminjamanAktif", String.format("%,d", peminjamanAktif).replace(',', '.'));
        responseData.put("stats", stats);

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
        faq2.put("question", "Apakah saya bisa mencari aset tertentu?");
        faq2.put("answer", "Ya, tersedia fitur pencarian berdasarkan nama aset, kode aset, dan merk. Anda juga dapat memfilter berdasarkan kategori dan status aset.");
        faqs.add(faq2);

        Map<String, Object> faq3 = new HashMap<>();
        faq3.put("category", "Peminjaman Aset");
        faq3.put("question", "Bagaimana cara mengajukan peminjaman aset?");
        faq3.put("answer", "Buka menu Peminjaman Aset, klik \"Buat Pengajuan\", lalu isi form dengan: aset yang ingin dipinjam, waktu peminjaman, waktu pengembalian, tujuan, dan kuantitas. Setelah disimpan, pengajuan akan berstatus DIAJUKAN dan menunggu persetujuan dari Sarana dan Prasarana.");
        faqs.add(faq3);

        Map<String, Object> faq4 = new HashMap<>();
        faq4.put("category", "Peminjaman Aset");
        faq4.put("question", "Bisakah saya membatalkan atau mengedit pengajuan peminjaman?");
        faq4.put("answer", "Ya, pengajuan yang masih berstatus DIAJUKAN dapat diedit atau dihapus. Setelah disetujui atau dalam proses, pengajuan tidak dapat lagi diubah atau dibatalkan.");
        faqs.add(faq4);

        Map<String, Object> faq5 = new HashMap<>();
        faq5.put("category", "Pengadaan Barang");
        faq5.put("question", "Apa saja status pengajuan pengadaan yang ada?");
        faq5.put("answer", "Status pengajuan pengadaan terdiri dari:\n- Diajukan: menunggu review\n- Disetujui oleh Kepsek: sudah disetujui Kepala Sekolah\n- Disetujui oleh Yayasan: sudah disetujui Yayasan\n- Ditolak: pengajuan ditolak\n- Dibeli: aset sudah dibeli dan ditambahkan ke inventaris");
        faqs.add(faq5);

        Map<String, Object> faq6 = new HashMap<>();
        faq6.put("category", "Pengadaan Barang");
        faq6.put("question", "Bisakah saya mengajukan penggantian barang yang rusak?");
        faq6.put("answer", "Ya, fitur penggantian barang rusak tersedia untuk Guru dan Siswa. Buka menu Pengadaan Aset pada navbar, lalu pilih menu Penggantian Barang Rusak, lalu isi form pengajuan. Sarpras akan mereview dan memproses pengajuan tersebut.");
        faqs.add(faq6);

        responseData.put("faqs", faqs);

        return ResponseEntity.ok(BaseResponseDTO.ok(responseData, "Home details retrieved successfully"));
    }
}
