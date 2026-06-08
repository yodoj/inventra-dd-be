package io.ibuprofen.inventra_dd_be.PengadaanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.UpdatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetDetailResponse;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import io.ibuprofen.inventra_dd_be.PengadaanAset.service.PengadaanAsetService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pengadaan")
public class PengadaanAsetRestController {

    @Autowired
    private PengadaanAsetService pengadaanAsetService;

    // Endpoint untuk membuat pengajuan pengadaan aset baru
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> createPengadaan(@Valid @ModelAttribute CreatePengadaanAsetRequestDTO request) {
        PengadaanAsetDetailResponse result = pengadaanAsetService.createPengadaan(request);
        
        return ResponseEntity.status(201)
                .body(BaseResponseDTO.created(result, "Pengajuan pengadaan berhasil diajukan"));
    }

    // Endpoint untuk mengambil semua pengajuan pengadaan aset dengan opsi filter, search dan sorting
    @GetMapping
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getAllPengadaan(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String unit) {

        List<PengadaanAsetResponse> result = pengadaanAsetService.getAllPengadaan(
                search, status, kategori, sortBy, direction, unit
        );

        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data pengajuan pengadaan berhasil diambil"));
    }

    // Endpoint untuk mengambil detail pengajuan pengadaan aset berdasarkan ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getPengadaanById(@PathVariable UUID id) {
        PengadaanAsetDetailResponse result = pengadaanAsetService.getPengadaanById(id);
        
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Detail pengajuan berhasil diambil"));
    }

    // Endpoint untuk menghapus pengajuan pengadaan aset berdasarkan ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> deletePengadaan(@PathVariable UUID id) {
        pengadaanAsetService.deletePengadaan(id);
        
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Pengajuan pengadaan berhasil dihapus"));
    }

    // Endpoint untuk memperbarui pengajuan pengadaan aset berdasarkan ID
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> updatePengadaan(
            @PathVariable UUID id, 
            @Valid @ModelAttribute UpdatePengadaanAsetRequestDTO request) {
        
        PengadaanAsetDetailResponse result = pengadaanAsetService.updatePengadaan(id, request);
        
        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Pengajuan pengadaan berhasil diupdate")
        ); 
    }
}