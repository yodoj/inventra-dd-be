package io.ibuprofen.inventra_dd_be.PengadaanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanPageResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.service.LaporanPengadaanService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/laporan/pengadaan")
public class LaporanPengadaanRestController {

    @Autowired
    private LaporanPengadaanService laporanService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS','ADMIN')")
    public ResponseEntity<?> getLaporanPengadaan(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) Integer bulan,
            @RequestParam(required = false) Integer tahun,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String dateField,
            @RequestParam(defaultValue = "waktuPengajuan") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LaporanPengadaanPageResponseDTO result = laporanService.getLaporanPengadaan(
                search, status, kategori, unit, bulan, tahun, from, to, dateField,
                sortBy, direction, page, size);

        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Data laporan pengadaan berhasil diambil")
        );
    }
}