package io.ibuprofen.inventra_dd_be.Aset.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAssetResponseDTO {
    private long totalAset;
    private Map<String, Long> breakdown;
}
