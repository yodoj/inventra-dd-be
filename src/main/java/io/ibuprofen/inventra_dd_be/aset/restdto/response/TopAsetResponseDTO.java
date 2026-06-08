package io.ibuprofen.inventra_dd_be.Aset.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopAsetResponseDTO {
    private String kodeAset;
    private String namaAset;
    private String merkAset;
    private String unit;
    private String value; // e.g. "150 kali"
    private String kategori; // To distinguish between barang and ruangan in frontend if needed
}
