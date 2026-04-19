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

import java.util.UUID;

@RestController
@RequestMapping("/api/peminjaman")
public class PeminjamanAsetRestController {

    @Autowired
    private PeminjamanAsetService peminjamanAsetService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'ADMIN')")
    public ResponseEntity<?> getMyPeminjaman(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) boolean all) {
        
        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        Page<PeminjamanAsetResponseDTO> result;
        if (all && isAdmin) {
            result = peminjamanAsetService.getAllPeminjaman(pageable);
        } else {
            result = peminjamanAsetService.getMyPeminjaman(userDetails.getId(), pageable);
        }
        
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data pengajuan peminjaman unit sendiri retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'SARPRAS', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> getPeminjamanById(@PathVariable UUID id) {
        PeminjamanAsetResponseDTO result = peminjamanAsetService.getPeminjamanById(id);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data peminjaman retrieved successfully"));
    }

    @GetMapping("/lintas-unit")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getMyPeminjamanLintasUnit(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) boolean all) {
        
        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest()
                    .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        Page<PeminjamanAsetResponseDTO> result;
        if (all && isAdmin) {
            result = peminjamanAsetService.getAllPeminjamanLintasUnit(pageable);
        } else {
            result = peminjamanAsetService.getMyPeminjamanLintasUnit(userDetails.getId(), pageable);
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
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> createPeminjamanLintasUnit(@Valid @RequestBody CreatePeminjamanLintasUnitRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.createPeminjamanLintasUnit(request, userDetails.getId());
        return ResponseEntity.status(201).body(BaseResponseDTO.created(result, "Pengajuan peminjaman lintas unit created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> updatePeminjaman(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePeminjamanRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.updatePeminjaman(id, request, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Pengajuan peminjaman updated successfully"));
    }

    @PutMapping("/lintas-unit/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> updatePeminjamanLintasUnit(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePeminjamanLintasUnitRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PeminjamanAsetResponseDTO result = peminjamanAsetService.updatePeminjamanLintasUnit(id, request, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Pengajuan peminjaman lintas unit updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SISWA', 'GURU', 'SUPERADMIN')")
    public ResponseEntity<?> deletePeminjaman(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peminjamanAsetService.deletePeminjaman(id, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Pengajuan peminjaman berhasil dihapus"));
    }

    @DeleteMapping("/lintas-unit/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'SUPERADMIN')")
    public ResponseEntity<?> deletePeminjamanLintasUnit(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        peminjamanAsetService.deletePeminjamanLintasUnit(id, userDetails.getId());
        return ResponseEntity.ok(BaseResponseDTO.ok(null, "Pengajuan peminjaman lintas unit berhasil dihapus"));
    }
}
