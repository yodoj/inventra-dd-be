package io.ibuprofen.inventra_dd_be.Aset.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsetBarangResponseDTO {
    @JsonProperty("id_aset")
    private UUID idAset;

    @JsonProperty("kode_aset")
    private String kodeAset;

    @JsonProperty("gambar_url_aset")
    private String gambarUrlAset;

    @JsonProperty("nama_aset")
    private String namaAset;

    @JsonProperty("merk_aset")
    private String merkAset;

    @JsonProperty("qty_aset")
    private Integer qtyAset;

    @JsonProperty("lokasi_aset")
    private String lokasiAset;

    @JsonProperty("kategori_aset")
    private KategoriAset kategoriAset;

    @JsonProperty("status_aset")
    private StatusAset statusAset;

    @JsonProperty("qty_tersedia")
    private Integer qtyTersedia;

    @JsonProperty("qty_rusak")
    private Integer qtyRusak;

    @JsonProperty("qty_perbaikan")
    private Integer qtyPerbaikan;

    @JsonProperty("qty_dimusnahkan")
    private Integer qtyDimusnahkan;

    @JsonProperty("qty_dipinjam")
    private Integer qtyDipinjam;

    @JsonProperty("keterangan_aset")
    private String keteranganAset;

    @JsonProperty("unit")
    private String unit;
}
