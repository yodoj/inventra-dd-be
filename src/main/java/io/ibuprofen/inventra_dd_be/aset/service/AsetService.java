package io.ibuprofen.inventra_dd_be.Aset.service;

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

        AsetBarangResponseDTO updateAsetBarang(Long id, UpdateAsetBarangRequestDTO request);

        AsetRuanganResponseDTO updateAsetRuangan(Long id, UpdateAsetRuanganRequestDTO request);

        AsetBarangResponseDTO getAsetBarangById(Long id);

        AsetRuanganResponseDTO getAsetRuanganById(Long id);

        void deleteAsetBarang(Long id);

        void deleteAsetRuangan(Long id);
}
