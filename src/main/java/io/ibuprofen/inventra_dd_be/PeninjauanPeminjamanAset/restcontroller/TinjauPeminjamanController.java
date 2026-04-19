package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service.TinjauPeminjamanService;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/peminjaman/tinjau")
public class TinjauPeminjamanController {
    @Autowired
    private TinjauPeminjamanService tinjauPeminjamanService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getAllTinjauPeminjaman() {
        List<TinjauPeminjamanResponseDTO> result = tinjauPeminjamanService.getAll();
        
        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Data peninjauan peminjaman aset berhasil diambil")
        );
    }

    @PostMapping("/{idpeminjaman}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> createTinjauPeminjaman(
            @PathVariable("idpeminjaman") UUID idPeminjaman,
            @Valid @RequestBody TinjauPeminjamanRequestDTO request) {
        
        TinjauPeminjamanResponseDTO result = tinjauPeminjamanService.create(idPeminjaman, request);
        
        return ResponseEntity.status(201)
                .body(BaseResponseDTO.created(result, "Peninjauan peminjaman aset berhasil tersimpan"));
    }

    @GetMapping("/{idPeminjaman}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> getPeninjauanById(@PathVariable("idPeminjaman") UUID idPeminjaman) {
        TinjauPeminjamanResponseDTO result = tinjauPeminjamanService.getPeninjauanById(idPeminjaman);
        
        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Detail peninjauan peminjaman berhasil diambil")
        );
    }

    @PutMapping("/{idPeminjaman}")
    @PreAuthorize("hasAnyAuthority('SARPRAS', 'ADMIN')")
    public ResponseEntity<?> updateTinjauPeminjaman(
            @PathVariable("idPeminjaman") UUID idPeminjaman,
            @Valid @RequestBody TinjauPeminjamanRequestDTO request) {
        
        TinjauPeminjamanResponseDTO result = tinjauPeminjamanService.update(idPeminjaman, request);
        return ResponseEntity.ok(
                BaseResponseDTO.ok(result, "Peninjauan peminjaman aset berhasil tersimpan")
        );
    }
}