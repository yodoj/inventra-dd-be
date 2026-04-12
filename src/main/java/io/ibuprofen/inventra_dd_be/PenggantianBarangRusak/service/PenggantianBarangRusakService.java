package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.service;

import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.PenggantianBarangRusakRequestDTO;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.PenggantianBarangRusakResponseDTO;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto.UpdatePenggantianBarangRusakRequestDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface PenggantianBarangRusakService {
    List<PenggantianBarangRusakResponseDTO> getAll(String search, String status);
    PenggantianBarangRusakResponseDTO createPengajuan(PenggantianBarangRusakRequestDTO request, MultipartFile file);
    PenggantianBarangRusakResponseDTO updatePengajuan(String idPenggantian, UpdatePenggantianBarangRusakRequestDTO request, MultipartFile file);
    void deletePengajuan(String idPenggantian);
    PenggantianBarangRusakResponseDTO getById(String idPenggantian);
}
