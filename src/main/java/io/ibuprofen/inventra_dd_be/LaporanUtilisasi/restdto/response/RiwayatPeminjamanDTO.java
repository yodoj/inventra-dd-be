package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiwayatPeminjamanDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("nama_peminjam")
    private String namaPeminjam;

    @JsonProperty("aset")
    private String aset;

    @JsonProperty("qty")
    private Integer qty;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("waktu_peminjaman")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime waktuPeminjaman;

    @JsonProperty("waktu_pengembalian")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime waktuPengembalian;

    @JsonProperty("tujuan")
    private String tujuan;
}
