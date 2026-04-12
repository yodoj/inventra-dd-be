package io.ibuprofen.inventra_dd_be.PeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.response.PeminjamanAsetResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PeminjamanAsetService {
    Page<PeminjamanAsetResponseDTO> getMyPeminjaman(UUID userId, Pageable pageable);
    Page<PeminjamanAsetResponseDTO> getMyPeminjamanLintasUnit(UUID userId, Pageable pageable);

    PeminjamanAsetResponseDTO createPeminjaman(io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.request.CreatePeminjamanRequestDTO request, UUID userId);

    PeminjamanAsetResponseDTO createPeminjamanLintasUnit(io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.request.CreatePeminjamanLintasUnitRequestDTO request, UUID userId);
}
