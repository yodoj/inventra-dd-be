package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LaporanUtilisasiRepository extends JpaRepository<PeminjamanAset, UUID>, JpaSpecificationExecutor<PeminjamanAset> {

    @Query("SELECT p FROM PeminjamanAset p JOIN FETCH p.aset a JOIN FETCH p.peminjam u WHERE p.statusPeminjaman = 'DISETUJUI'")
    List<PeminjamanAset> findAllApprovedForAggregation();
}
