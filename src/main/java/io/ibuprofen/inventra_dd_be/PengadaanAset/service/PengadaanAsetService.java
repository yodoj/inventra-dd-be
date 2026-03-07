package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.request.CreatePengadaanAsetRequestDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.PengadaanAsetResponse;
import java.util.List;

public interface PengadaanAsetService {
    PengadaanAsetResponse createPengadaan(CreatePengadaanAsetRequestDTO request);
    List<PengadaanAsetResponse> getAllPengadaan(); 
}