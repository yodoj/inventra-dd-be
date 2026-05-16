package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopDashboardResponseDTO {
    private List<TopCepatHabisResponseDTO> topPengadaan;
    private List<TopBiayaResponseDTO> topBiaya;
}