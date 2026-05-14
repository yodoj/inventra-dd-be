package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.service;

import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.FrekuensiPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.LaporanUtilisasiResponseDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.RiwayatPeminjamanDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface LaporanUtilisasiService {

    LaporanUtilisasiResponseDTO<RiwayatPeminjamanDTO> getHistoryReports(
            String unitFilter,
            String periodType,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            String kategori,
            int page,
            int limit);

    LaporanUtilisasiResponseDTO<FrekuensiPeminjamanDTO> getFrequencyReports(
            String unitFilter,
            String periodType,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            String kategori,
            int page,
            int limit);
}
