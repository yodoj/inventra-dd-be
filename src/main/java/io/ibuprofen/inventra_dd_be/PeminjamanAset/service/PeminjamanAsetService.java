package io.ibuprofen.inventra_dd_be.PeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.response.PeminjamanAsetResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.request.*;

public interface PeminjamanAsetService {
    Page<PeminjamanAsetResponseDTO> getMyPeminjaman(UUID userId, String unitTujuan, io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable);
    Page<PeminjamanAsetResponseDTO> getMyPeminjamanLintasUnit(UUID userId, String unitTujuan, io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable);

    Page<PeminjamanAsetResponseDTO> getAllPeminjaman(String unitTujuan, io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable);
    Page<PeminjamanAsetResponseDTO> getAllPeminjamanLintasUnit(String unitTujuan, io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman statusPeminjaman, java.util.List<io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset> kategoriAset, String search, Pageable pageable);

    PeminjamanAsetResponseDTO getPeminjamanById(UUID id);

    PeminjamanAsetResponseDTO createPeminjaman(CreatePeminjamanRequestDTO request, UUID userId);

    PeminjamanAsetResponseDTO createPeminjamanLintasUnit(CreatePeminjamanLintasUnitRequestDTO request, UUID userId);

    PeminjamanAsetResponseDTO updatePeminjaman(UUID idPeminjaman, UpdatePeminjamanRequestDTO request, UUID userId);

    PeminjamanAsetResponseDTO updatePeminjamanLintasUnit(UUID idPeminjaman, UpdatePeminjamanLintasUnitRequestDTO request, UUID userId);
    
    void deletePeminjaman(UUID id, UUID userId);
    
    void deletePeminjamanLintasUnit(UUID id, UUID userId);
}
