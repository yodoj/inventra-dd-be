package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.TinjauPengadaan;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;

public interface TinjauPengadaanRepository extends JpaRepository<TinjauPengadaan, Long> {
  boolean existsByPengadaanIdAndReviewerRole(Long pengadaanId, Role reviewerRole);
  Optional<TinjauPengadaan> findFirstByPengadaanIdAndReviewerRoleOrderByUpdatedAtDesc(Long pengadaanId, Role reviewerRole);
  // Optional<TinjauPengadaan> findByPengadaanId(Long pengadaanId);
  List<TinjauPengadaan> findByPengadaanId(Long pengadaanId);
  List<TinjauPengadaan> findByPengadaanIdIn(List<Long> pengadaanIds);
}
