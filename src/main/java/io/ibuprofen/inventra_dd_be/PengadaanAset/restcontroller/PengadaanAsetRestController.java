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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pengadaan")
public class PengadaanAsetRestController {

    @Autowired
    private PengadaanAsetService pengadaanAsetService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> createPengadaan(@Valid @RequestBody CreatePengadaanAsetRequestDTO request) {
        PengadaanAsetDetailResponse result = pengadaanAsetService.createPengadaan(request);
        return ResponseEntity.status(201)
                .body(BaseResponseDTO.created(result, "Pengajuan pengadaan berhasil diajukan"));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getAllPengadaan(@RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) String direction) {
        List<PengadaanAsetResponse> result = pengadaanAsetService.getAllPengadaan(search, sortBy, direction);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data pengajuan pengadaan berhasil diambil"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getPengadaanById(@PathVariable UUID id) {
        PengadaanAsetDetailResponse result = pengadaanAsetService.getPengadaanById(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Detail pengajuan berhasil diambil"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> deletePengadaan(@PathVariable UUID id) {
        pengadaanAsetService.deletePengadaan(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Pengajuan pengadaan berhasil dihapus"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> updatePengadaan(
            @PathVariable UUID id, 
            @Valid @RequestBody UpdatePengadaanAsetRequestDTO request) {
        
        PengadaanAsetDetailResponse result = pengadaanAsetService.updatePengadaan(id, request);
        
        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Pengajuan pengadaan berhasil diupdate")
        ); 
    }
}