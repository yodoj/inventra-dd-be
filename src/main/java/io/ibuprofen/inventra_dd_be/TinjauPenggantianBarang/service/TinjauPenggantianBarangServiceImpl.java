package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.service;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.Status;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.TinjauPenggantianBarang;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.repository.TinjauPenggantianBarangRepository;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.request.TinjauPenggantianBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.response.TinjauPenggantianBarangResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.Aset.service.AsetService;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.repository.PenggantianBarangRusakRepo;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.repository.PenggantianBarangRusakRepo;
import lombok.RequiredArgsConstructor;

@Service("TinjauPenggantianBarangService")
@RequiredArgsConstructor
@Transactional
public class TinjauPenggantianBarangServiceImpl implements TinjauPenggantianBarangService {

    @Autowired
    private AsetService asetService;
    
    private final TinjauPenggantianBarangRepository repo;
    private final PenggantianBarangRusakRepo penggantianRepo;
    private final UserRepository userRepository;
    private final AsetBarangRepository asetBarangRepository;
  

    @Override
    public List<TinjauPenggantianBarangResponseDTO> getAll(Status statusPenggantian, String search) {
    // Ambil info user yang sedang login
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();
        String unitUser = currentUser.getUnit(); 

        List<PenggantianBarangRusak> penggantianList;

        // Filter data berdasarkan Role
        if (role == Role.ADMIN) {
            // Admin bisa melihat semua data dari semua unit
            penggantianList = penggantianRepo.findAll().stream()
                .collect(Collectors.toList());
        } else if (role == Role.SARPRAS) {
            // Sarpras hanya bisa melihat data yang unit penggantiannya sama dengan unit dirinya
            penggantianList = penggantianRepo.findByUnitPengaju(unitUser, Sort.by(Sort.Direction.DESC, "waktuPengajuan")).stream()
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }

        if (penggantianList == null || penggantianList.isEmpty()) return Collections.emptyList();

        List<String> ids = penggantianList.stream().map(PenggantianBarangRusak::getIdPenggantian).toList();
        List<TinjauPenggantianBarang> tinjauanList = repo.findByPenggantian_IdPenggantianIn(ids);

        Map<String, TinjauPenggantianBarang> latestTinjauanByPenggantian =
        tinjauanList.stream()
            .filter(t -> t.getIdPenggantian() != null)
            .collect(Collectors.toMap(
                TinjauPenggantianBarang::getIdPenggantian,
                Function.identity(),
                (a, b) -> {
                    LocalDateTime au = a.getUpdatedAt();
                    LocalDateTime bu = b.getUpdatedAt();
                    if (au == null) return b;
                    if (bu == null) return a;
                    return bu.isAfter(au) ? b : a;
                }
            ));

        List<TinjauPenggantianBarangResponseDTO> result = penggantianList.stream().map(p -> {
            TinjauPenggantianBarang pick = latestTinjauanByPenggantian.get(p.getIdPenggantian());

            Status status = (pick != null && pick.getStatus() != null)
                ? pick.getStatus()
                : mapStatusFromString(p.getStatus());
            String alasan = (pick != null && pick.getAlasan() != null && !pick.getAlasan().isBlank()) ? pick.getAlasan() : "-";

            return TinjauPenggantianBarangResponseDTO.builder()
                .id(pick != null ? pick.getId() : null)
                .idPenggantian(p.getIdPenggantian().toString())
                .namaAset(p.getNamaBarang())
                .linkGambar(p.getContohBarang())
                .merk(p.getMerk())
                .qty(p.getQuantity())
                .unitPengaju(p.getUnitPengaju())
                .rolePengaju(p.getRolePengaju())
                .namaPengaju(p.getNamaPengaju())
                .waktuPenggantian(p.getWaktuPenggantian())
                .statusPenggantian(status)
                .alasan(alasan)
                .createdAt(pick != null ? pick.getCreatedAt() : null)
                .updatedAt(pick != null ? pick.getUpdatedAt() : null)
                .userId(pick != null && pick.getUser() != null ? pick.getUser().getId() : null)
                .reviewerRole(pick != null ? pick.getReviewerRole().toString() : null)
                .namaReviewer(pick != null && pick.getUser() != null ? pick.getUser().getName() : null)
                .build();
        }).collect(Collectors.toList());


        // Filter by status 
        if (statusPenggantian != null) {
            result = result.stream()
                .filter(dto -> 
                    dto.getStatusPenggantian() != null &&
                    dto.getStatusPenggantian().equals(statusPenggantian)
                )
                .collect(Collectors.toList());
        }

        // Filter by search, case insensitive
        if (search != null && !search.isBlank()) {
            String keyword = search.trim().toLowerCase();
            result = result.stream()
               .filter(dto ->
                    (dto.getNamaAset()   != null && dto.getNamaAset().toLowerCase().contains(keyword))
                || (dto.getMerk()       != null && dto.getMerk().toLowerCase().contains(keyword))
                )
                .collect(Collectors.toList());
        }

        return result;
    }

