package io.ibuprofen.inventra_dd_be.Aset.restdto.response;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowableAsetResponseDTO {
    private UUID idAset;
    private String kodeAset;
    private String namaAset;
    private String merkAset; // Null for rooms
    private KategoriAset kategoriAset;
    private Integer qtyTersedia; // 1 for rooms if status is TERSEDIA
}
