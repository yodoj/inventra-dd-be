package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.*;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardPengadaanService {

    @Autowired
    private PengadaanAsetRepository repo;

    @Autowired
    private UserRepository userRepository;

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    // Total + Breakdown (Score card + breakdown per unit)
    public DashboardPengadaanResponseDTO getScoreCard(Integer tahun, String unit) {
        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        String role = user.getRole().name();
        String userUnit = user.getUnit();
        
        // default tahun
        if (tahun == null) {
            tahun = Year.now().getValue();
        }

        // role restriction
        if (!role.equalsIgnoreCase("YAYASAN") && !role.equalsIgnoreCase("ADMIN")) {
            unit = userUnit;
        }

        TotalPengadaanResponseDTO total =
                repo.getTotalPengadaan(tahun, unit);

        List<BreakdownUnitResponseDTO> breakdown = null;
        if (role.equalsIgnoreCase("YAYASAN") || role.equalsIgnoreCase("ADMIN")) {
            breakdown = repo.getBreakdownPerUnit(tahun);
        }

        return new DashboardPengadaanResponseDTO(
                total,
                breakdown,
                null,
                null
        );
    }

    // Top 5 Biaya
    public List<TopBiayaResponseDTO> getTop5Biaya(
            Integer tahun,
            Integer bulan,
            KategoriAset kategori,
            String unit
    ) {

        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        String role = user.getRole().name();
        String userUnit = user.getUnit();

        if (tahun == null) {
            tahun = Year.now().getValue();
        }

        // handle empty param
        unit = (unit == null || unit.isBlank()) ? null : unit;

        // role restriction
        if (!role.equalsIgnoreCase("YAYASAN") && !role.equalsIgnoreCase("ADMIN")) {
            unit = userUnit;
        }

        return repo.getTop5Biaya(
                tahun,
                bulan,
                kategori,
                unit,
                PageRequest.of(0, 5)
        );
    }

    // Top 5 Pengadaan (list)
    public TopDashboardResponseDTO getListTop5(
            Integer tahun,
            Integer bulan,
            KategoriAset kategori,
            String unit
    ) {

        // List<TopPengadaanResponseDTO> topPengadaan =
        //         getTopPengadaan(tahun, bulan, kategori, unit);

        List<TopBiayaResponseDTO> topBiaya =
                getTop5Biaya(tahun, bulan, kategori, unit);

        return new TopDashboardResponseDTO(
                // topPengadaan,
                topBiaya
        );
    }

    // Fungsi untuk mengambil bar chart estimasi biaya pengadaan per tahun
    public List<BiayaPengadaanChartResponseDTO> getBiayaPengadaanChart(String unit) {
        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String role = user.getRole().name();
        String userUnit = user.getUnit();

        // validasi value unit
        unit = validateAndNormalizeUnit(unit);

        // selain yayasan/admin tidak boleh filter unit
        if (!role.equalsIgnoreCase("YAYASAN")
                && !role.equalsIgnoreCase("ADMIN")) {

            if (unit != null) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Forbidden: Anda tidak memiliki akses filter unit."
                );
            }

            unit = userUnit;
        }

        List<BiayaPengadaanChartResponseDTO> dbResult = repo.getBiayaPengadaanPerTahun(unit);

        // Mengubah hasil query jadi map
        Map<Integer, Long> biayaMap = dbResult.stream()
                .collect(Collectors.toMap(
                        BiayaPengadaanChartResponseDTO::getTahun,
                        BiayaPengadaanChartResponseDTO::getTotalBiaya
                ));

        int currentYear = Year.now().getValue();

        List<BiayaPengadaanChartResponseDTO> finalResult = new java.util.ArrayList<>();

        // generate 4 tahun terakhir
        for (int year = currentYear - 3; year <= currentYear; year++) {

            finalResult.add(
                    new BiayaPengadaanChartResponseDTO(
                            year,
                            biayaMap.getOrDefault(year, 0L)
                    )
            );
        }

        return finalResult;
    }

    // Fungsi untuk mengambil bar chart jumlah aset per tahun
    public List<JumlahAsetChartResponseDTO> getJumlahAsetChart(String unit) {
        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String role = user.getRole().name();
        String userUnit = user.getUnit();

        // validasi value unit
        unit = validateAndNormalizeUnit(unit);

        // selain yayasan/admin tidak boleh filter unit
        if (!role.equalsIgnoreCase("YAYASAN")
                && !role.equalsIgnoreCase("ADMIN")) {

            if (unit != null) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Forbidden: Anda tidak memiliki akses filter unit."
                );
            }

            unit = userUnit;
        }

        List<JumlahAsetChartResponseDTO> dbResult = repo.getJumlahAsetPerTahun(unit);

        // Mengubah hasil query jadi map
        Map<Integer, Long> jumlahMap = dbResult.stream()
                .collect(Collectors.toMap(
                        JumlahAsetChartResponseDTO::getTahun,
                        JumlahAsetChartResponseDTO::getJumlahAset
                ));

        int currentYear = Year.now().getValue();

        List<JumlahAsetChartResponseDTO> finalResult = new java.util.ArrayList<>();

        // generate 4 tahun terakhir
        for (int year = currentYear - 3; year <= currentYear; year++) {

            finalResult.add(
                    new JumlahAsetChartResponseDTO(
                            year,
                            jumlahMap.getOrDefault(year, 0L)
                    )
            );
        }

        return finalResult;
    }

    // Fungsi untuk validasi dan normalisasi parameter unit
    private String validateAndNormalizeUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            return null;
        }

        String normalized = unit.trim().toUpperCase();

        switch (normalized) {
            case "KB-TK":
            case "SD":
            case "SMP":
            case "SMA":
                return normalized;

            default:
                throw new IllegalArgumentException(
                        "Bad Request: Value parameter tidak valid."
                );
        }
    }

    // Fungsi untuk mengambil top 5 aset paling cepat habis
    public List<TopCepatHabisResponseDTO> getTop5CepatHabis(Integer tahun, String unit) {
        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        String role = user.getRole().name();
        String userUnit = user.getUnit();

        // default tahun = 2026
        if (tahun == null) {
            tahun = 2026;
        }

        // validasi unit
        unit = validateAndNormalizeUnit(unit);

        // role restriction
        if (!role.equalsIgnoreCase("YAYASAN")
                && !role.equalsIgnoreCase("ADMIN")) {

            if (unit != null) {

                throw new org.springframework.security.access.AccessDeniedException(
                        "Forbidden: Anda tidak memiliki akses filter unit."
                );
            }

            unit = userUnit;
        }

        return repo.getTop5CepatHabis(
                tahun,
                unit,
                PageRequest.of(0, 5)
        );
    }
}