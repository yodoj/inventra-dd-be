package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.repository;

import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PenggantianBarangRusakRepo extends JpaRepository<PenggantianBarangRusak, String> {
    List<PenggantianBarangRusak> findByUserId(UUID userId);
    Optional<PenggantianBarangRusak> findByIdPenggantian(String idPenggantian);
}
