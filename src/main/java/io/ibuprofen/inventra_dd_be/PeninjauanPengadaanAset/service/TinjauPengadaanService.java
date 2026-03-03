package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request.tinjauPengadaanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.tinjauPengadaanResponseDTO;

public interface TinjauPengadaanService {
  List<tinjauPengadaanResponseDTO> getAll();
  tinjauPengadaanResponseDTO create(UUID pengadaanId, tinjauPengadaanRequestDTO req);
  tinjauPengadaanResponseDTO update(UUID pengadaanId, tinjauPengadaanRequestDTO req);
  tinjauPengadaanResponseDTO getByPengadaanId(UUID pengadaanId);
  tinjauPengadaanResponseDTO beli(UUID pengadaanId, Long hargaFinal, MultipartFile file);
}
