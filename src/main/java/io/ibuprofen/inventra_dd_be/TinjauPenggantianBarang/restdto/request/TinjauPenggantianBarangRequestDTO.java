package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.restdto.request;

import io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinjauPenggantianBarangRequestDTO {
    @NotNull(message = "Status tidak boleh kosong")
    private Status statusPenggantian;

    @NotEmpty(message = "Alasan tidak boleh kosong")
    private String alasan;
}