package io.ibuprofen.inventra_dd_be.Aset.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AsetRepository extends JpaRepository<Aset, UUID> {
    
    @Query("SELECT COALESCE(SUM(CASE WHEN TYPE(a) = AsetBarang THEN TREAT(a AS AsetBarang).qtyAset ELSE 1 END), 0) " +
           "FROM Aset a WHERE (:unit IS NULL OR a.unit = :unit)")
    long sumAllAssets(@Param("unit") String unit);

    @Query("SELECT COALESCE(SUM(CASE WHEN TYPE(a) = AsetBarang THEN TREAT(a AS AsetBarang).qtyAset ELSE 1 END), 0) " +
           "FROM Aset a WHERE (:unit IS NULL OR a.unit = :unit) AND a.kategoriAset = :kategori")
    long sumAssetsByCategory(@Param("unit") String unit, @Param("kategori") KategoriAset kategori);
}
