package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restcontroller;

import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.FrekuensiPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.LaporanUtilisasiResponseDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.RiwayatPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.service.LaporanUtilisasiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/laporan/utilisasi")
@PreAuthorize("hasAnyAuthority('YAYASAN', 'KEPSEK', 'SARPRAS', 'ADMIN')")
public class LaporanUtilisasiRestController {

    @Autowired
    private LaporanUtilisasiService laporanUtilisasiService;

    @GetMapping("/history")
    public ResponseEntity<LaporanUtilisasiResponseDTO<RiwayatPeminjamanDTO>> getHistoryReports(
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String period_type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String kategori,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        LaporanUtilisasiResponseDTO<RiwayatPeminjamanDTO> response = laporanUtilisasiService.getHistoryReports(
                unit, period_type, start_date, end_date, search, kategori, page, limit
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/frequency")
    public ResponseEntity<LaporanUtilisasiResponseDTO<FrekuensiPeminjamanDTO>> getFrequencyReports(
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String period_type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String kategori,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        LaporanUtilisasiResponseDTO<FrekuensiPeminjamanDTO> response = laporanUtilisasiService.getFrequencyReports(
                unit, period_type, start_date, end_date, search, kategori, page, limit
        );

        return ResponseEntity.ok(response);
    }
}
