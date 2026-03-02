package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.TinjauPengadaan;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository.TinjauPengadaanRepository;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request.tinjauPengadaanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.PengajuanSummary;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.tinjauPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TinjauPengadaanServiceImpl implements TinjauPengadaanService {

  private final TinjauPengadaanRepository repo;
  private final PengajuanClient pengajuanClient;
  private final UserRepository userRepository;

  @Override
  public List<tinjauPengadaanResponseDTO> getAll() {
    List<PengajuanSummary> pengadaanList = pengajuanClient.getAll();
    if (pengadaanList == null || pengadaanList.isEmpty()) return Collections.emptyList();

    List<Long> ids = pengadaanList.stream().map(PengajuanSummary::getId).toList();
    List<TinjauPengadaan> tinjauanList = repo.findByPengadaanIdIn(ids);

    Map<Long, Map<Role, TinjauPengadaan>> tinjauanByPengadaanAndRole =
        tinjauanList.stream()
            .filter(t -> t.getPengadaanId() != null && t.getReviewerRole() != null)
            .collect(Collectors.groupingBy(
                TinjauPengadaan::getPengadaanId,
                Collectors.toMap(
                    TinjauPengadaan::getReviewerRole,
                    Function.identity(),
                    // kalau ada duplikat, ambil yang updatedAt paling baru
                    (a, b) -> {
                      LocalDateTime au = a.getUpdatedAt();
                      LocalDateTime bu = b.getUpdatedAt();
                      if (au == null) return b;
                      if (bu == null) return a;
                      return bu.isAfter(au) ? b : a;
                    }
                )
            ));

    // status dan alasan pakai prioritas
    //    - kalau ada review yayasan -> pakai itu
    //    - else kalau ada review kepsek -> pakai itu
    //    - else default DIAJUKAN & "-"
    return pengadaanList.stream().map(p -> {
      Map<Role, TinjauPengadaan> byRole = tinjauanByPengadaanAndRole.getOrDefault(p.getId(), Map.of());
      TinjauPengadaan tY = byRole.get(Role.YAYASAN);
      TinjauPengadaan tK = byRole.get(Role.KEPSEK);

      TinjauPengadaan pick = (tY != null) ? tY : tK;

      Status status = (pick != null && pick.getStatus() != null)
        ? pick.getStatus()
        : p.getStatus();
      String alasan = (pick != null && pick.getAlasan() != null && !pick.getAlasan().isBlank()) ? pick.getAlasan() : "-";

      return tinjauPengadaanResponseDTO.builder()
          .id(pick != null ? pick.getId() : null)
          .idPengadaan(p.getId())
          .namaAset(p.getNamaAset())
          .linkGambar(p.getLinkGambar())
          .kategori(p.getKategori())
          .merk(p.getMerk())
          .qty(p.getQty())
          .estimasiHarga(p.getEstimasiHarga())
          .waktuPengadaan(p.getWaktuPengadaan())
          .status(status)
          .alasan(alasan)
          .kepsekFirstReviewedAt(tK != null ? tK.getKepsekFirstReviewedAt() : null)
          .yayasanFirstReviewedAt(tY != null ? tY.getYayasanFirstReviewedAt() : null)
          .updatedAt(pick != null ? pick.getUpdatedAt() : null)
          .userId(pick != null && pick.getUser() != null ? pick.getUser().getId() : null)
          .build();
    }).collect(Collectors.toList());
  }

  @Override
  public tinjauPengadaanResponseDTO getByPengadaanId(Long pengadaanId) {
    PengajuanSummary p = pengajuanClient.getById(pengadaanId);

    // ambil tinjauan terakhir KEPSEK dan YAYASAN
    TinjauPengadaan tK = repo.findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.KEPSEK).orElse(null);
    TinjauPengadaan tY = repo.findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.YAYASAN).orElse(null);

    // yang ditampilin default: kalau yayasan ada -> yayasan, else kepsek, else null
    TinjauPengadaan pick = (tY != null) ? tY : tK;

    Status status = (pick != null && pick.getStatus() != null)
        ? pick.getStatus()
        : p.getStatus();

    String alasan = (pick != null && pick.getAlasan() != null && !pick.getAlasan().isBlank())
        ? pick.getAlasan()
        : "-";
    return tinjauPengadaanResponseDTO.builder()
        .id(pick != null ? pick.getId() : null)
        .idPengadaan(p.getId())
        .namaAset(p.getNamaAset())
        .linkGambar(p.getLinkGambar())
        .kategori(p.getKategori())
        .merk(p.getMerk())
        .qty(p.getQty())
        .estimasiHarga(p.getEstimasiHarga())
        .waktuPengadaan(p.getWaktuPengadaan())
        .status(status)
        .alasan(alasan)
        .kepsekFirstReviewedAt(tK != null ? tK.getKepsekFirstReviewedAt() : null)
        .yayasanFirstReviewedAt(tY != null ? tY.getYayasanFirstReviewedAt() : null)
        .updatedAt(pick != null ? pick.getUpdatedAt() : null)
        .userId(pick != null && pick.getUser() != null ? pick.getUser().getId() : null)
        .build();
  }

  @Override
  public tinjauPengadaanResponseDTO create(Long pengadaanId, tinjauPengadaanRequestDTO req) {
    User currentUser = getCurrentUserEntity();
    Role role = currentUser.getRole();

    if (repo.existsByPengadaanIdAndReviewerRole(pengadaanId, role)) {
      throw new IllegalStateException("Peninjauan untuk role ini sudah ada. Jika ingin mengubah, lakukan update.");
    }

    PengajuanSummary p = pengajuanClient.getById(pengadaanId);

    // statusAwal yang dipakai validasi:
    // - KEPSEK: pakai status dari pengajuan
    // - YAYASAN: harus berdasarkan hasil review KEPSEK (kalau belum ada -> dianggap bukan DISETUJUI_KEPSEK)
    Status statusAwal = p.getStatus();

    if (role == Role.YAYASAN) {
      TinjauPengadaan lastKepsek = repo
          .findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.KEPSEK)
          .orElse(null);

      statusAwal = (lastKepsek != null && lastKepsek.getStatus() != null)
          ? lastKepsek.getStatus()
          : p.getStatus();
    }

    if (role == Role.KEPSEK) {
      if (statusAwal != Status.DIAJUKAN) {
        throw new IllegalStateException("KEPSEK hanya boleh membuat peninjauan saat status pengadaan DIAJUKAN");
      }
      if (!(req.getStatus() == Status.DISETUJUI_KEPSEK || req.getStatus() == Status.DITOLAK)) {
        throw new IllegalStateException("KEPSEK saat membuat peninjauan hanya boleh memilih status DISETUJUI_KEPSEK atau DITOLAK");
      }
    }

    if (role == Role.YAYASAN) {
      if (statusAwal != Status.DISETUJUI_KEPSEK) {
        throw new IllegalStateException("YAYASAN hanya boleh membuat peninjauan saat sudah disetujui KEPSEK");
      }
      if (!(req.getStatus() == Status.DISETUJUI_YAYASAN || req.getStatus() == Status.DITOLAK)) {
        throw new IllegalStateException("YAYASAN saat membuat peninjauan hanya boleh memilih status DISETUJUI_YAYASAN atau DITOLAK");
      }
    }

    LocalDateTime now = LocalDateTime.now();

    TinjauPengadaan t = new TinjauPengadaan();
    t.setPengadaanId(pengadaanId);
    t.setUser(currentUser);

    t.setReviewerRole(role);

    t.setStatus(req.getStatus());
    t.setAlasan(req.getAlasan() == null || req.getAlasan().isBlank() ? "-" : req.getAlasan());

    if (role == Role.KEPSEK) t.setKepsekFirstReviewedAt(now);
    else if (role == Role.YAYASAN) t.setYayasanFirstReviewedAt(now);

    TinjauPengadaan saved = repo.save(t);
    return toResponse(saved, p);
  }

  @Override
  public tinjauPengadaanResponseDTO update(Long pengadaanId, tinjauPengadaanRequestDTO req) {
      PengajuanSummary p = pengajuanClient.getById(pengadaanId);
      User currentUser = getCurrentUserEntity();
      Role role = currentUser.getRole();

      TinjauPengadaan t = repo
          .findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, role)
          .orElseThrow(() -> new IllegalStateException("Belum dilakukan penjinjauan sebelumnya. Jika ingin membuat peninjauan, lakukan create."));

      LocalDateTime now = LocalDateTime.now();

      TinjauPengadaan yayasanReview = repo
          .findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.YAYASAN)
          .orElse(null);

      TinjauPengadaan kepsekReview = repo
          .findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(pengadaanId, Role.KEPSEK)
          .orElse(null);

      if (role == Role.KEPSEK) {
        if (yayasanReview != null) {
            throw new IllegalStateException(
                "KEPSEK tidak bisa update karena Yayasan sudah melakukan review."
            );
        }

        LocalDateTime first = t.getKepsekFirstReviewedAt();
        if (first == null) first = t.getCreatedAt();

        boolean withinTwoDays =
            first != null && !first.plusDays(2).isBefore(now);

        if (!withinTwoDays) {
            throw new IllegalStateException(
                "KEPSEK tidak bisa update karena sudah lewat 2 hari."
            );
        }
    }

      if (role == Role.YAYASAN) {
        if (t.getStatus() == Status.DIBELI) {
            throw new IllegalStateException(
                "YAYASAN tidak bisa update karena status sudah DIBELI."
            );
        }

        LocalDateTime first = t.getYayasanFirstReviewedAt();
        if (first == null) first = t.getCreatedAt();

        boolean withinTwoDays =
            first != null && !first.plusDays(2).isBefore(now);

        if (!withinTwoDays) {
            throw new IllegalStateException(
                "YAYASAN tidak bisa update karena sudah lewat 2 hari."
            );
        }
    }

      t.setStatus(req.getStatus());
      t.setAlasan(
          req.getAlasan() == null || req.getAlasan().isBlank()
              ? "-"
              : req.getAlasan()
      );
      t.setUser(currentUser);

      TinjauPengadaan saved = repo.save(t);

      return toResponse(saved, p);
  }


  private tinjauPengadaanResponseDTO toResponse(TinjauPengadaan t, PengajuanSummary p) {
    return tinjauPengadaanResponseDTO.builder()
        .id(t.getId())
        .idPengadaan(t.getPengadaanId())
        .namaAset(p.getNamaAset())
        .linkGambar(p.getLinkGambar())
        .kategori(p.getKategori())
        .merk(p.getMerk())
        .qty(p.getQty())
        .estimasiHarga(p.getEstimasiHarga())
        .waktuPengadaan(p.getWaktuPengadaan())
        .status(t.getStatus())
        .alasan(t.getAlasan())
        .kepsekFirstReviewedAt(t.getKepsekFirstReviewedAt())
        .yayasanFirstReviewedAt(t.getYayasanFirstReviewedAt())
        .updatedAt(t.getUpdatedAt())
        .userId(t.getUser() != null ? t.getUser().getId() : null)
        .build();
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