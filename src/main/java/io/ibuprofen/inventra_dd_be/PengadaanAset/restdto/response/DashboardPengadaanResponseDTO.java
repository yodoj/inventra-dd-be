package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPengadaanResponseDTO {

    private TotalPengadaanResponseDTO total;
    private List<BreakdownUnitResponseDTO> breakdownUnit;
    private List<TopBiayaResponseDTO> topBiaya;
    private List<TopDashboardResponseDTO> topPengadaan;
}