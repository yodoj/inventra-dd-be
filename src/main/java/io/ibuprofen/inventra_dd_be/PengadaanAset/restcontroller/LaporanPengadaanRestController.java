package io.ibuprofen.inventra_dd_be.PengadaanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.service.LaporanPengadaanService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laporan/pengadaan")
public class LaporanPengadaanRestController {

    @Autowired
    private LaporanPengadaanService laporanService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS','SUPERADMIN')")
    public ResponseEntity<?> getLaporanPengadaan() {

        List<LaporanPengadaanResponseDTO> result =
                laporanService.getLaporanPengadaan();

        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Data laporan pengadaan berhasil diambil")
        );
    }
}