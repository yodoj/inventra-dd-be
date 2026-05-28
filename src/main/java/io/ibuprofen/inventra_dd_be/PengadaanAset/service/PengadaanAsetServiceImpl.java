package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.UpdatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetDetailResponse;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.TinjauPengadaan;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository.TinjauPengadaanRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PengadaanAsetServiceImpl implements PengadaanAsetService {

    @Autowired
    private PengadaanAsetRepository pengadaanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TinjauPengadaanRepository tinjauRepo;

    // Fungsi untuk membuat pengadaan aset baru
    @Override
    public PengadaanAsetDetailResponse createPengadaan(CreatePengadaanAsetRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        // Mapping data dari request DTO ke entity PengadaanAset
        PengadaanAset pengadaan = new PengadaanAset();
        pengadaan.setNamaAset(request.getNamaAset());
        pengadaan.setKategoriAset(request.getKategoriAset());
        pengadaan.setMerk(request.getMerk());
        pengadaan.setQty(request.getQty());
        pengadaan.setEstimasiHarga(request.getEstimasiHarga());
        pengadaan.setWaktuPengadaan(request.getWaktuPengadaan());

        if (request.getGambarFile() != null && !request.getGambarFile().isEmpty()) {
            pengadaan.setLinkGambar("/uploads/assets/" + saveFileToLocal(request.getGambarFile()));
        } else {
            pengadaan.setLinkGambar(request.getLinkGambar());
        }
        
        // Inisialisasi status pengadaan menjadi "DIAJUKAN" saat dibuat
        pengadaan.setStatusPengadaan("DIAJUKAN");
        pengadaan.setNamaPengaju(userDetails.getName());
        
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
        pengadaan.setUserId(user);

        // Logika penentuan unit berdasarkan peran
        if (roles.contains("ADMIN") || roles.contains("ROLE_ADMIN")) {
            if (request.getUnit() == null || request.getUnit().isEmpty()) {
                throw new IllegalArgumentException("Admin wajib menentukan unit untuk pengadaan ini.");
            }
            pengadaan.setUnit(request.getUnit());
        } else {
            if (request.getUnit() != null && !request.getUnit().isEmpty() && !request.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalArgumentException("Anda hanya bisa membuat pengajuan untuk unit Anda sendiri: " + userDetails.getUnit());
            }
            pengadaan.setUnit(userDetails.getUnit());
        }

        pengadaan.setRolePengaju(user.getRole().name());
        pengadaan.setReviewPengajuan(null);
        pengadaan.setWaktuPengajuan(java.time.LocalDateTime.now());
        
        PengadaanAset saved = pengadaanRepository.save(pengadaan);
        return mapToDetailResponse(saved);
    }

    // Fungsi untuk mendapatkan semua pengadaan aset dengan filter dan sorting
    @Override
    public List<PengadaanAsetResponse> getAllPengadaan(String search, String statusPengadaan, String kategoriAset, String sortBy, String direction) {
        UserDetailsImpl userDetails = getCurrentUser();

        // Penentuan field untuk sorting berdasarkan input parameter
        String sortField = "waktu_pengajuan";
        if (sortBy != null && !sortBy.isBlank()) {
            switch (sortBy) {
                case "waktuPengajuan":
                case "waktu_pengajuan":
                    sortField = "waktu_pengajuan";
                    break;
                case "namaAset":
                case "nama_aset":
                    sortField = "nama_aset";
                    break;
                case "merk":
                    sortField = "merk";
                    break;
                case "statusPengadaan":
                case "status_pengadaan":
                    sortField = "status_pengadaan";
                    break;
                case "kategoriAset":
                case "kategori_aset":
                case "kategori":
                    sortField = "kategori_aset";
                    break;
                default:
                    sortField = "waktu_pengajuan";
            }
        }

        Sort.Direction sortDirection =
                (direction != null && direction.equalsIgnoreCase("ASC"))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, sortField);

        // Normalisasi parameter pencarian untuk query database
        String normalizedSearch =
                (search == null || search.isBlank()) ? null : search.trim().toUpperCase();

        String normalizedStatus =
                (statusPengadaan == null || statusPengadaan.isBlank() || "SEMUA STATUS".equalsIgnoreCase(statusPengadaan))
                        ? null
                        : statusPengadaan.trim().toUpperCase();

        String normalizedKategori =
                (kategoriAset == null || kategoriAset.isBlank() || "SEMUA KATEGORI".equalsIgnoreCase(kategoriAset))
                        ? null
                        : kategoriAset.trim().toUpperCase();

        List<PengadaanAset> results = pengadaanRepository.findByUserWithAllFilters(
                userDetails.getId(),
                normalizedSearch,
                normalizedStatus,
                normalizedKategori,
                sort
        );

        return results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
   
    // Fungsi untuk mendapatkan detail pengadaan aset berdasarkan ID
    @Override
    public PengadaanAsetDetailResponse getPengadaanById(UUID id) {
        UserDetailsImpl userDetails = getCurrentUser();
        PengadaanAset pengadaan = pengadaanRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Pengajuan pengadaan tidak ditemukan"));

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        // Validasi akses
        if (!pengadaan.getUserId().getId().equals(userDetails.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki akses ke data ini");
        }
        if (!roles.contains("ADMIN")){
            if (pengadaan.getUnit() == null || !pengadaan.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalStateException("Unit tidak sesuai dengan akses Anda");
            }
        }
        
        return mapToDetailResponse(pengadaan);
    }

    // Fungsi untuk menghapus pengadaan aset berdasarkan ID
    @Override
    @Transactional
    public void deletePengadaan(UUID id) {
        UserDetailsImpl userDetails = getCurrentUser();
        PengadaanAset pengadaan = pengadaanRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Pengajuan pengadaan tidak ditemukan"));

        // Validasi kepemilikan sebelum penghapusan
        if (!pengadaan.getUserId().getId().equals(userDetails.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Hanya pemilik yang dapat menghapus");
        }

        // Validasi status pengadaan sebelum penghapusan
        String status = pengadaan.getStatusPengadaan();
        if (!status.equals("DIAJUKAN") && !status.equals("DITOLAK")) {
            throw new IllegalStateException("Pengajuan tidak dapat dihapus karena status sudah " + status);
        }

        // Jika pengajuan ditolak, hapus juga data tinjauan terkait untuk menjaga konsistensi data
        if (status.equals("DITOLAK")) {
            List<TinjauPengadaan> relatedReviews = tinjauRepo.findByPengadaan_IdPengadaanIn(List.of(id)); 
            if (!relatedReviews.isEmpty()) {
                tinjauRepo.deleteAll(relatedReviews); 
            }
        }

        pengadaanRepository.delete(pengadaan);
    }

    // Fungsi untuk memperbarui pengadaan aset berdasarkan ID
    @Override
    @Transactional
    public PengadaanAsetDetailResponse updatePengadaan(UUID id, UpdatePengadaanAsetRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser(); 
        Set<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet()); 

        PengadaanAset pengadaan = pengadaanRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Data pengadaan tidak ditemukan."));

        if (!pengadaan.getUserId().getId().equals(userDetails.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki izin untuk mengubah pengajuan ini.");
        }

        // Validasi status pengadaan sebelum memperbarui
        String currentStatus = pengadaan.getStatusPengadaan();
        if (!currentStatus.equals("DIAJUKAN") && !currentStatus.equals("DITOLAK")) {
            throw new IllegalStateException("Hanya pengajuan dengan status DIAJUKAN atau DITOLAK yang dapat diubah.");
        }

        pengadaan.setNamaAset(request.getNamaAset()); 
        pengadaan.setKategoriAset(request.getKategoriAset()); 
        pengadaan.setMerk(request.getMerk()); 
        pengadaan.setQty(request.getQty()); 
        pengadaan.setEstimasiHarga(request.getEstimasiHarga()); 
        pengadaan.setWaktuPengadaan(request.getWaktuPengadaan()); 
        
        if (request.getGambarFile() != null && !request.getGambarFile().isEmpty()) {
            pengadaan.setLinkGambar("/uploads/assets/" + saveFileToLocal(request.getGambarFile()));
        } else {
            pengadaan.setLinkGambar(request.getLinkGambar());
        }

        // Logika penentuan unit berdasarkan peran saat update
        if (roles.contains("ADMIN") || roles.contains("ROLE_ADMIN")) {
            if (request.getUnit() == null || request.getUnit().trim().isEmpty()) {
                throw new IllegalArgumentException("Admin wajib menentukan unit untuk pengadaan ini.");
            }
            pengadaan.setUnit(request.getUnit()); 
        } 
        else {
            if (request.getUnit() != null && !request.getUnit().trim().isEmpty()) {
                if (!request.getUnit().equals(userDetails.getUnit())) {
                    throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah unit pengadaan. Unit Anda adalah: " + userDetails.getUnit());
                }
            }
            pengadaan.setUnit(userDetails.getUnit());
        }

        // Jika pengajuan sebelumnya ditolak, hapus data tinjauan terkait untuk memungkinkan pengajuan baru yang bersih
        if (currentStatus.equals("DITOLAK")) {
            List<TinjauPengadaan> relatedReviews = tinjauRepo.findByPengadaan_IdPengadaanIn(List.of(id)); 
            if (!relatedReviews.isEmpty()) {
                tinjauRepo.deleteAll(relatedReviews);
            }
        }
        
        pengadaan.setStatusPengadaan("DIAJUKAN"); 
        pengadaan.setReviewPengajuan(null); 
        pengadaan.setWaktuPengajuan(java.time.LocalDateTime.now()); 

        PengadaanAset saved = pengadaanRepository.save(pengadaan); 
        return mapToDetailResponse(saved);
    }

    // Helper method untuk mendapatkan informasi user yang sedang login
    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal(); 
    }

    // Helper method untuk memetakan entity PengadaanAset ke response DTO PengadaanAsetResponse
    private PengadaanAsetResponse mapToResponse(PengadaanAset p) {
        return PengadaanAsetResponse.builder()
                .idPengadaan(p.getIdPengadaan())
                .waktuPengajuan(p.getWaktuPengajuan())
                .unit(p.getUnit())
                .namaAset(p.getNamaAset())
                .merk(p.getMerk())
                .qty(p.getQty())
                .estimasiHarga(p.getEstimasiHarga())
                .tanggalPengadaan(p.getWaktuPengadaan())
                .kategori(p.getKategoriAset())
                .linkGambar(p.getLinkGambar())
                .statusPengadaan(p.getStatusPengadaan())
                .build();
    }

    // Helper method untuk memetakan entity PengadaanAset ke response DTO PengadaanAsetDetailResponse
    private PengadaanAsetDetailResponse mapToDetailResponse(PengadaanAset p) {
        String alasan = null;
        if (!"DIAJUKAN".equals(p.getStatusPengadaan())) {
            var tinjauan = tinjauRepo.findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(p.getIdPengadaan(), io.ibuprofen.inventra_dd_be.Profile.model.Role.YAYASAN)
                    .or(() -> tinjauRepo.findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(p.getIdPengadaan(), io.ibuprofen.inventra_dd_be.Profile.model.Role.KEPSEK))
                    .orElse(null);
            
            if (tinjauan != null) {
                alasan = tinjauan.getAlasan(); 
            }
        }
        return PengadaanAsetDetailResponse.builder()
                .idPengadaan(p.getIdPengadaan())
                .waktuPengajuan(p.getWaktuPengajuan())
                .unit(p.getUnit())
                .namaAset(p.getNamaAset())
                .merk(p.getMerk())
                .qty(p.getQty())
                .estimasiHarga(p.getEstimasiHarga())
                .tanggalPengadaan(p.getWaktuPengadaan())
                .kategori(p.getKategoriAset())
                .linkGambar(p.getLinkGambar())
                .statusPengadaan(p.getStatusPengadaan())
                .reviewPengajuan(alasan) 
                .build();
    }

    private String saveFileToLocal(org.springframework.web.multipart.MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            java.nio.file.Path root = java.nio.file.Paths.get("uploads/assets");
            if (!java.nio.file.Files.exists(root)) {
                java.nio.file.Files.createDirectories(root);
            }
            java.nio.file.Files.copy(file.getInputStream(), root.resolve(filename), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }
}