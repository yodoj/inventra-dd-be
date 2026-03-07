package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PembelianRequestDTO {
    @NotEmpty(message = "Harga tidak boleh kosong")
    private Long harga;
    
    @NotEmpty(message = "Bukti pembelian tidak boleh kosong")
    private MultipartFile buktiPembelian;
}
