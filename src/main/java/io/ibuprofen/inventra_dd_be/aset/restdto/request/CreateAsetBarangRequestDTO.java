package io.ibuprofen.inventra_dd_be.Aset.restdto.request;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAsetBarangRequestDTO {
    @NotBlank(message = "Nama aset tidak boleh kosong")
    private String namaAset;

    private String gambarUrlAset;

    private org.springframework.web.multipart.MultipartFile gambarFile;

    @NotNull(message = "Kategori aset tidak boleh boleh kosong")
    private KategoriAset kategoriAset;

    @NotNull(message = "Status aset tidak boleh boleh kosong")
    private StatusAset statusAset;

    private String keteranganAset;

    @NotBlank(message = "Merk aset tidak boleh kosong")
    private String merkAset;

    @NotNull(message = "Qty aset tidak boleh kosong")
    private Integer qtyAset;

    @NotBlank(message = "Lokasi aset tidak boleh kosong")
    private String lokasiAset;

    private String unit;
}
