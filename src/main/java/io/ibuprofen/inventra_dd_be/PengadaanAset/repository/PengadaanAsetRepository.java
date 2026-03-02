package io.ibuprofen.inventra_dd_be.PengadaanAset.repository;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface PengadaanAsetRepository extends JpaRepository<PengadaanAset, UUID> {
    List<PengadaanAset> findByUserId(java.util.UUID userId);
}