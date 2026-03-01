package io.ibuprofen.inventra_dd_be.PengadaanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import io.ibuprofen.inventra_dd_be.PengadaanAset.service.PengadaanAsetService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pengadaan")
public class PengadaanAsetRestController {

    @Autowired
    private PengadaanAsetService pengadaanAsetService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> createPengadaan(@Valid @RequestBody CreatePengadaanAsetRequestDTO request) {
        PengadaanAsetResponse result = pengadaanAsetService.createPengadaan(request);
        return ResponseEntity.status(201)
                .body(BaseResponseDTO.created(result, "Pengajuan pengadaan berhasil diajukan"));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getAllPengadaan() {
        List<PengadaanAsetResponse> result = pengadaanAsetService.getAllPengadaan();
        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Data pengajuan pengadaan berhasil diambil")
        );
    }
}