package io.ibuprofen.inventra_dd_be.Aset.service;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Aset.repository.AsetRepository;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.DashboardAssetResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.PeminjamanUnitResponseDTO;
import io.ibuprofen.inventra_dd_be.Aset.restdto.response.PeminjamanTrendResponseDTO;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.repository.PeminjamanAsetRepository;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardAssetService {

    @Autowired
    private AsetRepository asetRepository;

    @Autowired
    private PeminjamanAsetRepository peminjamanAsetRepository;

    public DashboardAssetResponseDTO getAsetSummary(User user, String unitOverride, String kategoriOverride) {
        String unitFilter = null;
        if (user.getRole() == Role.SARPRAS || user.getRole() == Role.KEPSEK) {
            unitFilter = user.getUnit();
        } else if (unitOverride != null && !unitOverride.equals("Semua Unit")) {
            unitFilter = unitOverride;
        }

        long total = asetRepository.sumAllAssets(unitFilter);
        
        Map<String, Long> breakdown = new HashMap<>();
        for (KategoriAset kategori : KategoriAset.values()) {
            breakdown.put(kategori.name(), asetRepository.sumAssetsByCategory(unitFilter, kategori));
        }

        return DashboardAssetResponseDTO.builder()
                .totalAset(total)
                .breakdown(breakdown)
                .build();
    }

    public List<PeminjamanUnitResponseDTO> getPeminjamanPerUnit() {
        String[] units = {"KB-TK", "SD", "SMP", "SMA"};
        List<PeminjamanUnitResponseDTO> result = new ArrayList<>();
        
        for (String unit : units) {
            long count = peminjamanAsetRepository.countApprovedPeminjamanByUnit(unit);
            result.add(PeminjamanUnitResponseDTO.builder()
                    .unit(unit)
                    .totalPeminjaman(count)
                    .build());
        }
        
        return result;
    }

    public List<PeminjamanTrendResponseDTO> getPeminjamanTrend(User user, int year, Integer month, String unitOverride, String kategoriOverride) {
        String unitFilter = null;
        if (user.getRole() == Role.SARPRAS || user.getRole() == Role.KEPSEK) {
            unitFilter = user.getUnit();
        } else if (unitOverride != null && !unitOverride.equals("Semua Unit")) {
            unitFilter = unitOverride;
        }

        String kategoriFilter = null;
        if (kategoriOverride != null && !kategoriOverride.equals("Semua Kategori")) {
            // Convert human readable category to enum name
            if (kategoriOverride.equals("Barang Habis Pakai")) kategoriFilter = KategoriAset.BARANG_HABIS_PAKAI.name();
            else if (kategoriOverride.equals("Barang Tidak Habis Pakai")) kategoriFilter = KategoriAset.BARANG_TIDAK_HABIS_PAKAI.name();
            else if (kategoriOverride.equals("Ruang Kelas")) kategoriFilter = KategoriAset.RUANG_KELAS.name();
            else if (kategoriOverride.equals("Ruang Non Kelas")) kategoriFilter = KategoriAset.RUANG_NON_KELAS.name();
        }

        List<Object[]> rawData;
        List<PeminjamanTrendResponseDTO> result = new ArrayList<>();

        if (month == null) {
            // Monthly trend
            rawData = peminjamanAsetRepository.getMonthlyTrend(unitFilter, year, kategoriFilter);
            String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};
            for (int i = 1; i <= 12; i++) {
                final int m = i;
                long count = rawData.stream()
                        .filter(r -> ((Number) r[0]).intValue() == m)
                        .map(r -> ((Number) r[1]).longValue())
                        .findFirst().orElse(0L);
                result.add(new PeminjamanTrendResponseDTO(months[i - 1], count));
            }
        } else {
            // Weekly trend
            rawData = peminjamanAsetRepository.getWeeklyTrend(unitFilter, year, month, kategoriFilter);
            for (int i = 1; i <= 4; i++) {
                final int w = i;
                long count = rawData.stream()
                        .filter(r -> ((Number) r[0]).intValue() == w)
                        .map(r -> ((Number) r[1]).longValue())
                        .findFirst().orElse(0L);
                result.add(new PeminjamanTrendResponseDTO("Minggu " + i, count));
            }
        }

        return result;
    }
}