  @Override
  public TinjauPenggantianBarangResponseDTO getByPenggantianId(String penggantianId) {
    PenggantianBarangRusak p = penggantianRepo.findById(penggantianId)
        .orElseThrow(() -> new IllegalStateException("Pengajuan Penggantian tidak ditemukan"));

    User currentUser = getCurrentUserEntity();
    
    // Validasi: Jika bukan Admin, cek apakah unit penggantian sama dengan unit Sarpras
    if (currentUser.getRole() != Role.ADMIN && !p.getUnitPengaju().equals(currentUser.getUnit())) {
        throw new IllegalStateException("Akses ditolak: Anda tidak memiliki izin melihat data unit lain.");
    }
    // ambil tinjauan terakhir ADMIN dan SARPRAS
    TinjauPenggantianBarang tA = repo.findFirstByPenggantian_IdPenggantianAndReviewerRoleOrderByUpdatedAtDesc(penggantianId, Role.ADMIN).orElse(null);
    TinjauPenggantianBarang tS = repo.findFirstByPenggantian_IdPenggantianAndReviewerRoleOrderByUpdatedAtDesc(penggantianId, Role.SARPRAS).orElse(null);

    // yang ditampilin default: kalau sarpras ada -> sarpras, else admin, else null
    TinjauPenggantianBarang pick = (tS != null) ? tS : tA;

    Status status = (pick != null && pick.getStatus() != null)
        ? pick.getStatus()
        : mapStatusFromString(p.getStatus());

    String alasan = (pick != null && pick.getAlasan() != null && !pick.getAlasan().isBlank())
        ? pick.getAlasan()
        : "-";
    return TinjauPenggantianBarangResponseDTO.builder()
        .id(pick != null ? pick.getId() : null)
        .idPenggantian(p.getIdPenggantian())
        .namaAset(p.getNamaBarang())
        .linkGambar(p.getContohBarang())
        .merk(p.getMerk())
        .qty(p.getQuantity())
        .unitPengaju(p.getUnitPengaju())
        .rolePengaju(p.getRolePengaju())
        .waktuPenggantian(p.getWaktuPenggantian())
        .namaPengaju(p.getNamaPengaju())
        .statusPenggantian(status)
        .alasan(alasan)
        .createdAt(pick != null ? pick.getCreatedAt() : null)   
        .updatedAt(pick != null ? pick.getUpdatedAt() : null)
        .userId(pick != null && pick.getUser() != null ? pick.getUser().getId() : null)
        .namaReviewer(pick != null && pick.getUser() != null ? pick.getUser().getName() : null)
        .reviewerRole(pick != null ? pick.getReviewerRole().toString() : null)
        .build();
    }

    @Override
    public TinjauPenggantianBarangResponseDTO create(String penggantianId, TinjauPenggantianBarangRequestDTO req) {
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();

        PenggantianBarangRusak p = penggantianRepo.findById(penggantianId)
                .orElseThrow(() -> new IllegalStateException("Penggantian tidak ditemukan"));

        // Sarpras hanya dapat mengakses unitnya
        if (role == Role.SARPRAS) {
            if (!p.getUnitPengaju().equals(currentUser.getUnit())) {
                throw new IllegalStateException("Akses ditolak: Anda hanya bisa meninjau penggantian dari unit Anda sendiri.");
            }
        }

        // Cek apakah ROLE INI sudah pernah review atau belum
        if (repo.existsByPenggantian_IdPenggantianAndReviewerRole(penggantianId, role)) {
            throw new IllegalStateException("Anda sudah melakukan peninjauan. Gunakan menu Update untuk mengubah.");
        }

        Status statusSaatIni = mapStatusFromString(p.getStatus());

        // Validasi Status yang diperbolehkan
        if (statusSaatIni != Status.DIAJUKAN) {
            throw new IllegalStateException("Hanya bisa review jika status masih DIAJUKAN.");
        }
       
        if (req.getStatusPenggantian() != Status.DISETUJUI && req.getStatusPenggantian() != Status.DITOLAK) {
            throw new IllegalStateException("Hanya boleh memilih status DISETUJUI atau DITOLAK.");
        }

        TinjauPenggantianBarang t = new TinjauPenggantianBarang();
        t.setIdPenggantian(penggantianId);
        t.setUser(currentUser);
        t.setReviewerRole(role);
        t.setStatus(req.getStatusPenggantian());
        t.setAlasan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());

