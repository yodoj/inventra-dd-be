package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiayaPengadaanChartResponseDTO {
    private Integer tahun;
    private Long totalBiaya; 
}
