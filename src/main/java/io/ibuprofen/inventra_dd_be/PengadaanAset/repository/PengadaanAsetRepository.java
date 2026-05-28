package io.ibuprofen.inventra_dd_be.PengadaanAset.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.BreakdownUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopBiayaResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopDashboardResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TotalPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.BiayaPengadaanChartResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.JumlahAsetChartResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopCepatHabisResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Query(value = """
        SELECT pa.*
        FROM pengadaan_aset pa
        WHERE (
                :search IS NULL
                OR UPPER(pa.nama_aset) LIKE CONCAT('%', :search, '%')
                OR UPPER(pa.merk) LIKE CONCAT('%', :search, '%')
          )
          AND (:statusPengadaan IS NULL OR UPPER(pa.status_pengadaan) = :statusPengadaan)
          AND (:kategoriAset IS NULL OR UPPER(pa.kategori_aset) = :kategoriAset)
          AND (:unit IS NULL OR UPPER(pa.unit) = UPPER(:unit))
        """, nativeQuery = true)
    List<PengadaanAset> findAllWithAllFilters(
            @Param("search") String search,
            @Param("statusPengadaan") String statusPengadaan,
            @Param("kategoriAset") String kategoriAset,
            @Param("unit") String unit,
            Sort sort);

    // Query untuk dashboard pengadaan aset
    // Total Pengadaan (Card)
    @Query("""
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TotalPengadaanResponseDTO(
            COALESCE(SUM(p.qty), 0),
            COALESCE(SUM(p.qty * p.estimasiHarga), 0)
        )
        FROM PengadaanAset p
        WHERE (:tahun IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
        AND (:unit IS NULL OR p.unit = :unit)
        AND p.statusPengadaan = 'DIBELI'
    """)
    TotalPengadaanResponseDTO getTotalPengadaan(
        @Param("tahun") Integer tahun,
        @Param("unit") String unit
    );

    // Breakdown per Unit (Yayasan)
    @Query("""
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.BreakdownUnitResponseDTO(
            p.unit,
            COALESCE(SUM(p.qty), 0),
            COALESCE(SUM(p.qty * p.estimasiHarga), 0)
        )
        FROM PengadaanAset p
        WHERE (:tahun IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
        AND p.statusPengadaan = 'DIBELI'
        GROUP BY p.unit
    """)
    List<BreakdownUnitResponseDTO> getBreakdownPerUnit(
        @Param("tahun") Integer tahun
    );

    // Top 5 Biaya Terbesar
    @Query("""
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopBiayaResponseDTO(
            LOWER(p.namaAset),
            COALESCE(SUM(p.qty * p.estimasiHarga), 0)
        )
        FROM PengadaanAset p
        WHERE (:tahun IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
        AND (:bulan IS NULL OR EXTRACT(MONTH FROM p.waktuPengadaan) = :bulan)
        AND (:kategori IS NULL OR CAST(p.kategoriAset AS string) = :kategori)
        AND (:unit IS NULL OR p.unit = :unit)
        AND p.statusPengadaan = 'DIBELI'
        GROUP BY LOWER(p.namaAset)
        ORDER BY SUM(p.qty * p.estimasiHarga) DESC
        LIMIT 5
    """)
    List<TopBiayaResponseDTO> getTop5Biaya(
        @Param("tahun") Integer tahun,
        @Param("bulan") Integer bulan,
        @Param("kategori") KategoriAset kategori,
        @Param("unit") String unit,
        Pageable pageable
    );

    // Bar chart estimasi biaya pengadaan per tahun
    @Query("""
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.BiayaPengadaanChartResponseDTO(
            CAST(EXTRACT(YEAR FROM p.waktuPengadaan) AS integer),
            COALESCE(SUM(p.qty * p.estimasiHarga), 0)
        )
        FROM PengadaanAset p
        WHERE (:unit IS NULL OR p.unit = :unit)
        AND p.statusPengadaan = 'DIBELI'
        GROUP BY EXTRACT(YEAR FROM p.waktuPengadaan)
        ORDER BY EXTRACT(YEAR FROM p.waktuPengadaan)
    """)
    List<BiayaPengadaanChartResponseDTO> getBiayaPengadaanPerTahun(
        @Param("unit") String unit
    );


    // Bar chart jumlah aset per tahun
    @Query("""
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.JumlahAsetChartResponseDTO(
            CAST(EXTRACT(YEAR FROM p.waktuPengadaan) AS integer),
            COALESCE(SUM(p.qty), 0)
        )
        FROM PengadaanAset p
        WHERE (:unit IS NULL OR p.unit = :unit)
        AND p.statusPengadaan = 'DIBELI'
        GROUP BY EXTRACT(YEAR FROM p.waktuPengadaan)
        ORDER BY EXTRACT(YEAR FROM p.waktuPengadaan)
    """)
    List<JumlahAsetChartResponseDTO> getJumlahAsetPerTahun(
        @Param("unit") String unit
    );

    // Top 5 aset paling cepat habis
    @Query("""
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopCepatHabisResponseDTO(

            LOWER(p.namaAset),

            COUNT(p),

            COALESCE(SUM(p.qty), 0)

        )

        FROM PengadaanAset p

        WHERE EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun
        AND (:bulan IS NULL OR EXTRACT(MONTH FROM p.waktuPengadaan) = :bulan)
        AND p.kategoriAset = io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset.BARANG_HABIS_PAKAI
        AND p.statusPengadaan = 'DIBELI'
        AND (:unit IS NULL OR p.unit = :unit)

        GROUP BY LOWER(p.namaAset)

        ORDER BY COUNT(p) DESC, SUM(p.qty) DESC
    """)
    List<TopCepatHabisResponseDTO> getTop5CepatHabis(

        @Param("tahun") Integer tahun,
        @Param("bulan") Integer bulan,
        @Param("unit") String unit,

        Pageable pageable
    );

    @Query(value = """
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanResponseDTO(
            p.waktuPengajuan,
            p.namaPengaju,
            p.namaAset,
            p.merk,
            p.qty,
            p.waktuPengadaan,
            p.estimasiHarga,
            p.kategoriAset,
            p.unit,
            p.statusPengadaan,
            t.buktiPembelian,
            t.alasan,
            t.harga
        )
        FROM PengadaanAset p
        LEFT JOIN TinjauPengadaan t
            ON t.pengadaan.idPengadaan = p.idPengadaan
            AND t.updatedAt = (
                SELECT MAX(t2.updatedAt)
                FROM TinjauPengadaan t2
                WHERE t2.pengadaan.idPengadaan = p.idPengadaan
            )
        WHERE (CAST(:search AS string) IS NULL
               OR CAST(FUNCTION('replace', LOWER(p.namaAset), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.namaPengaju), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.merk), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR (t.alasan IS NOT NULL AND CAST(FUNCTION('replace', LOWER(t.alasan), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')))
          AND (:status IS NULL OR p.statusPengadaan = :status)
          AND (:kategori IS NULL OR CAST(p.kategoriAset AS string) = :kategori)
          AND (:filterUnit IS NULL OR p.unit = :filterUnit)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengajuan) = :bulan)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengajuan) = :tahun)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:fromDateTime AS timestamp) IS NULL OR p.waktuPengajuan >= :fromDateTime)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:toDateTime AS timestamp) IS NULL OR p.waktuPengajuan <= :toDateTime)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengadaan) = :bulan)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:fromDateOnly AS date) IS NULL OR p.waktuPengadaan >= :fromDateOnly)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:toDateOnly AS date) IS NULL OR p.waktuPengadaan <= :toDateOnly)
        """,
        countQuery = """
        SELECT COUNT(p)
        FROM PengadaanAset p
        LEFT JOIN TinjauPengadaan t
            ON t.pengadaan.idPengadaan = p.idPengadaan
            AND t.updatedAt = (
                SELECT MAX(t2.updatedAt)
                FROM TinjauPengadaan t2
                WHERE t2.pengadaan.idPengadaan = p.idPengadaan
            )
        WHERE (CAST(:search AS string) IS NULL
               OR CAST(FUNCTION('replace', LOWER(p.namaAset), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.namaPengaju), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.merk), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR (t.alasan IS NOT NULL AND CAST(FUNCTION('replace', LOWER(t.alasan), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')))
          AND (:status IS NULL OR p.statusPengadaan = :status)
          AND (:kategori IS NULL OR CAST(p.kategoriAset AS string) = :kategori)
          AND (:filterUnit IS NULL OR p.unit = :filterUnit)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengajuan) = :bulan)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengajuan) = :tahun)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:fromDateTime AS timestamp) IS NULL OR p.waktuPengajuan >= :fromDateTime)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:toDateTime AS timestamp) IS NULL OR p.waktuPengajuan <= :toDateTime)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengadaan) = :bulan)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:fromDateOnly AS date) IS NULL OR p.waktuPengadaan >= :fromDateOnly)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:toDateOnly AS date) IS NULL OR p.waktuPengadaan <= :toDateOnly)
        """)
    Page<LaporanPengadaanResponseDTO> findAllLaporanFiltered(
            @Param("search") String search,
            @Param("status") String status,
            @Param("kategori") String kategori,
            @Param("filterUnit") String filterUnit,
            @Param("bulan") Integer bulan,
            @Param("tahun") Integer tahun,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("fromDateOnly") LocalDate fromDateOnly,
            @Param("toDateOnly") LocalDate toDateOnly,
            @Param("dateField") String dateField,
            Pageable pageable);

    @Query(value = """
        SELECT new io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanResponseDTO(
            p.waktuPengajuan,
            p.namaPengaju,
            p.namaAset,
            p.merk,
            p.qty,
            p.waktuPengadaan,
            p.estimasiHarga,
            p.kategoriAset,
            p.unit,
            p.statusPengadaan,
            t.buktiPembelian,
            t.alasan,
            t.harga
        )
        FROM PengadaanAset p
        LEFT JOIN TinjauPengadaan t
            ON t.pengadaan.idPengadaan = p.idPengadaan
            AND t.updatedAt = (
                SELECT MAX(t2.updatedAt)
                FROM TinjauPengadaan t2
                WHERE t2.pengadaan.idPengadaan = p.idPengadaan
            )
        WHERE p.unit = :unit
          AND (CAST(:search AS string) IS NULL
               OR CAST(FUNCTION('replace', LOWER(p.namaAset), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.namaPengaju), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.merk), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR (t.alasan IS NOT NULL AND CAST(FUNCTION('replace', LOWER(t.alasan), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')))
          AND (:status IS NULL OR p.statusPengadaan = :status)
          AND (:kategori IS NULL OR CAST(p.kategoriAset AS string) = :kategori)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengajuan) = :bulan)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengajuan) = :tahun)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:fromDateTime AS timestamp) IS NULL OR p.waktuPengajuan >= :fromDateTime)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:toDateTime AS timestamp) IS NULL OR p.waktuPengajuan <= :toDateTime)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengadaan) = :bulan)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:fromDateOnly AS date) IS NULL OR p.waktuPengadaan >= :fromDateOnly)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:toDateOnly AS date) IS NULL OR p.waktuPengadaan <= :toDateOnly)
        """,
        countQuery = """
        SELECT COUNT(p)
        FROM PengadaanAset p
        LEFT JOIN TinjauPengadaan t
            ON t.pengadaan.idPengadaan = p.idPengadaan
            AND t.updatedAt = (
                SELECT MAX(t2.updatedAt)
                FROM TinjauPengadaan t2
                WHERE t2.pengadaan.idPengadaan = p.idPengadaan
            )
        WHERE p.unit = :unit
          AND (CAST(:search AS string) IS NULL
               OR CAST(FUNCTION('replace', LOWER(p.namaAset), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.namaPengaju), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR CAST(FUNCTION('replace', LOWER(p.merk), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')
               OR (t.alasan IS NOT NULL AND CAST(FUNCTION('replace', LOWER(t.alasan), ' ', '') AS string) LIKE CONCAT('%', CAST(:search AS string), '%')))
          AND (:status IS NULL OR p.statusPengadaan = :status)
          AND (:kategori IS NULL OR CAST(p.kategoriAset AS string) = :kategori)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengajuan) = :bulan)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengajuan) = :tahun)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:fromDateTime AS timestamp) IS NULL OR p.waktuPengajuan >= :fromDateTime)
          AND (:dateField = 'tanggal_pengadaan' OR CAST(:toDateTime AS timestamp) IS NULL OR p.waktuPengajuan <= :toDateTime)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:bulan AS integer) IS NULL OR EXTRACT(MONTH FROM p.waktuPengadaan) = :bulan)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:tahun AS integer) IS NULL OR EXTRACT(YEAR FROM p.waktuPengadaan) = :tahun)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:fromDateOnly AS date) IS NULL OR p.waktuPengadaan >= :fromDateOnly)
          AND (:dateField <> 'tanggal_pengadaan' OR CAST(:toDateOnly AS date) IS NULL OR p.waktuPengadaan <= :toDateOnly)
        """)
    Page<LaporanPengadaanResponseDTO> findLaporanByUnitFiltered(
            @Param("unit") String unit,
            @Param("search") String search,
            @Param("status") String status,
            @Param("kategori") String kategori,
            @Param("bulan") Integer bulan,
            @Param("tahun") Integer tahun,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("fromDateOnly") LocalDate fromDateOnly,
            @Param("toDateOnly") LocalDate toDateOnly,
            @Param("dateField") String dateField,
            Pageable pageable);

}