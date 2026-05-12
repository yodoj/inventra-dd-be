package io.ibuprofen.inventra_dd_be.Aset.restcontroller;

import io.ibuprofen.inventra_dd_be.Aset.restdto.response.DashboardAssetResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.PeminjamanTrendResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.PeminjamanUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.service.DashboardAssetService;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardAssetRestController {

    @Autowired
    private DashboardAssetService dashboardAssetService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/jumlah-aset")
    @PreAuthorize("hasAnyAuthority('YAYASAN', 'SARPRAS', 'KEPSEK', 'ADMIN')")
    public ResponseEntity<?> getJumlahAset(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String unit,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String kategori
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, "User not found"));
        }

        DashboardAssetResponseDTO result = dashboardAssetService.getAsetSummary(userOptional.get(), unit, kategori);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data jumlah aset berhasil diambil"));
    }

    @GetMapping("/peminjaman-per-unit")
    @PreAuthorize("hasAnyAuthority('YAYASAN', 'ADMIN')")
    public ResponseEntity<?> getPeminjamanPerUnit() {
        List<PeminjamanUnitResponseDTO> result = dashboardAssetService.getPeminjamanPerUnit();
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data peminjaman per unit berhasil diambil"));
    }

    @GetMapping("/tren-peminjaman")
    @PreAuthorize("hasAnyAuthority('YAYASAN', 'SARPRAS', 'KEPSEK', 'ADMIN')")
    public ResponseEntity<?> getTrenPeminjaman(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer tahun,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer bulan,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String unit,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String kategori
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(BaseResponseDTO.error(404, "User not found"));
        }

        int year = (tahun != null) ? tahun : java.time.Year.now().getValue();
        List<PeminjamanTrendResponseDTO> result = dashboardAssetService.getPeminjamanTrend(userOptional.get(), year, bulan, unit, kategori);
        return ResponseEntity.ok(BaseResponseDTO.ok(result, "Data tren peminjaman berhasil diambil"));
    }
}
