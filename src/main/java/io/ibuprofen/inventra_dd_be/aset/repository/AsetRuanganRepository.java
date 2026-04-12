package io.ibuprofen.inventra_dd_be.Aset.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.AsetRuangan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AsetRuanganRepository extends JpaRepository<AsetRuangan, UUID> {
        @Query(value = "SELECT ar.*, a.* FROM aset_ruangan ar JOIN aset a ON ar.id_aset = a.id_aset WHERE " +
                        "(:unit IS NULL OR a.unit = :unit) AND " +
                        "(:kategori IS NULL OR a.kategori_aset = :kategori) AND " +
                        "(:status IS NULL OR a.status_aset = :status) AND " +
                        "(:search IS NULL OR a.nama_aset ILIKE CONCAT('%', :search, '%') OR a.kode_aset ILIKE CONCAT('%', :search, '%'))", countQuery = "SELECT count(*) FROM aset_ruangan ar JOIN aset a ON ar.id_aset = a.id_aset WHERE "
                                        +
                                        "(:unit IS NULL OR a.unit = :unit) AND " +
                                        "(:kategori IS NULL OR a.kategori_aset = :kategori) AND " +
                                        "(:status IS NULL OR a.status_aset = :status) AND " +
                                        "(:search IS NULL OR a.nama_aset ILIKE CONCAT('%', :search, '%') OR a.kode_aset ILIKE CONCAT('%', :search, '%'))", nativeQuery = true)
        Page<AsetRuangan> findWithFilters(
                        @Param("unit") String unit,
                        @Param("kategori") String kategori,
                        @Param("status") String status,
                        @Param("search") String search,
                        Pageable pageable);

        @Query(value = "SELECT MAX(CAST(SUBSTRING(a.kode_aset, 2) AS INTEGER)) FROM aset_ruangan ar JOIN aset a ON ar.id_aset = a.id_aset", nativeQuery = true)
        Integer findMaxNumericCode();

        Optional<AsetRuangan> findTopByOrderByKodeAsetDesc();

        @Query("SELECT ar FROM AsetRuangan ar WHERE ar.unit = :unit AND ar.statusAset = 'TERSEDIA'")
        java.util.List<AsetRuangan> findBorrowableInUnit(@Param("unit") String unit);
}
