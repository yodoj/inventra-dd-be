package io.ibuprofen.inventra_dd_be.Aset.restcontroller;

import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetBarangResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetRuanganResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.service.AsetService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AsetRestController {

    @Autowired
    private AsetService asetService;

    @GetMapping("/barang")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SARPRAS', 'YAYASAN', 'GURU', 'SISWA', 'KEPSEK')")
    public ResponseEntity<?> getAsetBarang(
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
        }

        if (unit != null && !unit.isEmpty() && !isValidUnit(unit)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400,
                            "Error: Parameter unit tidak valid. Harus salah satu dari (KB-TK, SD, SMP, SMA)"));
        }

        if (kategori != null && !kategori.isEmpty() && !isValidKategoriBarang(kategori)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter kategori barang tidak valid"));
        }

        if (status != null && !status.isEmpty() && !isValidStatus(status)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter status aset tidak valid"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<AsetBarangResponseDTO> result = asetService.getAsetBarang(unit, kategori, status, search, pageable);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data aset barang retrieved successfully"));
    }

    @GetMapping("/ruangan")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SARPRAS', 'YAYASAN', 'GURU', 'SISWA', 'KEPSEK')")
    public ResponseEntity<?> getAsetRuangan(
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
        }

        if (unit != null && !unit.isEmpty() && !isValidUnit(unit)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400,
                            "Error: Parameter unit tidak valid. Harus salah satu dari (KB-TK, SD, SMP, SMA)"));
        }

        if (kategori != null && !kategori.isEmpty() && !isValidKategoriRuangan(kategori)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter kategori ruangan tidak valid"));
        }

        if (status != null && !status.isEmpty() && !isValidStatus(status)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter status aset tidak valid"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<AsetRuanganResponseDTO> result = asetService.getAsetRuangan(unit, kategori, status, search, pageable);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data aset ruangan retrieved successfully"));
    }

    @PostMapping(value = "/barang", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> createAsetBarang(@Valid @ModelAttribute CreateAsetBarangRequestDTO request) {
        AsetBarangResponseDTO result = asetService.createAsetBarang(request);
        return ResponseEntity.status(201).body(BaseResponseDTO.created(result, "Aset barang created successfully"));
    }

    @PostMapping(value = "/ruangan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> createAsetRuangan(@Valid @ModelAttribute CreateAsetRuanganRequestDTO request) {
        AsetRuanganResponseDTO result = asetService.createAsetRuangan(request);
        return ResponseEntity.status(201)
                .body(BaseResponseDTO.created(result, "Aset ruangan created successfully"));
    }

    @GetMapping("/barang/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SARPRAS', 'YAYASAN', 'GURU', 'SISWA', 'KEPSEK')")
    public ResponseEntity<?> getAsetBarangById(@PathVariable UUID id) {
        AsetBarangResponseDTO result = asetService.getAsetBarangById(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Aset barang retrieved successfully"));
    }

    @GetMapping("/ruangan/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SARPRAS', 'YAYASAN', 'GURU', 'SISWA', 'KEPSEK')")
    public ResponseEntity<?> getAsetRuanganById(@PathVariable UUID id) {
        AsetRuanganResponseDTO result = asetService.getAsetRuanganById(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Aset ruangan retrieved successfully"));
    }

    @PutMapping(value = "/barang/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> updateAsetBarang(@PathVariable UUID id,
            @Valid @ModelAttribute UpdateAsetBarangRequestDTO request) {
        AsetBarangResponseDTO result = asetService.updateAsetBarang(id, request);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Aset barang updated successfully"));
    }

    @PutMapping(value = "/ruangan/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> updateAsetRuangan(@PathVariable UUID id,
            @Valid @ModelAttribute UpdateAsetRuanganRequestDTO request) {
        AsetRuanganResponseDTO result = asetService.updateAsetRuangan(id, request);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Aset ruangan updated successfully"));
    }

    @DeleteMapping("/barang/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> deleteAsetBarang(@PathVariable UUID id) {
        asetService.deleteAsetBarang(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Aset barang deleted successfully"));
    }

    @DeleteMapping("/ruangan/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> deleteAsetRuangan(@PathVariable UUID id) {
        asetService.deleteAsetRuangan(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Aset ruangan deleted successfully"));
    }

    @GetMapping("/borrowable")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SARPRAS', 'YAYASAN', 'GURU', 'SISWA', 'KEPSEK')")
    public ResponseEntity<?> getBorrowableAssets(@RequestParam String unit) {
        if (unit != null && !unit.isEmpty() && !isValidUnit(unit)) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter unit tidak valid"));
        }
        return ResponseEntity.ok(BaseResponseDTO.ok(asetService.getBorrowableAssets(unit),
                "Borrowable assets retrieved successfully"));
    }

    private boolean isValidUnit(String unit) {
        if (unit == null) return false;
        List<String> validUnits = Arrays.asList(
            "KB-TK", "SD", "SMP", "SMA", "GLOBAL", "PUSAT", "ADMIN", "YAYASAN"
        );
        return validUnits.contains(unit.toUpperCase());
    }

    private boolean isValidStatus(String status) {
        try {
            StatusAset.valueOf(status);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    private boolean isValidKategoriBarang(String kategori) {
        return kategori.equals("BARANG_HABIS_PAKAI") || kategori.equals("BARANG_TIDAK_HABIS_PAKAI");
    }

    private boolean isValidKategoriRuangan(String kategori) {
        return kategori.equals("RUANG_KELAS") || kategori.equals("RUANG_NON_KELAS");
    }
}