        LocalDateTime now = LocalDateTime.now();
        t.setCreatedAt(now);

        TinjauPenggantianBarang saved = repo.save(t);
        
        // UPDATE status di tabel utama (PenggantianBarangRusak)
        p.setStatus(req.getStatusPenggantian().name());
        p.setReviewPengajuan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());
        penggantianRepo.save(p);

        return toResponse(saved, p);
    }

    @Override
    public TinjauPenggantianBarangResponseDTO update(String penggantianId, TinjauPenggantianBarangRequestDTO req) {
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();

        PenggantianBarangRusak p = penggantianRepo.findById(penggantianId)
            .orElseThrow(() -> new IllegalStateException("Penggantian tidak ditemukan"));

        if (role == Role.SARPRAS) {
            if (!p.getUnitPengaju().equals(currentUser.getUnit())) {
                throw new IllegalStateException("Akses ditolak: Anda hanya bisa memperbarui peninjauan dari unit Anda sendiri.");
            }
        }

        // Cari tinjauan terakhir untuk ROLE INI berdasarkan penggantianId
        TinjauPenggantianBarang t = repo.findFirstByPenggantian_IdPenggantianAndReviewerRoleOrderByUpdatedAtDesc(penggantianId, role)
                .orElseThrow(() -> new IllegalStateException("Anda belum memiliki akses peninjauan. Silakan lakukan Create terlebih dahulu."));

        if (t.getCreatedAt() == null) {
            throw new IllegalStateException("Akses Ditolak: Anda belum melakukan peninjauan.");
        }

        LocalDateTime firstReview = t.getCreatedAt();
        if (firstReview != null && firstReview.plusDays(2).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Batas waktu update (2 hari) telah berakhir.");
        }

        t.setStatus(req.getStatusPenggantian());
        t.setAlasan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());
        t.setUser(currentUser);
        t.setUpdatedAt(LocalDateTime.now());

        TinjauPenggantianBarang saved = repo.save(t);
        p.setStatus(req.getStatusPenggantian().name());
        p.setReviewPengajuan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());
        penggantianRepo.save(p);

        return toResponse(saved, p);
    }

    private TinjauPenggantianBarangResponseDTO toResponse(TinjauPenggantianBarang t, PenggantianBarangRusak p) {
    return TinjauPenggantianBarangResponseDTO.builder()
        .id(t.getId())
        .idPenggantian(t.getIdPenggantian())
        .namaPengaju(p.getNamaPengaju())
        .rolePengaju(p.getRolePengaju())
        .namaAset(p.getNamaBarang())
        .linkGambar(p.getContohBarang())
        .merk(p.getMerk())
        .qty(p.getQuantity())
        .unitPengaju(p.getUnitPengaju())
        .waktuPenggantian(p.getWaktuPenggantian())
        .statusPenggantian(t.getStatus())
        .alasan(t.getAlasan())
        .createdAt(t.getCreatedAt())
        .updatedAt(t.getUpdatedAt())
        .userId(t.getUser() != null ? t.getUser().getId() : null)
        .namaReviewer(t.getUser() != null ? t.getUser().getName() : null)
        .reviewerRole(t.getReviewerRole() != null ? t.getReviewerRole().toString() : null)
        .build();
  }

  private Status mapStatusFromString(String status) {
    if (status == null || status.isBlank()) {
        return Status.DIAJUKAN;
    }

    try {
        return Status.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
        return Status.DIAJUKAN;
    }
}

  private UserDetailsImpl getCurrentUserDetails() {
    return (UserDetailsImpl) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
  }

  private User getCurrentUserEntity() {
    UserDetailsImpl principal = getCurrentUserDetails();
    return userRepository.findById(principal.getId())
        .orElseThrow(() -> new IllegalStateException("User tidak ditemukan"));
  }

    private String generateKodeAset(String prefix) {
        Integer maxNum = 0;
        try {
            if (prefix.equals("B")) {
                maxNum = asetBarangRepository.findMaxNumericCode();
            }
        } catch (Exception e) {
        }

        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return String.format("%s%05d", prefix, nextNum);
    }
}