package io.ibuprofen.inventra_dd_be.PeminjamanAset.repository;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PeminjamanAsetRepository extends JpaRepository<PeminjamanAset, UUID>, JpaSpecificationExecutor<PeminjamanAset> {
    Page<PeminjamanAset> findByPeminjamId(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.id = :peminjamId AND u.unit = a.unit ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndUnitSendiri(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.id = :peminjamId AND u.unit <> a.unit ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndLintasUnit(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.id = :peminjamId AND u.unit = a.unit " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:kategoriAset IS NULL OR a.kategoriAset IN :kategoriAset) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search OR EXISTS (SELECT 1 FROM AsetBarang ab WHERE ab.id = a.id AND LOWER(ab.merkAset) LIKE :search)) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndUnitSendiriFiltered(
            @Param("peminjamId") UUID peminjamId,
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("kategoriAset") java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.id = :peminjamId AND u.unit <> a.unit " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:kategoriAset IS NULL OR a.kategoriAset IN :kategoriAset) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search OR EXISTS (SELECT 1 FROM AsetBarang ab WHERE ab.id = a.id AND LOWER(ab.merkAset) LIKE :search)) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findByPeminjamIdAndLintasUnitFiltered(
            @Param("peminjamId") UUID peminjamId,
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("kategoriAset") java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset,
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
           "AND (:kategoriAset IS NULL OR a.kategoriAset IN :kategoriAset) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search OR EXISTS (SELECT 1 FROM AsetBarang ab WHERE ab.id = a.id AND LOWER(ab.merkAset) LIKE :search)) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findAllUnitSendiriFiltered(
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("kategoriAset") java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a " +
           "WHERE u.unit <> a.unit " +
           "AND (:unit IS NULL OR LOWER(TRIM(a.unit)) = :unit) " +
           "AND (:status IS NULL OR p.statusPeminjaman = :status) " +
           "AND (:kategoriAset IS NULL OR a.kategoriAset IN :kategoriAset) " +
           "AND (:search IS NULL OR LOWER(a.namaAset) LIKE :search OR LOWER(a.kodeAset) LIKE :search OR EXISTS (SELECT 1 FROM AsetBarang ab WHERE ab.id = a.id AND LOWER(ab.merkAset) LIKE :search)) " +
           "ORDER BY p.waktuPengajuan DESC")
    Page<PeminjamanAset> findAllLintasUnitFiltered(
            @Param("unit") String unit,
            @Param("status") StatusPeminjaman status,
            @Param("kategoriAset") java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.qty), 0) FROM PeminjamanAset p " +
           "WHERE p.aset.id = :asetId " +
           "AND p.statusPeminjaman = 'DISETUJUI' " +
           "AND p.waktuPeminjaman < :waktuEnd AND p.waktuPengembalian > :waktuStart")
    Integer countOverlappingLoans(
            @Param("asetId") UUID asetId,
            @Param("waktuStart") java.time.LocalDateTime waktuStart,
            @Param("waktuEnd") java.time.LocalDateTime waktuEnd);

    @Query("SELECT COALESCE(SUM(p.qty), 0) FROM PeminjamanAset p " +
           "WHERE p.aset.id = :asetId " +
           "AND p.id <> :excludeId " +
           "AND p.statusPeminjaman = 'DISETUJUI' " +
           "AND p.waktuPeminjaman < :waktuEnd AND p.waktuPengembalian > :waktuStart")
    Integer countOverlappingLoansExcludeId(
            @Param("asetId") UUID asetId,
            @Param("excludeId") UUID excludeId,
            @Param("waktuStart") java.time.LocalDateTime waktuStart,
            @Param("waktuEnd") java.time.LocalDateTime waktuEnd);

    @Query("SELECT COUNT(p) FROM PeminjamanAset p WHERE p.aset.unit = :unit AND p.statusPeminjaman = 'DISETUJUI'")
    long countApprovedPeminjamanByUnit(@Param("unit") String unit);

    @Query(value = "SELECT EXTRACT(YEAR FROM p.waktu_peminjaman) as period, COUNT(*) as count " +
           "FROM peminjaman_aset p JOIN aset a ON p.id_aset = a.id_aset " +
           "WHERE (:unit IS NULL OR a.unit = :unit) " +
           "AND (:kategori IS NULL OR a.kategori_aset = :kategori) " +
           "AND p.status_peminjaman = 'DISETUJUI' " +
           "AND EXTRACT(YEAR FROM p.waktu_peminjaman) >= :startYear " +
           "GROUP BY period ORDER BY period", nativeQuery = true)
    java.util.List<Object[]> getYearlyTrend(@Param("unit") String unit, @Param("startYear") int startYear, @Param("kategori") String kategori);

    @Query(value = "SELECT EXTRACT(MONTH FROM p.waktu_peminjaman) as period, COUNT(*) as count " +
           "FROM peminjaman_aset p JOIN aset a ON p.id_aset = a.id_aset " +
           "WHERE (:unit IS NULL OR a.unit = :unit) " +
           "AND (:kategori IS NULL OR a.kategori_aset = :kategori) " +
           "AND p.status_peminjaman = 'DISETUJUI' " +
           "AND EXTRACT(YEAR FROM p.waktu_peminjaman) = :year " +
           "GROUP BY period ORDER BY period", nativeQuery = true)
    java.util.List<Object[]> getMonthlyTrend(@Param("unit") String unit, @Param("year") int year, @Param("kategori") String kategori);

    @Query(value = "SELECT CEIL(EXTRACT(DAY FROM p.waktu_peminjaman) / 7.0) as period, COUNT(*) as count " +
           "FROM peminjaman_aset p JOIN aset a ON p.id_aset = a.id_aset " +
           "WHERE (:unit IS NULL OR a.unit = :unit) " +
           "AND (:kategori IS NULL OR a.kategori_aset = :kategori) " +
           "AND p.status_peminjaman = 'DISETUJUI' " +
           "AND EXTRACT(YEAR FROM p.waktu_peminjaman) = :year " +
           "AND EXTRACT(MONTH FROM p.waktu_peminjaman) = :month " +
           "GROUP BY period ORDER BY period", nativeQuery = true)
    java.util.List<Object[]> getWeeklyTrend(@Param("unit") String unit, @Param("year") int year, @Param("month") int month, @Param("kategori") String kategori);

    @Query(value = "SELECT a.kode_aset, a.nama_aset, ab.merk_aset, a.unit, COUNT(p.id_peminjaman) as freq, a.kategori_aset " +
           "FROM peminjaman_aset p JOIN aset a ON p.id_aset = a.id_aset " +
           "LEFT JOIN aset_barang ab ON a.id_aset = ab.id_aset " +
           "WHERE p.status_peminjaman = 'DISETUJUI' " +
           "AND (:unit IS NULL OR a.unit = :unit) " +
           "AND (:year IS NULL OR EXTRACT(YEAR FROM p.waktu_peminjaman) = :year) " +
           "AND (:month IS NULL OR EXTRACT(MONTH FROM p.waktu_peminjaman) = :month) " +
           "AND (:kategori IS NULL OR a.kategori_aset = :kategori) " +
           "GROUP BY a.kode_aset, a.nama_aset, ab.merk_aset, a.unit, a.kategori_aset " +
           "ORDER BY freq DESC, a.nama_aset ASC LIMIT 5", nativeQuery = true)
    java.util.List<Object[]> findTopBorrowed(
            @Param("unit") String unit,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("kategori") String kategori);

    long countByStatusPeminjaman(StatusPeminjaman status);
}
