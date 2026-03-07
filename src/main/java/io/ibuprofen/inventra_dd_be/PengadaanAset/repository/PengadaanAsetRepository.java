package io.ibuprofen.inventra_dd_be.PengadaanAset.repository;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface PengadaanAsetRepository extends JpaRepository<PengadaanAset, UUID> {
    List<PengadaanAset> findByUnit(String unit, Sort sort);
    List<PengadaanAset> findByUserId_Id(UUID userId, Sort sort);
    List<PengadaanAset> findByUserId_IdAndNamaAsetContainingIgnoreCaseOrUserId_IdAndMerkContainingIgnoreCase(
            UUID userId1, String namaAset, UUID userId2, String merk, Sort sort);
}