package io.ibuprofen.inventra_dd_be.PengadaanAset.repository;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface PengadaanAsetRepository extends JpaRepository<PengadaanAset, UUID> {
    List<PengadaanAset> findByUnit(String unit, Sort sort);
    
    List<PengadaanAset> findByUserId_Id(UUID userId, Sort sort);
    
    List<PengadaanAset> findByUserId_IdAndNamaAsetContainingIgnoreCaseOrUserId_IdAndMerkContainingIgnoreCase(
            UUID userId1, String namaAset, UUID userId2, String merk, Sort sort);

// Query untuk pencarian aset berdasarkan teks (nama/merk) dan filter kategori/status
@Query(value = """
    SELECT pa.*
    FROM pengadaan_aset pa
    WHERE pa.user_id = :userId
      AND (
            :search IS NULL
            OR UPPER(pa.nama_aset) LIKE CONCAT('%', :search, '%')
            OR UPPER(pa.merk) LIKE CONCAT('%', :search, '%')
      )
      AND (:statusPengadaan IS NULL OR UPPER(pa.status_pengadaan) = :statusPengadaan)
      AND (:kategoriAset IS NULL OR UPPER(pa.kategori_aset) = :kategoriAset)
    """, nativeQuery = true)
List<PengadaanAset> findByUserWithAllFilters(
        @Param("userId") UUID userId,
        @Param("search") String search,
        @Param("statusPengadaan") String statusPengadaan,
        @Param("kategoriAset") String kategoriAset,
        Sort sort);
}