package io.ibuprofen.inventra_dd_be.Aset.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeminjamanTrendResponseDTO {
    private String label;
    private long count;
}
