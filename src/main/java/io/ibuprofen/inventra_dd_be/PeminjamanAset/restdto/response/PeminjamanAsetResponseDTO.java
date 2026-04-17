package io.ibuprofen.inventra_dd_be.PeminjamanAset.restdto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
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
public class PeminjamanAsetResponseDTO {

    @JsonProperty("id_peminjaman")
    private UUID idPeminjaman;

    @JsonProperty("waktu_pengajuan")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waktuPengajuan;

    @JsonProperty("waktu_peminjaman")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waktuPeminjaman;

    @JsonProperty("waktu_pengembalian")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waktuPengembalian;

    @JsonProperty("qty")
    private Integer qty;

    @JsonProperty("tujuan_peminjaman")
    private String tujuanPeminjaman;

    @JsonProperty("status_peminjaman")
    private StatusPeminjaman statusPeminjaman;

    @JsonProperty("id_peminjam")
    private UUID idPeminjam;

    @JsonProperty("nama_peminjam")
    private String namaPeminjam;

    @JsonProperty("unit_peminjam")
    private String unitPeminjam;

    @JsonProperty("role_peminjam")
    private Role rolePeminjam;

    @JsonProperty("id_aset")
    private UUID idAset;

    @JsonProperty("kode_aset")
    private String kodeAset;

    @JsonProperty("aset")
    private String namaAset;

    @JsonProperty("merk_aset")
    private String merkAset;

    @JsonProperty("kategori_aset")
    private KategoriAset kategoriAset;
    @JsonProperty("unit_tujuan")
    private String unitTujuan;
}
