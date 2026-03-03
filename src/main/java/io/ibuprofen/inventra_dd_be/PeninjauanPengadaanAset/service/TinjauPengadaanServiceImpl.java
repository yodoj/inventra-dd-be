package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.TinjauPengadaan;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository.TinjauPengadaanRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request.tinjauPengadaanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.tinjauPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.security.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetBarangRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import lombok.RequiredArgsConstructor;

@Service("tinjauPengadaanService")
@RequiredArgsConstructor
@Transactional
public class TinjauPengadaanServiceImpl implements TinjauPengadaanService {

  private final TinjauPengadaanRepository repo;
  private final PengadaanAsetRepository pengadaanRepo;
  private final UserRepository userRepository;
  private final AsetBarangRepository asetBarangRepo;

    @Override
    public List<tinjauPengadaanResponseDTO> getAll() {
        // Ambil info user yang sedang login
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();
        String unitUser = currentUser.getUnit(); 

        List<PengadaanAset> pengadaanList;

        // Filter data berdasarkan Role
        if (role == Role.YAYASAN) {
            // Yayasan bisa melihat semua data dari semua unit
            pengadaanList = pengadaanRepo.findAll();
        } else if (role == Role.KEPSEK) {
            // Kepsek hanya bisa melihat data yang unit pengadaannya sama dengan unit dirinya
            pengadaanList = pengadaanRepo.findByUnit(unitUser); 
        } else {
            return Collections.emptyList();
        }

        if (pengadaanList == null || pengadaanList.isEmpty()) return Collections.emptyList();

        List<UUID> ids = pengadaanList.stream().map(PengadaanAset::getIdPengadaan).toList();
        List<TinjauPengadaan> tinjauanList = repo.findByPengadaan_IdPengadaanIn(ids);

        Map<UUID, Map<Role, TinjauPengadaan>> tinjauanByPengadaanAndRole =
            tinjauanList.stream()
                .filter(t -> t.getPengadaanId() != null && t.getReviewerRole() != null)
                .collect(Collectors.groupingBy(
                    TinjauPengadaan::getPengadaanId,
                    Collectors.toMap(
                        TinjauPengadaan::getReviewerRole,
                        Function.identity(),
                        (a, b) -> {
                            LocalDateTime au = a.getUpdatedAt();
                            LocalDateTime bu = b.getUpdatedAt();
                            if (au == null) return b;
                            if (bu == null) return a;
                            return bu.isAfter(au) ? b : a;
                        }
                    )
                ));

        return pengadaanList.stream().map(p -> {
            Map<Role, TinjauPengadaan> byRole = tinjauanByPengadaanAndRole.getOrDefault(p.getIdPengadaan(), Map.of());
            TinjauPengadaan tY = byRole.get(Role.YAYASAN);
            TinjauPengadaan tK = byRole.get(Role.KEPSEK);

            TinjauPengadaan pick = (tY != null) ? tY : tK;

            Status status = (pick != null && pick.getStatus() != null)
                ? pick.getStatus()
                : mapStatusFromString(p.getStatusPengadaan());
            String alasan = (pick != null && pick.getAlasan() != null && !pick.getAlasan().isBlank()) ? pick.getAlasan() : "-";

            return tinjauPengadaanResponseDTO.builder()
                .id(pick != null ? pick.getId() : null)
                .idPengadaan(p.getIdPengadaan())
                .namaAset(p.getNamaAset())
                .linkGambar(p.getLinkGambar())
                .kategori(p.getKategoriAset() != null ? p.getKategoriAset().toString() : null)
                .merk(p.getMerk())
                .qty(p.getQty())
                .namaPengaju(p.getNamaPengaju())
                .estimasiHarga(p.getEstimasiHarga())
                .waktuPengadaan(p.getWaktuPengadaan())
                .statusPengadaan(status)
                .alasan(alasan)
                .kepsekFirstReviewedAt(tK != null ? tK.getKepsekFirstReviewedAt() : null)
                .yayasanFirstReviewedAt(tY != null ? tY.getYayasanFirstReviewedAt() : null)
                .updatedAt(pick != null ? pick.getUpdatedAt() : null)
                .userId(pick != null && pick.getUser() != null ? pick.getUser().getId() : null)
                .reviewerRole(pick != null ? pick.getReviewerRole().toString() : null)
                .namaReviewer(pick != null && pick.getUser() != null ? pick.getUser().getName() : null)
                .build();
        }).collect(Collectors.toList());
    }

