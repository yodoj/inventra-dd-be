package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PembelianRequestDTO {
    @NotNull(message = "Harga tidak boleh kosong")
    @Min(value = 1, message = "Harga tidak boleh kurang dari atau sama dengan 0")
    private Long harga;
    
    @NotNull(message = "Bukti pembelian tidak boleh kosong")
    private MultipartFile buktiPembelian;
}
