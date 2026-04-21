package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.restdto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinjauPeminjamanResponseDTO {
    @JsonProperty("id_peninjauan")
    private Long idPeninjauan;

    @JsonProperty("id_peminjaman")
    private UUID idPeminjaman;

    // Data peminjam
    @JsonProperty("id_peminjam")
    private UUID idPeminjam;

    @JsonProperty("nama_peminjam")
    private String namaPeminjam;

    @JsonProperty("role_peminjam")
    private Role rolePeminjam;

    @JsonProperty("unit_asal")
    private String unitAsal;

    // Data aset
    @JsonProperty("id_aset")
    private UUID idAset;

    @JsonProperty("kode_aset")
    private String kodeAset;

    @JsonProperty("nama_aset")
    private String namaAset;

    @JsonProperty("merk_aset")
    private String merkAset;

    @JsonProperty("kategori_aset")
    private KategoriAset kategoriAset;

    @JsonProperty("qty")
    private Integer qty;

    // Detail peminjaman
    @JsonProperty("waktu_peminjaman")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waktuPeminjaman;

    @JsonProperty("waktu_pengembalian")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waktuPengembalian;

    @JsonProperty("waktu_pengajuan")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime waktuPengajuan;

    @JsonProperty("tujuan_peminjaman")
    private String tujuanPeminjaman;

    @JsonProperty("unit_tujuan")
    private String unitTujuan;

    @JsonProperty("status_peminjaman")
    private StatusPeminjaman statusPeminjaman;

    // Data peninjauan
    @JsonProperty("alasan")
    private String alasan;

    @JsonProperty("id_peninjau")
    private UUID idPeninjau;

    @JsonProperty("role_peninjau")
    private Role rolePeninjau;

    @JsonProperty("nama_peninjau")
    private String namaPeninjau;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
