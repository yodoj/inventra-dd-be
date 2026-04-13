package io.ibuprofen.inventra_dd_be.PeminjamanAset.repository;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PeminjamanAsetRepository extends JpaRepository<PeminjamanAset, UUID> {
    Page<PeminjamanAset> findByPeminjamId(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.id = :peminjamId AND u.unit = a.unit")
    Page<PeminjamanAset> findByPeminjamIdAndUnitSendiri(UUID peminjamId, Pageable pageable);

    @Query("SELECT p FROM PeminjamanAset p JOIN p.peminjam u JOIN p.aset a WHERE u.id = :peminjamId AND u.unit <> a.unit")
    Page<PeminjamanAset> findByPeminjamIdAndLintasUnit(UUID peminjamId, Pageable pageable);
}
