package io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePeminjamanRequestDTO {

    @NotNull(message = "ID Aset tidak boleh kosong")
    private UUID idAset;

    @NotNull(message = "Waktu peminjaman tidak boleh kosong")
    private LocalDateTime waktuPeminjaman;

    @NotNull(message = "Waktu pengembalian tidak boleh kosong")
    private LocalDateTime waktuPengembalian;

    @NotBlank(message = "Tujuan peminjaman tidak boleh kosong")
    private String tujuanPeminjaman;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas minimal 1")
    private Integer qty;

    private String unitTujuan;
}
