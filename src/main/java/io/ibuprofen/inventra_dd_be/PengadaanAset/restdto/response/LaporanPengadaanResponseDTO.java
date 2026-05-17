package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("waktu_pengajuan")
    private LocalDateTime waktuPengajuan;

    @JsonProperty("nama_pengaju")
    private String namaPengaju;

    @JsonProperty("nama_aset")
    private String namaAset;

    private String merk;

    private Integer qty;

    @JsonProperty("tanggal_pengadaan")
    private LocalDate waktuPengadaan;

    @JsonProperty("estimasi_harga")
    private Long estimasiHarga;

    @JsonProperty("kategori_aset")
    private KategoriAset kategoriAset;

    private String unit;

    @JsonProperty("status_pengajuan")
    private String statusPengadaan;

    @JsonProperty("bukti_pembelian")
    private String buktiPembelian;

    private String alasan;

    @JsonProperty("harga_aktual")
    private Long hargaAktual;
}