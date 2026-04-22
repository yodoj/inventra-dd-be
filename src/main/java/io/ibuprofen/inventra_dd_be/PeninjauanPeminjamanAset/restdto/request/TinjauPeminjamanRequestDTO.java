package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinjauPeminjamanRequestDTO {
    @NotNull(message = "Status tidak boleh kosong")
    private StatusPeminjaman statusPeminjaman;

    @NotBlank(message = "Alasan tidak boleh kosong")
    private String alasan;
}
