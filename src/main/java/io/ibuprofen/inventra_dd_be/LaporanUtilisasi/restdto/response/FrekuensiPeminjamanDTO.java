package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrekuensiPeminjamanDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("aset")
    private String aset;

    @JsonProperty("kategori")
    private String kategori;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("frekuensi_peminjaman")
    private String frekuensiPeminjaman;

    @JsonProperty("frekuensi_count")
    private Long frekuensiCount;

    @JsonProperty("total_durasi_peminjaman")
    private String totalDurasiPeminjaman;

    @JsonProperty("total_durasi_hari")
    private Long totalDurasiHari;

    @JsonProperty("periode")
    private String periode;

    @JsonProperty("status_terakhir")
    private String statusTerakhir;
}
