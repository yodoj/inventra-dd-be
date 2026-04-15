package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.service;

import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request.TinjauPeminjamanRequestDTO;
import io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response.TinjauPeminjamanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import java.util.UUID;
import java.util.List;

public interface TinjauPeminjamanService {
    List<TinjauPeminjamanResponseDTO> getAll(User currentUser);
    
    TinjauPeminjamanResponseDTO create(UUID peminjamanId, TinjauPeminjamanRequestDTO req, User currentUser);
}
