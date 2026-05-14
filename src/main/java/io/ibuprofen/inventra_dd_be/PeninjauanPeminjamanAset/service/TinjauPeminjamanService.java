package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

public interface TinjauPeminjamanService {
    List<TinjauPeminjamanResponseDTO> getAll(
            StatusPeminjaman statusPeminjaman, 
            String unitTujuan, 
            LocalDate tanggal, 
            String kategoriAset
    );
    
    TinjauPeminjamanResponseDTO create(UUID idPeminjaman, TinjauPeminjamanRequestDTO request);

    TinjauPeminjamanResponseDTO getPeninjauanById(UUID idPeminjaman);

    TinjauPeminjamanResponseDTO update(UUID idPeminjaman, TinjauPeminjamanRequestDTO request);
}
