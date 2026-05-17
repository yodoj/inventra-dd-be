package io.ibuprofen.inventra_dd_be.PengadaanAset.restcontroller;

import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.*;
import io.ibuprofen.inventra_dd_be.PengadaanAset.service.DashboardPengadaanService;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Profile.restdto.response.BaseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/dashboard/pengadaan")
public class DashboardPengadaanRestController {

        @Autowired
        private DashboardPengadaanService dashboardService;

        // Total + Breakdown
        @GetMapping("/jumlah-barang-harga")
        @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS', 'ADMIN')")
        public ResponseEntity<?> getJumlahBarangHarga(
                @RequestParam(required = false) Integer tahun,
                @RequestParam(required = false) String unit
        ) {
                DashboardPengadaanResponseDTO result =
                        dashboardService.getScoreCard(tahun, unit);

                return ResponseEntity.ok(
                        BaseResponseDTO.ok(result, "Data total pengadaan berhasil diambil")
                );
        }

        // Top 5 Biaya
        @GetMapping("/top-5-biaya-besar")
        @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS', 'ADMIN')")
        public ResponseEntity<?> getTop5Biaya(
                @RequestParam(required = false) Integer tahun,
                @RequestParam(required = false) Integer bulan,
                @RequestParam(required = false) KategoriAset kategori,
                @RequestParam(required = false) String unit
        ) {

                List<TopBiayaResponseDTO> result =
                        dashboardService.getTop5Biaya(
                                tahun,
                                bulan,
                                kategori,
                                unit
                        );

                return ResponseEntity.ok(
                        BaseResponseDTO.ok(result, "Data top 5 biaya terbesar berhasil diambil")
                );
        }

        // Top 5 Pengadaan (list)
        @GetMapping("/top5-pengadaan")
        @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS', 'ADMIN')")
        public ResponseEntity<?> getTop5Combined(
                @RequestParam(required = false) Integer tahun,
                @RequestParam(required = false) Integer bulan,
                @RequestParam(required = false) KategoriAset kategori,
                @RequestParam(required = false) String unit
        ) {

                TopDashboardResponseDTO result =
                        dashboardService.getListTop5(
                                tahun,
                                bulan,
                                kategori,
                                unit
                        );

                return ResponseEntity.ok(
                        BaseResponseDTO.ok(result, "Data top 5 dashboard berhasil diambil")
                );
        }

        // Bar chart biaya pengadaan per tahun
        @GetMapping("/biaya")
        @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS', 'ADMIN')")
        public ResponseEntity<?> getBiayaPengadaanChart(
                @RequestParam(required = false) String unit,  HttpServletRequest request
        ) {
                validateQueryParams(request);
                List<BiayaPengadaanChartResponseDTO> result =
                        dashboardService.getBiayaPengadaanChart(unit);

                return ResponseEntity.ok(
                        BaseResponseDTO.ok(
                                result,
                                "Data chart estimasi biaya pengadaan berhasil diambil"
                        )
                );
        }

        // Bar chart jumlah aset per tahun
        @GetMapping("/jumlah-aset")
        @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS', 'ADMIN')")
        public ResponseEntity<?> getJumlahAsetChart(
                @RequestParam(required = false) String unit, HttpServletRequest request
        ) {
                validateQueryParams(request);
                List<JumlahAsetChartResponseDTO> result =
                        dashboardService.getJumlahAsetChart(unit);

                return ResponseEntity.ok(
                        BaseResponseDTO.ok(
                                result,
                                "Data chart jumlah aset barang berhasil diambil"
                        )
                );
        }

        // Top 5 aset paling cepat habis
        @GetMapping("/top-cepat-habis")
        @PreAuthorize("hasAnyAuthority('YAYASAN','KEPSEK','SARPRAS', 'ADMIN')")
        public ResponseEntity<?> getTopCepatHabis(
                @RequestParam(required = false) Integer tahun,
                @RequestParam(required = false) Integer bulan,
                @RequestParam(required = false) String unit,
                HttpServletRequest request
        ) {

                validateQueryParamsTopCepatHabis(request);

                List<TopCepatHabisResponseDTO> result =
                        dashboardService.getTop5CepatHabis(
                                tahun,
                                bulan,
                                unit
                        );

                return ResponseEntity.ok(
                        BaseResponseDTO.ok(
                                result,
                                "Data top 5 aset paling cepat habis berhasil diambil"
                        )
                );
        }

        private void validateQueryParams(HttpServletRequest request) {

                Set<String> allowedParams = Set.of("unit");

                for (String param : request.getParameterMap().keySet()) {

                        if (!allowedParams.contains(param)) {
                                throw new IllegalArgumentException(
                                        "Bad Request: Parameter tidak valid."
                                );
                        }
                }
        }

        private void validateQueryParamsTopCepatHabis(HttpServletRequest request) {
                Set<String> allowedParams = Set.of("tahun", "unit");
                for (String param : request.getParameterMap().keySet()) {
                        if (!allowedParams.contains(param)) {
                                throw new IllegalArgumentException(
                                        "Bad Request: Parameter tidak valid."
                                );
                        }
                }
        }
}