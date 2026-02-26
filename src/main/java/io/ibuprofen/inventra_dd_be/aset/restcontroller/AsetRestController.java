package io.ibuprofen.inventra_dd_be.Aset.restcontroller;

import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetBarangResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetRuanganResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.service.AsetService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

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
        try {
            if (page < 0 || size < 1) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<AsetBarangResponseDTO> result = asetService.getAsetBarang(unit, kategori, status, search, pageable);
            return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data aset barang retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
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
        try {
            if (page < 0 || size < 1) {
                return ResponseEntity.badRequest()
                        .body(BaseResponseDTO.error(400, "Error: Parameter page atau size tidak valid"));
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<AsetRuanganResponseDTO> result = asetService.getAsetRuangan(unit, kategori, status, search, pageable);
            return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data aset ruangan retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/barang")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> createAsetBarang(@Valid @RequestBody CreateAsetBarangRequestDTO request) {
        try {
            AsetBarangResponseDTO result = asetService.createAsetBarang(request);
            return ResponseEntity.status(201).body(BaseResponseDTO.ok(result, "Aset barang created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponseDTO.error(400, "Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/ruangan")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> createAsetRuangan(@Valid @RequestBody CreateAsetRuanganRequestDTO request) {
        try {
            AsetRuanganResponseDTO result = asetService.createAsetRuangan(request);
            return ResponseEntity.status(201).body(BaseResponseDTO.ok(result, "Aset ruangan created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponseDTO.error(400, "Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }

    @PutMapping("/barang/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> updateAsetBarang(@PathVariable Long id,
            @Valid @RequestBody UpdateAsetBarangRequestDTO request) {
        try {
            AsetBarangResponseDTO result = asetService.updateAsetBarang(id, request);
            return ResponseEntity.ok(BaseResponseDTO.ok(result, "Aset barang updated successfully"));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, "Error: " + e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, "Error: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponseDTO.error(400, "Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }

    @PutMapping("/ruangan/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> updateAsetRuangan(@PathVariable Long id,
            @Valid @RequestBody UpdateAsetRuanganRequestDTO request) {
        try {
            AsetRuanganResponseDTO result = asetService.updateAsetRuangan(id, request);
            return ResponseEntity.ok(BaseResponseDTO.ok(result, "Aset ruangan updated successfully"));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, "Error: " + e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, "Error: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponseDTO.error(400, "Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/barang/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> deleteAsetBarang(@PathVariable Long id) {
        try {
            asetService.deleteAsetBarang(id);
            return ResponseEntity.ok(BaseResponseDTO.ok(null, "Aset barang deleted successfully"));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, "Error: " + e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, "Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/ruangan/{id}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'YAYASAN', 'ADMIN')")
    public ResponseEntity<?> deleteAsetRuangan(@PathVariable Long id) {
        try {
            asetService.deleteAsetRuangan(id);
            return ResponseEntity.ok(BaseResponseDTO.ok(null, "Aset ruangan deleted successfully"));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, "Error: " + e.getMessage()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(BaseResponseDTO.error(403, "Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(BaseResponseDTO.error(500, "Error: " + e.getMessage()));
        }
    }
}
