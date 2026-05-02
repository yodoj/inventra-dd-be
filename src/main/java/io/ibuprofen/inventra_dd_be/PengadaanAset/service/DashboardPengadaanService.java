package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.*;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

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
}