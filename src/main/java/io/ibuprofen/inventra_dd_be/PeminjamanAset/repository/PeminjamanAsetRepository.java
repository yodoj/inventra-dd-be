package io.ibuprofen.inventra_dd_be.PeminjamanAset.repository;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PeminjamanAsetRepository extends JpaRepository<PeminjamanAset, UUID> {
    Page<PeminjamanAset> findByPeminjamId(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.id = :peminjamId AND u.unit = a.unit ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndUnitSendiri(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.id = :peminjamId AND u.unit <> a.unit ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndLintasUnit(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.id = :peminjamId AND u.unit = a.unit " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndUnitSendiriFiltered(
            @Param("peminjamId") UUID peminjamId,
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.id = :peminjamId AND u.unit <> a.unit " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndLintasUnitFiltered(
            @Param("peminjamId") UUID peminjamId,
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.unit = a.unit AND u.role IN (io.ibuprofen.inventra_dd_be.Profile.model.Role.GURU, io.ibuprofen.inventra_dd_be.Profile.model.Role.SISWA) ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findAllUnitSendiri(Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.unit <> a.unit ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findAllLintasUnit(Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.unit = a.unit " +
           "AND u.role IN (io.ibuprofen.inventra_dd_be.Profile.model.Role.GURU, io.ibuprofen.inventra_dd_be.Profile.model.Role.SISWA) " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findAllUnitSendiriFiltered(
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.unit <> a.unit " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findAllLintasUnitFiltered(
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("search") String search,
            Pageable pageable);
}
