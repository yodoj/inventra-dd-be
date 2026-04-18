package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import java.util.UUID;
import java.util.List;

public interface TinjauPeminjamanService {
    List<TinjauPeminjamanResponseDTO> getAll();
    
    TinjauPeminjamanResponseDTO create(UUID idPeminjaman, TinjauPeminjamanRequestDTO request);

    TinjauPeminjamanResponseDTO getPeninjauanById(UUID idPeminjaman);

    TinjauPeminjamanResponseDTO update(UUID idPeminjaman, TinjauPeminjamanRequestDTO request);
}
