package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.repository;

import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.model.TinjauPeminjaman;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TinjauPeminjamanRepository extends JpaRepository<TinjauPeminjaman, Long> {
    // Mencari peninjauan berdasarkan ID peninjauan
    Optional<TinjauPeminjaman> findByIdPeninjauan(Long idPeninjauan);

    // Mencari peninjauan berdasarkan ID Peminjaman
    Optional<TinjauPeminjaman> findByPeminjaman_Id(UUID idPeminjaman);

    // Query untuk filter berdasarkan status peminjaman, unit, dan tanggal peminjaman
    @Query("SELECT t FROM TinjauPeminjaman t JOIN t.peminjaman p JOIN p.aset a " +
           "WHERE (:status IS NULL OR t.statusPeminjaman = :status) " +
           "AND (:unit IS NULL OR a.unit = :unit) " +
           "AND (:tanggal IS NULL OR CAST(p.waktuPeminjaman AS date) = :tanggal) " +
           "ORDER BY p.waktuPengajuan DESC")
    List<TinjauPeminjaman> findWithFilters(
            @Param("status") StatusPeminjaman status,
            @Param("unit") String unit,
            @Param("tanggal") LocalDate tanggal
    );
}
