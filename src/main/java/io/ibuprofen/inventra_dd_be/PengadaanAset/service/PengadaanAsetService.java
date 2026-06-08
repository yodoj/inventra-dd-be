package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.UpdatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetDetailResponse;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import java.util.List;
import java.util.UUID;

public interface PengadaanAsetService {
    PengadaanAsetDetailResponse createPengadaan(CreatePengadaanAsetRequestDTO request);

    List<PengadaanAsetResponse> getAllPengadaan(
        String search, 
        String statusPengadaan, 
        String kategoriAset, 
        String sortBy, 
        String direction,
        String unit);

    PengadaanAsetDetailResponse getPengadaanById(UUID id);

    void deletePengadaan(UUID id);
    
    PengadaanAsetDetailResponse updatePengadaan(UUID id, UpdatePengadaanAsetRequestDTO request);
}