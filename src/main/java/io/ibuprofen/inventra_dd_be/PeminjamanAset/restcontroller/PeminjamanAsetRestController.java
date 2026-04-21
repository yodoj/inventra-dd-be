package io.ibuprofen.inventra_dd_be.PeminjamanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.request.*;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.response.PeminjamanAsetResponseDTO;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.service.PeminjamanAsetService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/peminjaman")
public class PeminjamanAsetRestController {

    @Autowired
    private PeminjamanAsetService peminjamanAsetService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'ADMIN')")
    public ResponseEntity<?> getMyPeminjaman(
            @RequestParam java.util.Map<String, String> allParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String unitTujuan,
            @RequestParam(required = false) String statusPeminjaman,
            @RequestParam(required = false) String kategoriAset,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean all) {
        
        validateAllowedParams(allParams);

        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        boolean isSarpras = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SARPRAS"));

        boolean shouldShowAll = (all != null) ? all : isAdmin;

        io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman statusEnum = validateStatusPeminjaman(statusPeminjaman);
        String unitParam = validateUnitTujuan(unitTujuan, isAdmin, isSarpras);
        List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriList = validateKategoriAset(kategoriAset);
        
        String searchParam = (search == null || search.isEmpty()) ? null : "%" + search.trim().toLowerCase() + "%";

        Page<PeminjamanAsetResponseDTO> result;
        if (shouldShowAll && isAdmin) {
            result = peminjamanAsetService.getAllPeminjaman(unitParam, statusEnum, kategoriList, searchParam, pageable);
        } else {
            result = peminjamanAsetService.getMyPeminjaman(userDetails.getId(), unitParam, statusEnum, kategoriList, searchParam, pageable);
        }
        
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data pengajuan peminjaman unit sendiri retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getPeminjamanById(@PathVariable UUID id) {
        PeminjamanAsetResponseDTO result = peminjamanAsetService.getPeminjamanById(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data peminjaman retrieved successfully"));
    }

    @GetMapping("/lintas-unit")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getMyPeminjamanLintasUnit(
            @RequestParam java.util.Map<String, String> allParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String unitTujuan,
            @RequestParam(required = false) String statusPeminjaman,
            @RequestParam(required = false) String kategoriAset,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean all) {
        
        validateAllowedParams(allParams);

        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        boolean isSarpras = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SARPRAS"));

        boolean shouldShowAll = (all != null) ? all : isAdmin;

        io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman statusEnum = validateStatusPeminjaman(statusPeminjaman);
        String unitParam = validateUnitTujuan(unitTujuan, isAdmin, isSarpras);
        List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriList = validateKategoriAset(kategoriAset);
        
        String searchParam = (search == null || search.isEmpty()) ? null : "%" + search.trim().toLowerCase() + "%";

        Page<PeminjamanAsetResponseDTO> result;
        if (shouldShowAll && isAdmin) {
            result = peminjamanAsetService.getAllPeminjamanLintasUnit(unitParam, statusEnum, kategoriList, searchParam, pageable);
        } else {
            result = peminjamanAsetService.getMyPeminjamanLintasUnit(userDetails.getId(), unitParam, statusEnum, kategoriList, searchParam, pageable);
        }
        
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data pengajuan peminjaman lintas unit retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'ADMIN')")
    public ResponseEntity<?> createPeminjaman(@Valid @RequestBody CreatePeminjamanRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.createPeminjaman(request, userDetails.getId());
        return ResponseEntity.status(201).body(BaseResponseDTO.created(result, "Pengajuan peminjaman created successfully"));
    }

    @PostMapping("/lintas-unit")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> createPeminjamanLintasUnit(@Valid @RequestBody CreatePeminjamanLintasUnitRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.createPeminjamanLintasUnit(request, userDetails.getId());
        return ResponseEntity.status(201).body(BaseResponseDTO.created(result, "Pengajuan peminjaman lintas unit created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'ADMIN')")
    public ResponseEntity<?> updatePeminjaman(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePeminjamanRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.updatePeminjaman(id, request, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Pengajuan peminjaman updated successfully"));
    }

    @PutMapping("/lintas-unit/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> updatePeminjamanLintasUnit(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePeminjamanLintasUnitRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.updatePeminjamanLintasUnit(id, request, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Pengajuan peminjaman lintas unit updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'ADMIN')")
    public ResponseEntity<?> deletePeminjaman(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peminjamanAsetService.deletePeminjaman(id, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Pengajuan peminjaman berhasil dihapus"));
    }

    @DeleteMapping("/lintas-unit/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> deletePeminjamanLintasUnit(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peminjamanAsetService.deletePeminjamanLintasUnit(id, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Pengajuan peminjaman lintas unit berhasil dihapus"));
    }

    private void validateAllowedParams(java.util.Map<String, String> allParams) {
        java.util.Set<String> allowedParams = java.util.Set.of("page", "size", "unitTujuan", "statusPeminjaman", "kategoriAset", "search", "all");
        for (String param : allParams.keySet()) {
            if (!allowedParams.contains(param)) {
                throw new IllegalArgumentException("Parameter tidak valid.");
            }
        }
    }

    private String validateUnitTujuan(String unitTujuan, boolean isAdmin, boolean isSarpras) {
        if (unitTujuan == null || unitTujuan.trim().isEmpty() || unitTujuan.equalsIgnoreCase("Semua Unit")) {
            return null;
        }
        String unit = unitTujuan.trim().toUpperCase();
        List<String> validUnits = List.of("KB-TK", "SD", "SMP", "SMA");
        if (!validUnits.contains(unit)) {
            throw new IllegalArgumentException("Value parameter tidak valid.");
        }
        if (!isAdmin && !isSarpras) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki akses untuk filter unit tujuan");
        }
        return unit.toLowerCase(); 
    }

    private io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman validateStatusPeminjaman(String status) {
        if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("Semua Status")) {
            return null;
        }
        try {
            return io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Value parameter tidak valid.");
        }
    }

    private List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> validateKategoriAset(String kategori) {
        if (kategori == null || kategori.trim().isEmpty() || kategori.equalsIgnoreCase("Semua Kategori")) {
            return null;
        }
        String cat = kategori.trim().toLowerCase();
        if (cat.equals("barang")) {
            return List.of(io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.BARANG_TIDAK_HABIS_PAKAI);
        } else if (cat.equals("ruangan")) {
            return List.of(io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.RUANG_KELAS, io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.RUANG_NON_KELAS);
        } else {
            throw new IllegalArgumentException("Value parameter tidak valid.");
        }
    }
}
