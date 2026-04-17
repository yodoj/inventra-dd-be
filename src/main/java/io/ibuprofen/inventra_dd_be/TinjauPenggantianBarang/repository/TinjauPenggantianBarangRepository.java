package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.TinjauPenggantianBarang;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;

public interface TinjauPenggantianBarangRepository extends JpaRepository<TinjauPenggantianBarang, UUID> {
  boolean existsByPenggantian_IdPenggantianAndReviewerRole(String idPenggantian, Role reviewerRole);
  Optional<TinjauPenggantianBarang> findFirstByPenggantian_IdPenggantianAndReviewerRoleOrderByUpdatedAtDesc(String idPenggantian, Role reviewerRole);
  Optional<TinjauPenggantianBarang> findFirstByPenggantian_IdPenggantianOrderByUpdatedAtDesc(String idPenggantian);
  List<TinjauPenggantianBarang> findByPenggantian_IdPenggantianIn(List<String> idPenggantian);
  
}