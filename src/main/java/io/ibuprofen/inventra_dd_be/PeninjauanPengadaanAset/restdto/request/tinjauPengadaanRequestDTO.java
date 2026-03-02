package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class tinjauPengadaanRequestDTO {
    @NotNull(message = "Status tidak boleh kosong")
    private Status statusPengadaan;

    @NotEmpty(message = "Alasan tidak boleh kosong")
    private String alasan;
}