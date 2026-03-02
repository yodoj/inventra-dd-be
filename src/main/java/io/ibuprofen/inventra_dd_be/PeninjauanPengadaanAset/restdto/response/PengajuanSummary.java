package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.restdto.response;

import io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model.Status;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PengajuanSummary {

    private Long id;

    private String namaAset;
    private String linkGambar;
    private String kategori;
    private String merk;

    private int qty;
    private int estimasiHarga;

    private LocalDateTime waktuPengadaan;

    private Status status;
}