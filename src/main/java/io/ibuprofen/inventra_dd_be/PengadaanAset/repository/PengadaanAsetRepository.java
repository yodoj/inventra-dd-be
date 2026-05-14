package io.ibuprofen.inventra_dd_be.PengadaanAset.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.BreakdownUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopBiayaResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TopDashboardResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.TotalPengadaanResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        AND (:kategori IS NULL OR p.kategoriAset = :kategori)
        AND (:unit IS NULL OR p.unit = :unit)
        AND p.statusPengadaan = 'DIBELI'
        GROUP BY LOWER(p.namaAset)
        ORDER BY SUM(p.qty * p.estimasiHarga) DESC
    """)
    List<TopBiayaResponseDTO> getTop5Biaya(
        @Param("tahun") Integer tahun,
        @Param("bulan") Integer bulan,
        @Param("kategori") KategoriAset kategori,
        @Param("unit") String unit,
        Pageable pageable
    );

}