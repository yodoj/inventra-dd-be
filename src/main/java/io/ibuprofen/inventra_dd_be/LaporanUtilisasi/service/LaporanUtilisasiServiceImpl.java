package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.service;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository.LaporanUtilisasiRepository;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository.LaporanUtilisasiSpecification;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.FrekuensiPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.LaporanUtilisasiResponseDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.RiwayatPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaporanUtilisasiServiceImpl implements LaporanUtilisasiService {

    @Autowired
    private LaporanUtilisasiRepository laporanUtilisasiRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public LaporanUtilisasiResponseDTO<RiwayatPeminjamanDTO> getHistoryReports(
            String unitFilter,
            String periodType,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            String kategori,
            int page,
            int limit) {

        UserDetailsImpl userDetails = getCurrentUser();
        String userUnitForRbac = determineUserUnitForRbac(userDetails);

        LocalDateTime[] resolvedDates = resolveDateRange(periodType, startDate, endDate);
        LocalDateTime resolvedStart = resolvedDates[0];
        LocalDateTime resolvedEnd = resolvedDates[1];

        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, limit > 0 ? limit : 10);

        Specification<PeminjamanAset> spec = LaporanUtilisasiSpecification.filterHistory(
                userUnitForRbac, unitFilter, resolvedStart, resolvedEnd, search, kategori
        );

        Page<PeminjamanAset> pagedResult = laporanUtilisasiRepository.findAll(spec, pageable);

        List<RiwayatPeminjamanDTO> dtoList = pagedResult.getContent().stream()
                .map(this::mapToRiwayatDTO)
                .collect(Collectors.toList());

        return LaporanUtilisasiResponseDTO.success(
                dtoList,
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pageIndex + 1,
                pageable.getPageSize(),
                "Data riwayat peminjaman berhasil diambil"
        );
    }

    @Override
    public LaporanUtilisasiResponseDTO<FrekuensiPeminjamanDTO> getFrequencyReports(
            String unitFilter,
            String periodType,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            String kategori,
            int page,
            int limit) {

        UserDetailsImpl userDetails = getCurrentUser();
        String userUnitForRbac = determineUserUnitForRbac(userDetails);

        LocalDateTime[] resolvedDates = resolveDateRange(periodType, startDate, endDate);
        LocalDateTime resolvedStart = resolvedDates[0];
        LocalDateTime resolvedEnd = resolvedDates[1];

        Specification<PeminjamanAset> spec = LaporanUtilisasiSpecification.filterHistory(
                userUnitForRbac, unitFilter, resolvedStart, resolvedEnd, search, kategori
        );

        // Ambil semua data yang valid untuk diagregasi per aset
        List<PeminjamanAset> allMatching = laporanUtilisasiRepository.findAll(spec);

        Map<Aset, List<PeminjamanAset>> grouped = allMatching.stream()
                .collect(Collectors.groupingBy(PeminjamanAset::getAset));

        String periodeLabel = generatePeriodeLabel(periodType, resolvedStart, resolvedEnd);

        List<FrekuensiPeminjamanDTO> aggregatedList = grouped.entrySet().stream().map(entry -> {
            Aset aset = entry.getKey();
            List<PeminjamanAset> list = entry.getValue();

            long count = list.size();
            long totalDays = list.stream().mapToLong(p -> {
                long days = ChronoUnit.DAYS.between(p.getWaktuPeminjaman().toLocalDate(), p.getWaktuPengembalian().toLocalDate());
                return days <= 0 ? 1 : days;
            }).sum();

            String namaLengkapAset = aset.getKodeAset() + " - " + aset.getNamaAset();

            return FrekuensiPeminjamanDTO.builder()
                    .id(aset.getId())
                    .aset(namaLengkapAset)
                    .kategori(aset.getKategoriAset() != null ? formatKategori(aset.getKategoriAset().name()) : "-")
                    .unit(aset.getUnit() != null ? aset.getUnit().toUpperCase() : "-")
                    .frekuensiCount(count)
                    .frekuensiPeminjaman(count + " Kali")
                    .totalDurasiHari(totalDays)
                    .totalDurasiPeminjaman(totalDays + " Hari")
                    .periode(periodeLabel)
                    .build();
        })
        .sorted(Comparator.comparingLong(FrekuensiPeminjamanDTO::getFrekuensiCount).reversed())
        .collect(Collectors.toList());

        // Implementasi Memory Pagination
        int safeLimit = limit > 0 ? limit : 10;
        int pageIndex = Math.max(page - 1, 0);
        int totalData = aggregatedList.size();
        int totalPage = (int) Math.ceil((double) totalData / safeLimit);
        
        int fromIndex = pageIndex * safeLimit;
        int toIndex = Math.min(fromIndex + safeLimit, totalData);
        
        List<FrekuensiPeminjamanDTO> pagedList = fromIndex < totalData ? aggregatedList.subList(fromIndex, toIndex) : Collections.emptyList();

        return LaporanUtilisasiResponseDTO.success(
                pagedList,
                totalData,
                totalPage,
                pageIndex + 1,
                safeLimit,
                "Data frekuensi peminjaman berhasil diambil"
        );
    }

    private UserDetailsImpl getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }
        throw new org.springframework.security.access.AccessDeniedException("User tidak terautentikasi dengan valid");
    }

    private String determineUserUnitForRbac(UserDetailsImpl userDetails) {
        boolean isAdminOrYayasan = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN") || role.equals("YAYASAN"));

        if (isAdminOrYayasan) {
            return null; // Akses penuh lintas unit
        }
        return userDetails.getUnit(); // Akses terbatas pada unit miliknya
    }

    private RiwayatPeminjamanDTO mapToRiwayatDTO(PeminjamanAset p) {
        String namaAset = p.getAset() != null ? p.getAset().getKodeAset() + " - " + p.getAset().getNamaAset() : "-";
        String unitAset = p.getAset() != null && p.getAset().getUnit() != null ? p.getAset().getUnit().toUpperCase() : "-";
        String namaPeminjam = p.getPeminjam() != null ? p.getPeminjam().getName() : "-";

        return RiwayatPeminjamanDTO.builder()
                .id(p.getId())
                .namaPeminjam(namaPeminjam)
                .aset(namaAset)
                .qty(p.getQty())
                .unit(unitAset)
                .waktuPeminjaman(p.getWaktuPeminjaman())
                .waktuPengembalian(p.getWaktuPengembalian())
                .tujuan(p.getTujuanPeminjaman())
                .build();
    }

    private String formatKategori(String enumName) {
        if (enumName == null) return "-";
        String lower = enumName.toLowerCase().replace("_", " ");
        String[] words = lower.split(" ");
        return Arrays.stream(words)
                .map(w -> w.isEmpty() ? "" : Character.toUpperCase(w.charAt(0)) + w.substring(1))
                .collect(Collectors.joining(" "));
    }

    private String generatePeriodeLabel(String periodType, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "Semua Periode";
        }
        if ("monthly".equalsIgnoreCase(periodType)) {
            String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
            return months[start.getMonthValue() - 1] + " " + start.getYear();
        } else if ("yearly".equalsIgnoreCase(periodType)) {
            return "Jan - Des " + start.getYear();
        } else if ("daily".equalsIgnoreCase(periodType)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return start.format(formatter) + " - " + end.format(formatter);
        }
        return "Semua Periode";
    }

    private LocalDateTime[] resolveDateRange(String periodType, LocalDate startDate, LocalDate endDate) {
        if (periodType == null || periodType.trim().isEmpty()) {
            if (startDate != null && endDate != null) {
                 if (startDate.isAfter(endDate)) {
                     throw new IllegalArgumentException("start_date tidak boleh lebih besar dari end_date");
                 }
                 return new LocalDateTime[]{startDate.atStartOfDay(), endDate.atTime(23, 59, 59, 999999999)};
            }
            return new LocalDateTime[]{null, null};
        }

        LocalDateTime start = null;
        LocalDateTime end = null;

        switch (periodType.toLowerCase()) {
            case "daily":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("start_date dan end_date wajib diisi untuk periode daily");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(23, 59, 59, 999999999);
                break;
            case "monthly":
                if (startDate == null && endDate == null) {
                    start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                    end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59, 999999999);
                } else {
                    LocalDate refDate = startDate != null ? startDate : endDate;
                    start = refDate.withDayOfMonth(1).atStartOfDay();
                    end = refDate.withDayOfMonth(refDate.lengthOfMonth()).atTime(23, 59, 59, 999999999);
                }
                break;
            case "yearly":
                if (startDate == null && endDate == null) {
                    start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                    end = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()).atTime(23, 59, 59, 999999999);
                } else {
                    LocalDate refDate = startDate != null ? startDate : endDate;
                    start = refDate.withDayOfYear(1).atStartOfDay();
                    end = refDate.withDayOfYear(refDate.lengthOfYear()).atTime(23, 59, 59, 999999999);
                }
                break;
            default:
                throw new IllegalArgumentException("Format periode tidak valid. Gunakan: daily, monthly, atau yearly");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start_date tidak boleh lebih besar dari end_date");
        }

        return new LocalDateTime[]{start, end};
    }
}
