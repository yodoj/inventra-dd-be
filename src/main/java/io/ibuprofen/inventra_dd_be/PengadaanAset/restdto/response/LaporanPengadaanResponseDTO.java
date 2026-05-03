package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaporanPengadaanResponseDTO {
    private LocalDateTime waktuPengajuan;
    private UUID idPengadaan;
    private String namaAset;
    private String merk;
    private Integer qty;
    private LocalDate waktuPengadaan;
    private Long estimasiHarga;
    private KategoriAset kategoriAset;
    private String unit;
    private String statusPengadaan;
    private String buktiPembelian;
    private String alasan;
}