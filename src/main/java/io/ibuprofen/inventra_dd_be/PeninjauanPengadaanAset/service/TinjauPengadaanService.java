package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.service;

import java.util.List;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request.tinjauPengadaanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response.tinjauPengadaanResponseDTO;

public interface TinjauPengadaanService {
  List<tinjauPengadaanResponseDTO> getAll();
  tinjauPengadaanResponseDTO create(Long pengadaanId, tinjauPengadaanRequestDTO req);
  tinjauPengadaanResponseDTO update(Long pengadaanId, tinjauPengadaanRequestDTO req);
  tinjauPengadaanResponseDTO getByPengadaanId(Long pengadaanId);
}
