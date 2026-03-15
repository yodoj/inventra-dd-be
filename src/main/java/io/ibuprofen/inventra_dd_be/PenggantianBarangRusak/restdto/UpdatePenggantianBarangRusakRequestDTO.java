package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.restdto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePenggantianBarangRusakRequestDTO {

    @NotBlank(message = "Nama barang tidak boleh kosong")
    private String namaBarang;

    @NotBlank(message = "Merk tidak boleh kosong")
    private String merk;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas harus lebih dari 0")
    private Integer quantity;

    @NotNull(message = "Waktu penggantian tidak boleh kosong")
    private LocalDate waktuPenggantian;

    private MultipartFile contohBarang;

    @NotBlank(message = "Unit tidak boleh kosong")
    private String unitPengaju;

    private String keterangan;

}