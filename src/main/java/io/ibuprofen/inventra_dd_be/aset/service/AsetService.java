package io.ibuprofen.inventra_dd_be.Aset.service;

import java.util.UUID;

import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.CreateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.request.UpdateAsetRuanganRequestDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetBarangResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.AsetRuanganResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AsetService {
        Page<AsetBarangResponseDTO> getAsetBarang(String unit, String kategori, String status, String search,
                        Pageable pageable);

        Page<AsetRuanganResponseDTO> getAsetRuangan(String unit, String kategori, String status, String search,
                        Pageable pageable);

        AsetBarangResponseDTO createAsetBarang(CreateAsetBarangRequestDTO request);

        AsetRuanganResponseDTO createAsetRuangan(CreateAsetRuanganRequestDTO request);

        AsetBarangResponseDTO updateAsetBarang(UUID id, UpdateAsetBarangRequestDTO request);

        AsetRuanganResponseDTO updateAsetRuangan(UUID id, UpdateAsetRuanganRequestDTO request);

        AsetBarangResponseDTO getAsetBarangById(UUID id);

        AsetRuanganResponseDTO getAsetRuanganById(UUID id);

        void deleteAsetBarang(UUID id);

        void deleteAsetRuangan(UUID id);

        java.util.List<io.ibuprofen.inventra_dd_be.Aset.restdto.response.BorrowableAsetResponseDTO> getBorrowableAssets(String unit);
}
