package io.ibuprofen.inventra_dd_be.PengadaanAset.service;

import io.ibuprofen.inventra_dd_be.PengadaanAset.repository.PengadaanAsetRepository;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanPageResponseDTO;
import io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response.LaporanPengadaanResponseDTO;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.repository.UserRepository;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LaporanPengadaanService {

    private static final List<String> VALID_SORT_FIELDS =
            List.of("waktuPengajuan", "namaAset", "estimasiHarga");

    private static final List<String> VALID_DATE_FIELDS =
            List.of("waktu_pengajuan", "tanggal_pengadaan");
    private static final String DEFAULT_DATE_FIELD = "waktu_pengajuan";

    @Autowired
    private PengadaanAsetRepository repo;

    @Autowired
    private UserRepository userRepository;

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    public LaporanPengadaanPageResponseDTO getLaporanPengadaan(
            String search,
            String status,
            String kategoriStr,
            String filterUnit,
            Integer bulan,
            Integer tahun,
            LocalDate from,
            LocalDate to,
            String dateField,
            String sortBy,
            String direction,
            int page,
            int size) {

        // Input validation (PBI-64 #15, PBI-65 #18)
        if (page < 0) {
            throw new IllegalArgumentException("Parameter 'page' tidak boleh negatif");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Parameter 'size' harus antara 1 dan 100");
        }
        if (sortBy != null && !sortBy.isBlank() && !VALID_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Parameter 'sortBy' tidak valid. Allowed: " + VALID_SORT_FIELDS);
        }
        if (direction != null && !direction.isBlank()
                && !direction.equalsIgnoreCase("ASC") && !direction.equalsIgnoreCase("DESC")) {
            throw new IllegalArgumentException("Parameter 'direction' harus 'ASC' atau 'DESC'");
        }
        if (dateField != null && !dateField.isBlank() && !VALID_DATE_FIELDS.contains(dateField)) {
            throw new IllegalArgumentException(
                    "Parameter 'dateField' tidak valid. Allowed: " + VALID_DATE_FIELDS);
        }
        String dateFieldParam = (dateField != null && !dateField.isBlank()) ? dateField : DEFAULT_DATE_FIELD;

        UserDetailsImpl userDetails = getCurrentUser();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().name();
        String userUnit = user.getUnit();

        // Normalize empty strings to null for JPQL IS NULL checks
        String searchParam = (search != null && !search.isBlank())
                ? search.replaceAll("\\s+", "").toLowerCase()
                : null;
        String statusParam = (status != null && !status.isBlank()) ? status.trim() : null;
        String filterUnitParam = (filterUnit != null && !filterUnit.isBlank()) ? filterUnit.trim() : null;

        String kategori = (kategoriStr != null && !kategoriStr.isBlank()) ? kategoriStr.trim().toUpperCase() : null;

        // Dua pair tanggal: LocalDateTime untuk waktuPengajuan, LocalDate untuk waktuPengadaan.
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(23, 59, 59) : null;

        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "waktuPengajuan"
        );
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<LaporanPengadaanResponseDTO> result;

        if (role.equalsIgnoreCase("YAYASAN") || role.equalsIgnoreCase("ADMIN")) {
            result = repo.findAllLaporanFiltered(
                    searchParam, statusParam, kategori, filterUnitParam,
                    bulan, tahun, fromDateTime, toDateTime, from, to, dateFieldParam, pageable);
        } else if (role.equalsIgnoreCase("KEPSEK") || role.equalsIgnoreCase("SARPRAS")) {
            // unit param from request is ignored — always scoped to the user's own unit
            result = repo.findLaporanByUnitFiltered(
                    userUnit, searchParam, statusParam, kategori,
                    bulan, tahun, fromDateTime, toDateTime, from, to, dateFieldParam, pageable);
        } else {
            throw new AccessDeniedException("Unauthorized role");
        }

        return LaporanPengadaanPageResponseDTO.builder()
                .content(result.getContent())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(result.getNumber())
                .pageSize(result.getSize())
                .build();
    }
}