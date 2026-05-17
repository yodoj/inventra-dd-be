package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.repository;

import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PenggantianBarangRusakRepo extends JpaRepository<PenggantianBarangRusak, String> {
    List<PenggantianBarangRusak> findByUserId(UUID userId);
    Optional<PenggantianBarangRusak> findByIdPenggantian(String idPenggantian);
    List<PenggantianBarangRusak> findByUnitPengaju(String unit, Sort sort);

    @org.springframework.data.jpa.repository.Query(value = "SELECT a.kode_aset, a.nama_aset, ab.merk_aset, a.unit, COUNT(pbr.id_penggantian) as freq, a.kategori_aset " +
           "FROM penggantian_barang_rusak pbr " +
           "JOIN aset a ON LOWER(pbr.nama_barang) = LOWER(a.nama_aset) " +
           "LEFT JOIN aset_barang ab ON a.id_aset = ab.id_aset AND (pbr.merk IS NULL OR LOWER(pbr.merk) = LOWER(ab.merk_aset)) " +
           "WHERE (:unit IS NULL OR a.unit = :unit) " +
           "AND (:year IS NULL OR EXTRACT(YEAR FROM pbr.waktu_pengajuan) = :year) " +
           "AND (:month IS NULL OR EXTRACT(MONTH FROM pbr.waktu_pengajuan) = :month) " +
           "AND (:kategori IS NULL OR a.kategori_aset = :kategori) " +
           "GROUP BY a.kode_aset, a.nama_aset, ab.merk_aset, a.unit, a.kategori_aset " +
           "ORDER BY freq DESC, a.nama_aset ASC LIMIT 5", nativeQuery = true)
    java.util.List<Object[]> findTopDamaged(
            @org.springframework.data.repository.query.Param("unit") String unit,
            @org.springframework.data.repository.query.Param("year") Integer year,
            @org.springframework.data.repository.query.Param("month") Integer month,
            @org.springframework.data.repository.query.Param("kategori") String kategori);
}
