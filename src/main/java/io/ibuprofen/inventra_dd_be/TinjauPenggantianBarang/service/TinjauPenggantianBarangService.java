package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.Status;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.request.TinjauPenggantianBarangRequestDTO;
import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.response.TinjauPenggantianBarangResponseDTO;

public interface TinjauPenggantianBarangService {
  List<TinjauPenggantianBarangResponseDTO> getAll(Status statusPenggantian, String search);
  TinjauPenggantianBarangResponseDTO create(String penggantianId, TinjauPenggantianBarangRequestDTO req);
  TinjauPenggantianBarangResponseDTO update(String penggantianId, TinjauPenggantianBarangRequestDTO req);
  TinjauPenggantianBarangResponseDTO getByPenggantianId(String penggantianId);
}