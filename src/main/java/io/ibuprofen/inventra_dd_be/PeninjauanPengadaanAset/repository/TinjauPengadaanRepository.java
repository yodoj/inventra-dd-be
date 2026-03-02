package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.TinjauPengadaan;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;

public interface TinjauPengadaanRepository extends JpaRepository<TinjauPengadaan, Long> {
  
  boolean existsByPengadaan_IdPengadaanAndReviewerRole(UUID idPengadaan, Role reviewerRole);

  Optional<TinjauPengadaan> findFirstByPengadaan_IdPengadaanAndReviewerRoleOrderByUpdatedAtDesc(UUID idPengadaan, Role reviewerRole);

  List<TinjauPengadaan> findByPengadaan_IdPengadaanIn(List<UUID> idPengadaans);
}