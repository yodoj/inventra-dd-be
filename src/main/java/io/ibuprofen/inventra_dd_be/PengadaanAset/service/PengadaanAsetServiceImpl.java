package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import io.ibuprofen.inventra_dd_be.Profile.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PengadaanAsetServiceImpl implements PengadaanAsetService {

    @Autowired
    private PengadaanAsetRepository pengadaanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PengadaanAsetResponse createPengadaan(CreatePengadaanAsetRequestDTO request) {
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
        return mapToResponse(saved);
    }

    @Override
    public List<PengadaanAsetResponse> getAllPengadaan() {
        UserDetailsImpl userDetails = getCurrentUser();
        List<PengadaanAset> results = pengadaanRepository.findByUserId_Id(userDetails.getId());

        return results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
}