  @Override
  public tinjauPengadaanResponseDTO getByPengadaanId(UUID pengadaanId) {
    PengadaanAset p = pengadaanRepo.findById(pengadaanId)
        .orElseThrow(() -> new IllegalStateException("Pengadaan tidak ditemukan"));

    User currentUser = getCurrentUserEntity();
    
    // Validasi: Jika bukan Yayasan, cek apakah unit pengadaan sama dengan unit Kepsek
    if (currentUser.getRole() != Role.YAYASAN && !p.getUnit().equals(currentUser.getUnit())) {
        throw new IllegalStateException("Akses ditolak: Anda tidak memiliki izin melihat data unit lain.");
    }
    // ambil tinjauan terakhir KEPSEK dan YAYASAN
    TinjauPengadaan tK = repo.findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.KEPSEK).orElse(null);
    TinjauPengadaan tY = repo.findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.YAYASAN).orElse(null);

    // yang ditampilin default: kalau yayasan ada -> yayasan, else kepsek, else null
    TinjauPengadaan pick = (tY != null) ? tY : tK;

    Status status = (pick != null && pick.getStatus() != null)
        ? pick.getStatus()
        : mapStatusFromString(p.getStatusPengadaan());

    String alasan = (pick != null && pick.getAlasan() != null && !pick.getAlasan().isBlank())
        ? pick.getAlasan()
        : "-";
    return tinjauPengadaanResponseDTO.builder()
        .id(pick != null ? pick.getId() : null)
        .idPengadaan(p.getIdPengadaan())
        .namaAset(p.getNamaAset())
        .linkGambar(p.getLinkGambar())
        .kategori(p.getKategoriAset() != null ? p.getKategoriAset().toString() : null)
        .merk(p.getMerk())
        .qty(p.getQty())
        .estimasiHarga(p.getEstimasiHarga())
        .waktuPengadaan(p.getWaktuPengadaan())
        .namaPengaju(p.getNamaPengaju())
        .statusPengadaan(status)
        .alasan(alasan)
        .kepsekFirstReviewedAt(tK != null ? tK.getKepsekFirstReviewedAt() : null)
        .yayasanFirstReviewedAt(tY != null ? tY.getYayasanFirstReviewedAt() : null)
        .updatedAt(pick != null ? pick.getUpdatedAt() : null)
        .userId(pick != null && pick.getUser() != null ? pick.getUser().getId() : null)
        .namaReviewer(pick != null && pick.getUser() != null ? pick.getUser().getName() : null)
        .reviewerRole(pick != null ? pick.getReviewerRole().toString() : null)
        .build();
    }

    @Override
    public tinjauPengadaanResponseDTO create(UUID pengadaanId, tinjauPengadaanRequestDTO req) {
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();

        PengadaanAset p = pengadaanRepo.findById(pengadaanId)
                .orElseThrow(() -> new IllegalStateException("Pengadaan tidak ditemukan"));

        // Kepsek hanya dapat mengakses unitnya
        if (role == Role.KEPSEK) {
            if (!p.getUnit().equals(currentUser.getUnit())) {
                throw new IllegalStateException("Akses ditolak: Anda hanya bisa meninjau pengadaan dari unit Anda sendiri.");
            }
        }

        // Cek apakah ROLE INI sudah pernah review atau belum
        if (repo.existsByPengadaan_IdPengadaanAndReviewerRole(pengadaanId, role)) {
            throw new IllegalStateException("Anda sudah melakukan peninjauan. Gunakan menu Update untuk mengubah.");
        }

        Status statusSaatIni = mapStatusFromString(p.getStatusPengadaan());

        // Validasi Alur
        if (role == Role.KEPSEK) {
            if (statusSaatIni != Status.DIAJUKAN) {
                throw new IllegalStateException("Kepsek hanya bisa review jika status masih DIAJUKAN.");
            }
        } else if (role == Role.YAYASAN) {
            // Yayasan hanya boleh create jika Kepsek sudah setuju
            if (statusSaatIni != Status.DISETUJUI_KEPSEK) {
                throw new IllegalStateException("Yayasan belum bisa review sebelum disetujui Kepsek.");
            }
        }

        // Validasi Status yang diperbolehkan berdasarkan Role
        if (role == Role.KEPSEK) {
            if (req.getStatusPengadaan() != Status.DISETUJUI_KEPSEK && req.getStatusPengadaan() != Status.DITOLAK) {
                throw new IllegalStateException("Kepsek hanya boleh memilih status DISETUJUI_KEPSEK atau DITOLAK.");
            }
        } else if (role == Role.YAYASAN) {
            if (req.getStatusPengadaan() != Status.DISETUJUI_YAYASAN && req.getStatusPengadaan() != Status.DITOLAK) {
                throw new IllegalStateException("Yayasan hanya boleh memilih status DISETUJUI_YAYASAN atau DITOLAK.");
            }
        }

        TinjauPengadaan t = new TinjauPengadaan();
        t.setPengadaanId(pengadaanId);
        t.setUser(currentUser);
        t.setReviewerRole(role);
        t.setStatus(req.getStatusPengadaan());
        t.setAlasan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());

        LocalDateTime now = LocalDateTime.now();
        if (role == Role.KEPSEK) t.setKepsekFirstReviewedAt(now);
        else if (role == Role.YAYASAN) t.setYayasanFirstReviewedAt(now);

        TinjauPengadaan saved = repo.save(t);
        
        // UPDATE status di tabel utama (PengadaanAset)
        p.setStatusPengadaan(req.getStatusPengadaan().name());
        pengadaanRepo.save(p);

        return toResponse(saved, p);
    }

    @Override
    public tinjauPengadaanResponseDTO update(UUID pengadaanId, tinjauPengadaanRequestDTO req) {
        User currentUser = getCurrentUserEntity();
        Role role = currentUser.getRole();

        PengadaanAset p = pengadaanRepo.findById(pengadaanId)
            .orElseThrow(() -> new IllegalStateException("Pengadaan tidak ditemukan"));

        if (role == Role.KEPSEK) {
            if (!p.getUnit().equals(currentUser.getUnit())) {
                throw new IllegalStateException("Akses ditolak: Anda hanya bisa memperbarui peninjauan dari unit Anda sendiri.");
            }
        }

        // Cari tinjauan terakhir untuk ROLE INI berdasarkan pengadaanId
        TinjauPengadaan t = repo.findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, role)
                .orElseThrow(() -> new IllegalStateException("Anda belum memiliki akses peninjauan. Silakan lakukan Create terlebih dahulu."));

        if (role == Role.KEPSEK) {
            if (t.getKepsekFirstReviewedAt() == null) {
                throw new IllegalStateException("Akses Ditolak: Anda belum melakukan peninjauan pertama (Create).");
            }
        } else if (role == Role.YAYASAN) {
            if (t.getYayasanFirstReviewedAt() == null) {
                throw new IllegalStateException("Akses Ditolak: Anda belum melakukan peninjauan pertama (Create).");
            }
        }
        if (role == Role.KEPSEK) {
            boolean sudahAdaYayasan = repo.existsByPengadaan_IdPengadaanAndReviewerRole(pengadaanId, Role.YAYASAN);
            if (sudahAdaYayasan) {
                throw new IllegalStateException("Update ditolak: Yayasan sudah memberikan tinjauan.");
            }
        } else if (role == Role.YAYASAN) {
            if (t.getStatus() == Status.DIBELI) {
                throw new IllegalStateException("Update ditolak: Status pengadaan sudah DIBELI.");
            }
        }

        LocalDateTime firstReview = (role == Role.KEPSEK) ? t.getKepsekFirstReviewedAt() : t.getYayasanFirstReviewedAt();
        if (firstReview != null && firstReview.plusDays(2).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Batas waktu update (2 hari) telah berakhir.");
        }

        // Validasi Status yang diperbolehkan berdasarkan Role
        if (role == Role.KEPSEK) {
            if (req.getStatusPengadaan() != Status.DISETUJUI_KEPSEK && req.getStatusPengadaan() != Status.DITOLAK) {
                throw new IllegalStateException("Kepsek hanya boleh memilih status DISETUJUI_KEPSEK atau DITOLAK.");
            }
        } else if (role == Role.YAYASAN) {
            if (req.getStatusPengadaan() != Status.DISETUJUI_YAYASAN && req.getStatusPengadaan() != Status.DITOLAK) {
                throw new IllegalStateException("Yayasan hanya boleh memilih status DISETUJUI_YAYASAN atau DITOLAK.");
            }
        }

        t.setStatus(req.getStatusPengadaan());
        t.setAlasan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());
        t.setUser(currentUser);
        t.setUpdatedAt(LocalDateTime.now());

        TinjauPengadaan saved = repo.save(t);
        p.setStatusPengadaan(req.getStatusPengadaan().name());
        pengadaanRepo.save(p);

        return toResponse(saved, p);
    }

    @Override
    @Transactional
    public tinjauPengadaanResponseDTO beli(UUID pengadaanId, Long hargaFinal, MultipartFile file) {
        User currentUser = getCurrentUserEntity();
        
        // Validasi Akses
        if (currentUser.getRole() != Role.YAYASAN) {
            throw new IllegalStateException("Akses ditolak: Hanya Yayasan yang bisa melakukan pembelian.");
        }

        // Ambil data Pengadaan & Tinjauan
        PengadaanAset p = pengadaanRepo.findById(pengadaanId)
                .orElseThrow(() -> new IllegalStateException("Data pengadaan tidak ditemukan."));
                
        TinjauPengadaan t = repo.findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.YAYASAN)
                .orElseThrow(() -> new IllegalStateException("Tinjauan Yayasan belum dibuat. Silakan setujui dahulu."));

        // Simpan File Bukti ke Storage
        String fileName = null;
        if (file != null && !file.isEmpty()) {
            fileName = saveFileToLocal(file); 
        }

        // Update Data Peninjauan (Simpan Harga & Nama File)
        t.setHarga(hargaFinal);
        t.setBuktiPembelian(fileName);
        t.setStatus(Status.DIBELI);
        t.setUpdatedAt(LocalDateTime.now());
        repo.save(t);

        // Update Status Tabel Utama
        p.setStatusPengadaan(Status.DIBELI.name());
        pengadaanRepo.save(p);

        // UPDATE STOK (Langsung di dalam fungsi yang sama)
        // Cari apakah barang dengan merk tersebut sudah ada di gudang unit tersebut
        AsetBarang aset = asetBarangRepo.findByNamaAsetAndMerkAsetAndUnit(p.getNamaAset(), p.getMerk(), p.getUnit())
            .orElseGet(() -> {
                AsetBarang newAset = new AsetBarang();
                newAset.setNamaAset(p.getNamaAset());
                newAset.setMerkAset(p.getMerk());
                newAset.setQtyAset(0); 
                newAset.setUnit(p.getUnit());
                
                return newAset; 
            });

        aset.setQtyAset(aset.getQtyAset() + p.getQty());
        asetBarangRepo.save(aset);

        return toResponse(t, p);
    }

    private String saveFileToLocal(MultipartFile file) {
        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path root = Paths.get("uploads/bukti-pembelian");
            if (!Files.exists(root)) Files.createDirectories(root);
            Files.copy(file.getInputStream(), root.resolve(filename));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage());
        }
    }


    private tinjauPengadaanResponseDTO toResponse(TinjauPengadaan t, PengadaanAset p) {
    return tinjauPengadaanResponseDTO.builder()
        .id(t.getId())
        .idPengadaan(t.getPengadaanId())
        .namaPengaju(p.getNamaPengaju())
        .namaAset(p.getNamaAset())
        .linkGambar(p.getLinkGambar())
        .kategori(p.getKategoriAset() != null ? p.getKategoriAset().toString() : null)
        .merk(p.getMerk())
        .qty(p.getQty())
        .estimasiHarga(p.getEstimasiHarga())
        .waktuPengadaan(p.getWaktuPengadaan())
        .statusPengadaan(t.getStatus())
        .alasan(t.getAlasan())
        .kepsekFirstReviewedAt(t.getKepsekFirstReviewedAt())
        .yayasanFirstReviewedAt(t.getYayasanFirstReviewedAt())
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
      return Status.valueOf(status);
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
}