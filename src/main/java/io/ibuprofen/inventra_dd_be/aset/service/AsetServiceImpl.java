package io.ibuprofen.inventra_dd_be.Aset.service;

import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetRuangan;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetRuanganRepository;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetBarangResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetRuanganResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.BorrowableAsetResponseDTO;

@Service
public class AsetServiceImpl implements AsetService {

    @Autowired
    private AsetBarangRepository asetBarangRepository;

    @Autowired
    private AsetRuanganRepository asetRuanganRepository;

    @Autowired
    private io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository peminjamanAsetRepository;

    @Override
    public Page<AsetBarangResponseDTO> getAsetBarang(String unit, String kategori, String status, String search,
            Pageable pageable) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        boolean hasAllAccess = roles.stream()
                .anyMatch(role -> role.equals("SARPRAS") || role.equals("YAYASAN") || role.equals("ADMIN") ||
                        role.equals("ROLE_SARPRAS") || role.equals("ROLE_YAYASAN") || role.equals("ROLE_ADMIN"));

        String targetUnit = unit;
        if (!hasAllAccess) {
            targetUnit = userDetails.getUnit();
            if (targetUnit == null || targetUnit.isEmpty()) {
                return Page.empty(pageable);
            }
        }

        Page<AsetBarang> asetPage = asetBarangRepository.findWithFilters(targetUnit, kategori, status, search,
                pageable);
        return asetPage.map(this::mapToAsetBarangDTO);
    }

    @Override
    public Page<AsetRuanganResponseDTO> getAsetRuangan(String unit, String kategori, String status, String search,
            Pageable pageable) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        boolean hasAllAccess = roles.stream()
                .anyMatch(role -> role.equals("SARPRAS") || role.equals("YAYASAN") || role.equals("ADMIN") ||
                        role.equals("ROLE_SARPRAS") || role.equals("ROLE_YAYASAN") || role.equals("ROLE_ADMIN"));

        String targetUnit = unit;
        if (!hasAllAccess) {
            targetUnit = userDetails.getUnit();
            if (targetUnit == null || targetUnit.isEmpty()) {
                return Page.empty(pageable);
            }
        }

        Page<AsetRuangan> asetPage = asetRuanganRepository.findWithFilters(targetUnit, kategori, status, search,
                pageable);
        return asetPage.map(this::mapToAsetRuanganDTO);
    }

    @Override
    public AsetBarangResponseDTO createAsetBarang(CreateAsetBarangRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        AsetBarang asetBarang = new AsetBarang();
        asetBarang.setNamaAset(request.getNamaAset());

        if (request.getGambarFile() != null && !request.getGambarFile().isEmpty()) {
            asetBarang.setGambarUrlAset("/uploads/assets/" + saveFileToLocal(request.getGambarFile()));
        } else {
            asetBarang.setGambarUrlAset(request.getGambarUrlAset());
        }

        asetBarang.setKategoriAset(request.getKategoriAset());
        asetBarang.setStatusAset(io.ibuprofen.inventra_dd_be.Aset.model.StatusAset.TERSEDIA); // Always TERSEDIA on creation
        asetBarang.setKeteranganAset(request.getKeteranganAset());
        asetBarang.setMerkAset(request.getMerkAset());
        asetBarang.setQtyAset(request.getQtyAset());
        asetBarang.setLokasiAset(request.getLokasiAset());

        // Set qty status breakdown: All quantity goes to 'Tersedia' on creation
        asetBarang.setQtyTersedia(request.getQtyAset());
        asetBarang.setQtyRusak(0);
        asetBarang.setQtyPerbaikan(0);
        asetBarang.setQtyDimusnahkan(0);
        asetBarang.setQtyDipinjam(0);

        if (roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN")) {
            asetBarang.setUnit(request.getUnit());
        } else {
            if (request.getUnit() != null && !request.getUnit().isEmpty()
                    && !request.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalArgumentException(
                        "Anda tidak memiliki akses untuk membuat aset di unit " + request.getUnit());
            }
            asetBarang.setUnit(userDetails.getUnit());
        }

        asetBarang.setKodeAset(generateKodeAset("B"));

        validateStatusByCategory(asetBarang.getKategoriAset(), asetBarang.getStatusAset());

        AsetBarang savedAset = asetBarangRepository.save(asetBarang);
        return mapToAsetBarangDTO(savedAset);
    }

    @Override
    public AsetRuanganResponseDTO createAsetRuangan(CreateAsetRuanganRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        AsetRuangan asetRuangan = new AsetRuangan();
        asetRuangan.setNamaAset(request.getNamaAset());
        
        if (request.getGambarFile() != null && !request.getGambarFile().isEmpty()) {
            asetRuangan.setGambarUrlAset("/uploads/assets/" + saveFileToLocal(request.getGambarFile()));
        } else {
            asetRuangan.setGambarUrlAset(request.getGambarUrlAset());
        }

        asetRuangan.setKategoriAset(request.getKategoriAset());
        asetRuangan.setStatusAset(request.getStatusAset());
        asetRuangan.setKeteranganAset(request.getKeteranganAset());

        if (roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN")) {
            asetRuangan.setUnit(request.getUnit());
        } else {
            if (request.getUnit() != null && !request.getUnit().isEmpty()
                    && !request.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalArgumentException(
                        "Anda tidak memiliki akses untuk membuat aset di unit " + request.getUnit());
            }
            asetRuangan.setUnit(userDetails.getUnit());
        }

        asetRuangan.setKodeAset(generateKodeAset("R"));

        validateStatusByCategory(asetRuangan.getKategoriAset(), asetRuangan.getStatusAset());

        AsetRuangan savedAset = asetRuanganRepository.save(asetRuangan);
        return mapToAsetRuanganDTO(savedAset);
    }

    @Override
    public AsetBarangResponseDTO updateAsetBarang(UUID id, UpdateAsetBarangRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        AsetBarang asetBarang = asetBarangRepository.findById(id)
                .orElseThrow(
                        () -> new java.util.NoSuchElementException("Aset barang dengan ID " + id + " tidak ditemukan"));

        // Access Control: Sarpras can only edit their own unit
        if (!(roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN"))) {
            if (!asetBarang.getUnit().equals(userDetails.getUnit())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Anda tidak memiliki akses untuk mengubah aset di unit " + asetBarang.getUnit());
            }
        }

        // Validate unit change for Sarpras (only Yayasan/Admin can change unit)
        if (!(roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN"))) {
            if (request.getUnit() != null && !request.getUnit().isEmpty()
                    && !request.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalArgumentException("Anda tidak dapat memindahkan aset ke unit lain.");
            }
        }

        asetBarang.setNamaAset(request.getNamaAset());

        if (request.getGambarFile() != null && !request.getGambarFile().isEmpty()) {
            asetBarang.setGambarUrlAset("/uploads/assets/" + saveFileToLocal(request.getGambarFile()));
        } else {
            asetBarang.setGambarUrlAset(request.getGambarUrlAset());
        }

        asetBarang.setKategoriAset(request.getKategoriAset());
        asetBarang.setStatusAset(request.getStatusAset());
        asetBarang.setKeteranganAset(request.getKeteranganAset());
        asetBarang.setMerkAset(request.getMerkAset());
        asetBarang.setQtyAset(request.getQtyAset());
        asetBarang.setLokasiAset(request.getLokasiAset());

        // Validate qty sum alignment
        int totalQtyDetails = (request.getQtyTersedia() != null ? request.getQtyTersedia() : 0) +
                (request.getQtyRusak() != null ? request.getQtyRusak() : 0) +
                (request.getQtyPerbaikan() != null ? request.getQtyPerbaikan() : 0) +
                (request.getQtyDimusnahkan() != null ? request.getQtyDimusnahkan() : 0) +
                (request.getQtyDipinjam() != null ? request.getQtyDipinjam() : 0);

        if (totalQtyDetails != request.getQtyAset()) {
            throw new IllegalArgumentException(
                "Jumlah rincian ketersediaan (" + totalQtyDetails + ") harus sama dengan total kuantitas aset (" + request.getQtyAset() + ")");
        }

        // Update qty status breakdown
        asetBarang.setQtyTersedia(request.getQtyTersedia() != null ? request.getQtyTersedia() : 0);
        asetBarang.setQtyRusak(request.getQtyRusak() != null ? request.getQtyRusak() : 0);
        asetBarang.setQtyPerbaikan(request.getQtyPerbaikan() != null ? request.getQtyPerbaikan() : 0);
        asetBarang.setQtyDimusnahkan(request.getQtyDimusnahkan() != null ? request.getQtyDimusnahkan() : 0);
        asetBarang.setQtyDipinjam(request.getQtyDipinjam() != null ? request.getQtyDipinjam() : 0);

        if (roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN")) {
            asetBarang.setUnit(request.getUnit());
        }

        validateStatusByCategory(asetBarang.getKategoriAset(), asetBarang.getStatusAset());

        AsetBarang savedAset = asetBarangRepository.save(asetBarang);
        return mapToAsetBarangDTO(savedAset);
    }

    @Override
    public AsetRuanganResponseDTO updateAsetRuangan(UUID id, UpdateAsetRuanganRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        AsetRuangan asetRuangan = asetRuanganRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Aset ruangan dengan ID " + id + " tidak ditemukan"));

        // Access Control: Sarpras can only edit their own unit
        if (!(roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN"))) {
            if (!asetRuangan.getUnit().equals(userDetails.getUnit())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Anda tidak memiliki akses untuk mengubah aset di unit " + asetRuangan.getUnit());
            }
        }

        // Validate unit change for Sarpras (only Yayasan/Admin can change unit)
        if (!(roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN"))) {
            if (request.getUnit() != null && !request.getUnit().isEmpty()
                    && !request.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalArgumentException("Anda tidak dapat memindahkan aset ke unit lain.");
            }
        }

        asetRuangan.setNamaAset(request.getNamaAset());

        if (request.getGambarFile() != null && !request.getGambarFile().isEmpty()) {
            asetRuangan.setGambarUrlAset("/uploads/assets/" + saveFileToLocal(request.getGambarFile()));
        } else {
            asetRuangan.setGambarUrlAset(request.getGambarUrlAset());
        }

        asetRuangan.setKategoriAset(request.getKategoriAset());
        asetRuangan.setStatusAset(request.getStatusAset());
        asetRuangan.setKeteranganAset(request.getKeteranganAset());

        if (roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN")) {
            asetRuangan.setUnit(request.getUnit());
        }

        validateStatusByCategory(asetRuangan.getKategoriAset(), asetRuangan.getStatusAset());

        AsetRuangan savedAset = asetRuanganRepository.save(asetRuangan);
        return mapToAsetRuanganDTO(savedAset);
    }

    @Override
    public AsetBarangResponseDTO getAsetBarangById(UUID id) {
        AsetBarang asetBarang = asetBarangRepository.findById(id)
                .orElseThrow(
                        () -> new java.util.NoSuchElementException("Aset barang dengan ID " + id + " tidak ditemukan"));
        return mapToAsetBarangDTO(asetBarang);
    }

    @Override
    public AsetRuanganResponseDTO getAsetRuanganById(UUID id) {
        AsetRuangan asetRuangan = asetRuanganRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Aset ruangan dengan ID " + id + " tidak ditemukan"));
        return mapToAsetRuanganDTO(asetRuangan);
    }

    @Override
    public void deleteAsetBarang(UUID id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        AsetBarang asetBarang = asetBarangRepository.findById(id)
                .orElseThrow(
                        () -> new java.util.NoSuchElementException("Aset barang dengan ID " + id + " tidak ditemukan"));

        // Access Control: Sarpras can only delete their own unit
        if (!(roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN"))) {
            if (!asetBarang.getUnit().equals(userDetails.getUnit())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Anda tidak memiliki akses untuk menghapus aset di unit " + asetBarang.getUnit());
            }
        }

        asetBarangRepository.delete(asetBarang);
    }

    @Override
    public void deleteAsetRuangan(UUID id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        AsetRuangan asetRuangan = asetRuanganRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Aset ruangan dengan ID " + id + " tidak ditemukan"));

        // Access Control: Sarpras can only delete their own unit
        if (!(roles.contains("YAYASAN") || roles.contains("ROLE_YAYASAN") || roles.contains("ADMIN")
                || roles.contains("ROLE_ADMIN"))) {
            if (!asetRuangan.getUnit().equals(userDetails.getUnit())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Anda tidak memiliki akses untuk menghapus aset di unit " + asetRuangan.getUnit());
            }
        }

        asetRuanganRepository.delete(asetRuangan);
    }

    private String generateKodeAset(String prefix) {
        Integer maxNum = 0;
        try {
            if (prefix.equals("B")) {
                maxNum = asetBarangRepository.findMaxNumericCode();
            } else {
                maxNum = asetRuanganRepository.findMaxNumericCode();
            }
        } catch (Exception e) {
            // Fallback or log error
        }

        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return String.format("%s%05d", prefix, nextNum);
    }

    private void validateStatusByCategory(KategoriAset kategori, StatusAset status) {
        boolean isValid = false;
        switch (kategori) {
            case BARANG_TIDAK_HABIS_PAKAI:
                isValid = (status == StatusAset.TERSEDIA || status == StatusAset.RUSAK ||
                        status == StatusAset.SEDANG_PERBAIKAN || status == StatusAset.DIMUSNAHKAN ||
                        status == StatusAset.SEDANG_DIPINJAM);
                break;
            case BARANG_HABIS_PAKAI:
                isValid = (status == StatusAset.HABIS || status == StatusAset.TERSEDIA);
                break;
            case RUANG_KELAS:
            case RUANG_NON_KELAS:
                isValid = (status == StatusAset.TERSEDIA || status == StatusAset.SEDANG_PERBAIKAN ||
                        status == StatusAset.SEDANG_DIPINJAM);
                break;
        }

        if (!isValid) {
            throw new IllegalArgumentException("Status " + status + " tidak diizinkan untuk kategori " + kategori);
        }
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    private AsetBarangResponseDTO mapToAsetBarangDTO(AsetBarang aset) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Integer dipinjamSkrg = peminjamanAsetRepository.countOverlappingLoans(aset.getId(), now, now);
        int dipinjamCount = (dipinjamSkrg != null) ? dipinjamSkrg : 0;

        // Dynamic calculation based on physical capacity
        int totalKapasitas = nullToZero(aset.getQtyAset()) - nullToZero(aset.getQtyRusak()) 
                           - nullToZero(aset.getQtyPerbaikan()) - nullToZero(aset.getQtyDimusnahkan());
        int tersediaSkrg = Math.max(0, totalKapasitas - dipinjamCount);

        return AsetBarangResponseDTO.builder()
                .idAset(aset.getId())
                .kodeAset(aset.getKodeAset())
                .gambarUrlAset(aset.getGambarUrlAset())
                .namaAset(aset.getNamaAset())
                .merkAset(aset.getMerkAset())
                .qtyAset(nullToZero(aset.getQtyAset()))
                .lokasiAset(aset.getLokasiAset())
                .kategoriAset(aset.getKategoriAset())
                .statusAset(aset.getStatusAset())
                .qtyTersedia(tersediaSkrg)
                .qtyRusak(nullToZero(aset.getQtyRusak()))
                .qtyPerbaikan(nullToZero(aset.getQtyPerbaikan()))
                .qtyDimusnahkan(nullToZero(aset.getQtyDimusnahkan()))
                .qtyDipinjam(dipinjamCount)
                .keteranganAset(aset.getKeteranganAset())
                .unit(aset.getUnit())
                .build();
    }

    private AsetRuanganResponseDTO mapToAsetRuanganDTO(AsetRuangan aset) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Integer dipinjamSkrg = peminjamanAsetRepository.countOverlappingLoans(aset.getId(), now, now);
        
        StatusAset statusDinamis = aset.getStatusAset();
        if (statusDinamis == StatusAset.TERSEDIA && dipinjamSkrg != null && dipinjamSkrg > 0) {
            statusDinamis = StatusAset.SEDANG_DIPINJAM;
        }

        return AsetRuanganResponseDTO.builder()
                .idAset(aset.getId())
                .kodeAset(aset.getKodeAset())
                .gambarUrlAset(aset.getGambarUrlAset())
                .namaAset(aset.getNamaAset())
                .kategoriAset(aset.getKategoriAset())
                .statusAset(statusDinamis)
                .keteranganAset(aset.getKeteranganAset())
                .unit(aset.getUnit())
                .build();
    }

    @Override
    public List<BorrowableAsetResponseDTO> getBorrowableAssets(String unit) {
        List<BorrowableAsetResponseDTO> result = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Add goods with dynamic stock calculation
        List<AsetBarang> barangList = asetBarangRepository.findBorrowableInUnit(unit);
        for (AsetBarang b : barangList) {
            Integer dipinjamSkrg = peminjamanAsetRepository.countOverlappingLoans(b.getId(), now, now);
            int dipinjamCount = (dipinjamSkrg != null) ? dipinjamSkrg : 0;
            
            int totalKapasitas = nullToZero(b.getQtyAset()) - nullToZero(b.getQtyRusak()) 
                               - nullToZero(b.getQtyPerbaikan()) - nullToZero(b.getQtyDimusnahkan());
            int tersediaSkrg = Math.max(0, totalKapasitas - dipinjamCount);

            result.add(BorrowableAsetResponseDTO.builder()
                    .idAset(b.getId())
                    .kodeAset(b.getKodeAset())
                    .namaAset(b.getNamaAset())
                    .merkAset(b.getMerkAset())
                    .kategoriAset(b.getKategoriAset())
                    .qtyTersedia(tersediaSkrg)
                    .build());
        }

        // Add rooms with dynamic status check
        List<AsetRuangan> ruanganList = asetRuanganRepository.findBorrowableInUnit(unit);
        for (AsetRuangan r : ruanganList) {
            Integer dipinjamSkrg = peminjamanAsetRepository.countOverlappingLoans(r.getId(), now, now);
            int tersediaSkrg = (dipinjamSkrg != null && dipinjamSkrg > 0) ? 0 : 1;

            result.add(BorrowableAsetResponseDTO.builder()
                    .idAset(r.getId())
                    .kodeAset(r.getKodeAset())
                    .namaAset(r.getNamaAset())
                    .merkAset(null)
                    .kategoriAset(r.getKategoriAset())
                    .qtyTersedia(tersediaSkrg)
                    .build());
        }

        return result;
    }

    private int nullToZero(Integer val) {
        return (val != null) ? val : 0;
    }

    private String saveFileToLocal(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            Path root = Paths.get("uploads/assets");
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            Files.copy(file.getInputStream(), root.resolve(filename), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }
}
