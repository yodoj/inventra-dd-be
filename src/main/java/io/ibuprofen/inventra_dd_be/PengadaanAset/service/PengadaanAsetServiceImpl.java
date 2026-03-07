package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.UpdatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetDetailResponse;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.TinjauPengadaan;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository.TinjauPengadaanRepository;
import io.ibuprofen.inventra_dd_be.Profile.security.services.UserDetailsImpl;
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

    @Override
    public PengadaanAsetDetailResponse createPengadaan(CreatePengadaanAsetRequestDTO request) {
        UserDetailsImpl userDetails = getCurrentUser();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        PengadaanAset pengadaan = new PengadaanAset();
        pengadaan.setNamaAset(request.getNamaAset());
        pengadaan.setKategoriAset(request.getKategoriAset());
        pengadaan.setMerk(request.getMerk());
        pengadaan.setQty(request.getQty());
        pengadaan.setEstimasiHarga(request.getEstimasiHarga());
        pengadaan.setWaktuPengadaan(request.getWaktuPengadaan());
        pengadaan.setLinkGambar(request.getLinkGambar());
        
        pengadaan.setStatusPengadaan("DIAJUKAN");
        pengadaan.setNamaPengaju(userDetails.getName());
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
        pengadaan.setUserId(user);

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

        pengadaan.setReviewPengajuan(null);
        pengadaan.setWaktuPengajuan(java.time.LocalDateTime.now());
        PengadaanAset saved = pengadaanRepository.save(pengadaan);
        return mapToDetailResponse(saved);
    }

    @Override
    public List<PengadaanAsetResponse> getAllPengadaan(String search, String sortBy, String direction) {
        UserDetailsImpl userDetails = getCurrentUser();
        List<PengadaanAset> results;

        // Default sorting
        String sortField = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "waktuPengajuan";
        Sort.Direction sortDirection = (direction != null && direction.equalsIgnoreCase("ASC")) ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(sortDirection, sortField);

        if (search != null && !search.trim().isEmpty()) {
            results = pengadaanRepository
                    .findByUserId_IdAndNamaAsetContainingIgnoreCaseOrUserId_IdAndMerkContainingIgnoreCase(
                            userDetails.getId(), search, userDetails.getId(), search, sort);
        } else {
            results = pengadaanRepository.findByUserId_Id(userDetails.getId(), sort);
        }

        return results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PengadaanAsetDetailResponse getPengadaanById(UUID id) {
        UserDetailsImpl userDetails = getCurrentUser();
        PengadaanAset pengadaan = pengadaanRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Pengajuan pengadaan tidak ditemukan"));

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        if (!roles.contains("ADMIN") && !roles.contains("ROLE_ADMIN")) {
            
            if (!pengadaan.getUserId().getId().equals(userDetails.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Anda tidak memiliki akses ke data ini");
            }
            
            if (pengadaan.getUnit() == null || !pengadaan.getUnit().equals(userDetails.getUnit())) {
                throw new IllegalStateException("Unit tidak sesuai dengan akses Anda");
            }
        }
        
        return mapToDetailResponse(pengadaan);
    }

    @Override
    @Transactional
    public void deletePengadaan(UUID id) {
        UserDetailsImpl userDetails = getCurrentUser();
        PengadaanAset pengadaan = pengadaanRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Pengajuan pengadaan tidak ditemukan"));

        if (!pengadaan.getUserId().getId().equals(userDetails.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Hanya pemilik yang dapat menghapus");
        }

        String status = pengadaan.getStatusPengadaan();
        if (!status.equals("DIAJUKAN") && !status.equals("DITOLAK")) {
            throw new IllegalStateException("Pengajuan tidak dapat dihapus karena status sudah " + status);
        }

        if (status.equals("DITOLAK")) {
            List<TinjauPengadaan> relatedReviews = tinjauRepo.findByPengadaan_IdPengadaanIn(List.of(id)); 
            if (!relatedReviews.isEmpty()) {
                tinjauRepo.deleteAll(relatedReviews); 
            }
        }

        pengadaanRepository.delete(pengadaan);
    }

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
        pengadaan.setLinkGambar(request.getLinkGambar()); 

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

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal(); 
    }

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
}