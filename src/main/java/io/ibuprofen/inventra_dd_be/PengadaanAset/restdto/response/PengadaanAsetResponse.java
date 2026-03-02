package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PengadaanAsetResponse {

    @JsonProperty("id_pengadaan")
    private UUID idPengadaan;

    @JsonProperty("waktu_pengajuan")
    private LocalDateTime waktuPengajuan;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("nama_aset")
    private String namaAset;

    @JsonProperty("merk")
    private String merk;

    @JsonProperty("qty")
    private Integer qty;

    @JsonProperty("estimasi_harga")
    private Long estimasiHarga;

    @JsonProperty("tanggal_pengadaan")
    private LocalDate tanggalPengadaan;

    @JsonProperty("kategori")
    private KategoriAset kategori;

    @JsonProperty("link_gambar")
    private String linkGambar;

    @JsonProperty("status_pengadaan")
    private String statusPengadaan;
}