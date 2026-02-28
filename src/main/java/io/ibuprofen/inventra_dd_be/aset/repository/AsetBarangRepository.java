package io.ibuprofen.inventra_dd_be.Aset.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;

@Repository
public interface AsetBarangRepository extends JpaRepository<AsetBarang, Long> {
        @Query(value = "SELECT ab.*, a.* FROM aset_barang ab JOIN aset a ON ab.id_aset = a.id_aset WHERE " +
                        "(:unit IS NULL OR a.unit = :unit) AND " +
                        "(:kategori IS NULL OR a.kategori_aset = :kategori) AND " +
                        "(:status IS NULL OR a.status_aset = :status) AND " +
                        "(:search IS NULL OR a.nama_aset ILIKE CONCAT('%', :search, '%') OR a.kode_aset ILIKE CONCAT('%', :search, '%') OR ab.merk_aset ILIKE CONCAT('%', :search, '%'))", countQuery = "SELECT count(*) FROM aset_barang ab JOIN aset a ON ab.id_aset = a.id_aset WHERE "
                                        +
                                        "(:unit IS NULL OR a.unit = :unit) AND " +
                                        "(:kategori IS NULL OR a.kategori_aset = :kategori) AND " +
                                        "(:status IS NULL OR a.status_aset = :status) AND " +
                                        "(:search IS NULL OR a.nama_aset ILIKE CONCAT('%', :search, '%') OR a.kode_aset ILIKE CONCAT('%', :search, '%') OR ab.merk_aset ILIKE CONCAT('%', :search, '%'))", nativeQuery = true)
        Page<AsetBarang> findWithFilters(
                        @Param("unit") String unit,
                        @Param("kategori") String kategori,
                        @Param("status") String status,
                        @Param("search") String search,
                        Pageable pageable);

        @Query(value = "SELECT MAX(CAST(SUBSTRING(a.kode_aset, 2) AS INTEGER)) FROM aset_barang ab JOIN aset a ON ab.id_aset = a.id_aset", nativeQuery = true)
        Integer findMaxNumericCode();

        Optional<AsetBarang> findTopByOrderByKodeAsetDesc();
}
