package io.ibuprofen.inventra_dd_be.Aset.restdto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Aset.model.StatusAset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsetRuanganResponseDTO {
    @JsonProperty("id_aset")
    private Long idAset;

    @JsonProperty("kode_aset")
    private String kodeAset;

    @JsonProperty("gambar_url_aset")
    private String gambarUrlAset;

    @JsonProperty("nama_aset")
    private String namaAset;

    @JsonProperty("kategori_aset")
    private KategoriAset kategoriAset;

    @JsonProperty("status_aset")
    private StatusAset statusAset;

    @JsonProperty("keterangan_aset")
    private String keteranganAset;

    @JsonProperty("unit")
    private String unit;
}
