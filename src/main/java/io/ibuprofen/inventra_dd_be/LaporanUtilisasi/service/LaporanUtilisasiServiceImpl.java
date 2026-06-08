package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.service;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository.LaporanUtilisasiRepository;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository.LaporanUtilisasiSpecification;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.FrekuensiPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.LaporanUtilisasiResponseDTO;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.restdto.response.RiwayatPeminjamanDTO;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.Profile.services.UserDetailsImpl;
import io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository.LaporanUtilisasiRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
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
                userUnitForRbac, unitFilter, resolvedStart, resolvedEnd, search, kategori, true, false);

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
                "Data riwayat peminjaman berhasil diambil");
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
                userUnitForRbac, unitFilter, resolvedStart, resolvedEnd, search, kategori, true, true);

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
                long days = ChronoUnit.DAYS.between(p.getWaktuPeminjaman().toLocalDate(),
                        p.getWaktuPengembalian().toLocalDate());
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

        List<FrekuensiPeminjamanDTO> pagedList = fromIndex < totalData ? aggregatedList.subList(fromIndex, toIndex)
                : Collections.emptyList();

        return LaporanUtilisasiResponseDTO.success(
                pagedList,
                totalData,
                totalPage,
                pageIndex + 1,
                safeLimit,
                "Data frekuensi peminjaman berhasil diambil");
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
        String unitAset = p.getAset() != null && p.getAset().getUnit() != null ? p.getAset().getUnit().toUpperCase()
                : "-";
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
        if (enumName == null)
            return "-";
        String lower = enumName.toLowerCase().replace("_", " ");
        String[] words = lower.split(" ");
        return Arrays.stream(words)
                .map(w -> w.isEmpty() ? "" : Character.toUpperCase(w.charAt(0)) + w.substring(1))
                .collect(Collectors.joining(" "));
    }

    private String generatePeriodeLabel(String periodType, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return "Jan - Des " + LocalDate.now().getYear();
        }
        if ("monthly".equalsIgnoreCase(periodType)) {
            String[] months = { "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des" };
            return months[start.getMonthValue() - 1] + " " + start.getYear();
        } else if ("yearly".equalsIgnoreCase(periodType)) {
            return "Jan - Des " + start.getYear();
        } else if ("daily".equalsIgnoreCase(periodType)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return start.format(formatter) + " - " + end.format(formatter);
        }
        return "Jan - Des " + LocalDate.now().getYear();
    }

    private LocalDateTime[] resolveDateRange(String periodType, LocalDate startDate, LocalDate endDate) {
        if (periodType == null || periodType.trim().isEmpty()) {
            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException("start_date tidak boleh lebih besar dari end_date");
                }
                return new LocalDateTime[] { startDate.atStartOfDay(), endDate.atTime(23, 59, 59, 999999999) };
            }
            return new LocalDateTime[] { null, null };
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

        return new LocalDateTime[] { start, end };
    }
    @Override
    public byte[] exportPdf(String reportType, String unitFilter, String periodType, LocalDate startDate, LocalDate endDate, String search, String kategori) {
        UserDetailsImpl userDetails = getCurrentUser();
        String userUnitForRbac = determineUserUnitForRbac(userDetails);

        LocalDateTime[] resolvedDates = resolveDateRange(periodType, startDate, endDate);
        LocalDateTime resolvedStart = resolvedDates[0];
        LocalDateTime resolvedEnd = resolvedDates[1];

        // Fetch data based on type
        boolean isFrequency = "frequency".equalsIgnoreCase(reportType);
        Specification<PeminjamanAset> spec = LaporanUtilisasiSpecification.filterHistory(
                userUnitForRbac, unitFilter, resolvedStart, resolvedEnd, search, kategori, true, isFrequency);

        List<PeminjamanAset> allData = laporanUtilisasiRepository.findAll(spec);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Use custom margins: 36f left/right/top, 50f bottom to accommodate footer
        Document document = new Document(PageSize.A4.rotate(), 36f, 36f, 36f, 50f);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Prepare footer metadata details
        String userName = userDetails.getName() != null ? userDetails.getName() : "-";
        String userRole = userDetails.getAuthorities().isEmpty() ? "-" : userDetails.getAuthorities().iterator().next().getAuthority().toUpperCase();
        String userUnit = userDetails.getUnit();

        String roleAndUnit = userRole;
        if (userUnit != null && !userUnit.trim().isEmpty()) {
            roleAndUnit += " (" + userUnit.toUpperCase() + ")";
        }

        String tgl = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID")));
        String jam = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        String footerText = String.format("Dicetak oleh %s (%s) pada %s, %s.", userName, roleAndUnit, tgl, jam);
        writer.setPageEvent(new PDFHeaderFooter(footerText));

        document.open();
        
        // Add Title colored #00588F
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        fontTitle.setColor(new Color(0, 88, 143)); // #00588F
        String titleText = isFrequency ? "LAPORAN FREKUENSI PEMINJAMAN ASET" : "LAPORAN RIWAYAT PEMINJAMAN ASET";
        if (userUnitForRbac != null && !userUnitForRbac.trim().isEmpty()) {
            titleText += " " + userUnitForRbac.toUpperCase();
        }
        Paragraph title = new Paragraph(titleText, fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        // Center underline helper
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(30f); // 30% of width
        lineTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderWidthBottom(0.5f);
        lineCell.setBorderColorBottom(new Color(0, 88, 143));
        lineCell.setFixedHeight(5f);
        lineTable.addCell(lineCell);
        lineTable.setSpacingAfter(15);
        document.add(lineTable);

        // Build active filters
        List<String[]> activeFilters = new ArrayList<>();

        // 1. Unit Filter
        String resolvedUnit = null;
        if (userUnitForRbac != null && !userUnitForRbac.trim().isEmpty()) {
            resolvedUnit = userUnitForRbac.toUpperCase();
        } else if (unitFilter != null && !unitFilter.trim().isEmpty() && !unitFilter.equalsIgnoreCase("Semua Unit")) {
            resolvedUnit = unitFilter.toUpperCase();
        }
        if (resolvedUnit != null) {
            activeFilters.add(new String[]{"Unit", resolvedUnit});
        }

        // 2. Kategori Aset Filter
        if (kategori != null && !kategori.trim().isEmpty() && !kategori.equalsIgnoreCase("Semua Kategori")) {
            String displayKategori = kategori;
            if ("BARANG".equalsIgnoreCase(kategori)) {
                displayKategori = "Barang";
            } else if ("RUANGAN".equalsIgnoreCase(kategori)) {
                displayKategori = "Ruangan";
            }
            activeFilters.add(new String[]{"Kategori Aset", displayKategori});
        }

        // 3. Periode Filter (always added)
        if (periodType != null && !periodType.trim().isEmpty() && resolvedStart != null && resolvedEnd != null) {
            if ("monthly".equalsIgnoreCase(periodType)) {
                String[] indonesianMonths = {
                    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                };
                String monthName = indonesianMonths[resolvedStart.getMonthValue() - 1];
                activeFilters.add(new String[]{"Periode", monthName + " " + resolvedStart.getYear()});
            } else if ("yearly".equalsIgnoreCase(periodType)) {
                activeFilters.add(new String[]{"Tahun", String.valueOf(resolvedStart.getYear())});
            } else if ("daily".equalsIgnoreCase(periodType)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                activeFilters.add(new String[]{"Rentang Tanggal", resolvedStart.format(dtf) + "  s/d  " + resolvedEnd.format(dtf)});
            }
        } else if (startDate != null || endDate != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fromStr = startDate != null ? startDate.format(dtf) : "...";
            String toStr = endDate != null ? endDate.format(dtf) : "...";
            activeFilters.add(new String[]{"Rentang Tanggal", fromStr + "  s/d  " + toStr});
        } else {
            // Default period
            activeFilters.add(new String[]{"Periode", "Jan - Des " + LocalDate.now().getYear()});
        }

        // 4. Pencarian Filter
        if (search != null && !search.trim().isEmpty()) {
            activeFilters.add(new String[]{"Pencarian", "\"" + search.trim() + "\""});
        }

        // Render "FILTER YANG DITERAPKAN" box
        PdfPTable filterTable = new PdfPTable(2);
        filterTable.setWidthPercentage(100f);
        try {
            filterTable.setWidths(new float[]{1f, 4.5f});
        } catch (Exception e) {
            // ignore
        }
        filterTable.setSpacingAfter(15);

        // Header cell
        PdfPCell headerCell = new PdfPCell();
        headerCell.setColspan(2);
        headerCell.setBackgroundColor(new Color(0, 88, 143));
        headerCell.setBorderColor(new Color(0, 88, 143));
        headerCell.setPadding(5);
        
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontHeader.setColor(Color.WHITE);
        fontHeader.setSize(10);
        headerCell.setPhrase(new Phrase("FILTER YANG DITERAPKAN", fontHeader));
        filterTable.addCell(headerCell);

        Color zebraColor = new Color(241, 245, 249);
        Color whiteColor = Color.WHITE;
        Color borderColor = new Color(203, 213, 225);
        Color darkBlueBorder = new Color(0, 88, 143);

        Font fontKey = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontKey.setSize(9.5f);
        fontKey.setColor(new Color(15, 23, 42));

        Font fontVal = FontFactory.getFont(FontFactory.HELVETICA);
        fontVal.setSize(9.5f);
        fontVal.setColor(new Color(51, 65, 85));

        int idx = 0;
        for (String[] pair : activeFilters) {
            Color bg = (idx % 2 == 0) ? zebraColor : whiteColor;
            idx++;

            // Key cell
            PdfPCell keyCell = new PdfPCell(new Phrase(pair[0], fontKey));
            keyCell.setBackgroundColor(bg);
            keyCell.setPadding(5);
            keyCell.setBorder(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
            keyCell.setBorderColor(borderColor);
            if (idx == activeFilters.size()) {
                keyCell.setBorderColorBottom(darkBlueBorder);
                keyCell.setBorderWidthBottom(1f);
            }
            keyCell.setBorderColorLeft(darkBlueBorder);
            keyCell.setBorderWidthLeft(1f);
            filterTable.addCell(keyCell);

            // Value cell
            PdfPCell valCell = new PdfPCell(new Phrase(pair[1], fontVal));
            valCell.setBackgroundColor(bg);
            valCell.setPadding(5);
            valCell.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT);
            valCell.setBorderColor(borderColor);
            if (idx == activeFilters.size()) {
                valCell.setBorderColorBottom(darkBlueBorder);
                valCell.setBorderWidthBottom(1f);
            }
            valCell.setBorderColorRight(darkBlueBorder);
            valCell.setBorderWidthRight(1f);
            filterTable.addCell(valCell);
        }
        document.add(filterTable);

        if (allData.isEmpty()) {
            Paragraph empty = new Paragraph("Data tidak tersedia", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE));
            empty.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(empty);
        } else {
            if (isFrequency) {
                generateFrequencyTable(document, allData, periodType, resolvedStart, resolvedEnd);
            } else {
                generateHistoryTable(document, allData);
            }
        }

        document.close();

        return baos.toByteArray();
    }

    private void generateHistoryTable(Document document, List<PeminjamanAset> data) {
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 4f, 6f, 1.5f, 2.5f, 4.5f, 4.5f, 6f});
        table.setSpacingBefore(10);

        writeTableHeader(table, new String[]{"No", "Nama Peminjam", "Aset", "Qty", "Unit", "Waktu Pinjam", "Waktu Kembali", "Tujuan"});

        int no = 1;
        for (PeminjamanAset p : data) {
            table.addCell(String.valueOf(no++));
            table.addCell(p.getPeminjam() != null ? p.getPeminjam().getName() : "-");
            table.addCell(p.getAset() != null ? p.getAset().getKodeAset() + " - " + p.getAset().getNamaAset() : "-");
            table.addCell(String.valueOf(p.getQty()));
            table.addCell(p.getAset() != null && p.getAset().getUnit() != null ? p.getAset().getUnit().toUpperCase() : "-");
            table.addCell(p.getWaktuPeminjaman().format(DATE_FORMATTER));
            table.addCell(p.getWaktuPengembalian().format(DATE_FORMATTER));
            table.addCell(p.getTujuanPeminjaman());
        }
        document.add(table);
    }

    private void generateFrequencyTable(Document document, List<PeminjamanAset> data, String periodType, LocalDateTime start, LocalDateTime end) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 8f, 4f, 2.5f, 4f, 4f});
        table.setSpacingBefore(10);

        writeTableHeader(table, new String[]{"No", "Aset", "Kategori", "Unit", "Frekuensi", "Total Durasi"});

        Map<Aset, List<PeminjamanAset>> grouped = data.stream().collect(Collectors.groupingBy(PeminjamanAset::getAset));
        List<Map.Entry<Aset, List<PeminjamanAset>>> sorted = grouped.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .collect(Collectors.toList());

        int no = 1;
        for (Map.Entry<Aset, List<PeminjamanAset>> entry : sorted) {
            Aset aset = entry.getKey();
            List<PeminjamanAset> list = entry.getValue();
            long totalDays = list.stream().mapToLong(p -> {
                long days = ChronoUnit.DAYS.between(p.getWaktuPeminjaman().toLocalDate(), p.getWaktuPengembalian().toLocalDate());
                return days <= 0 ? 1 : days;
            }).sum();

            table.addCell(String.valueOf(no++));
            table.addCell(aset.getKodeAset() + " - " + aset.getNamaAset());
            table.addCell(aset.getKategoriAset() != null ? formatKategori(aset.getKategoriAset().name()) : "-");
            table.addCell(aset.getUnit() != null ? aset.getUnit().toUpperCase() : "-");
            table.addCell(list.size() + " Kali");
            table.addCell(totalDays + " Hari");
        }
        document.add(table);
    }

    private void writeTableHeader(PdfPTable table, String[] headers) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0, 88, 143)); // #00588F
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);
        font.setSize(10);

        for (String header : headers) {
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

    private static class PDFHeaderFooter extends PdfPageEventHelper {
        private PdfTemplate totalPagesTemplate;
        private BaseFont baseFont;
        private final String printedByText;

        public PDFHeaderFooter(String printedByText) {
            this.printedByText = printedByText;
            try {
                this.baseFont = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                // fallback
            }
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPagesTemplate = writer.getDirectContent().createTemplate(30, 16);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            float pgW = writer.getPageSize().getWidth();

            // Draw line above footer
            cb.setColorStroke(new Color(203, 213, 225));
            cb.setLineWidth(0.5f);
            cb.moveTo(36, 35);
            cb.lineTo(pgW - 36, 35);
            cb.stroke();

            cb.beginText();
            try {
                if (baseFont != null) {
                    cb.setFontAndSize(baseFont, 9);
                } else {
                    cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9);
                }
            } catch (Exception e) {
                // ignore
            }
            cb.setColorFill(new Color(100, 116, 139));

            // Left side page number prefix
            String pageText = "Halaman " + writer.getPageNumber() + " dari ";
            cb.setTextMatrix(36, 20);
            cb.showText(pageText);
            float len = cb.getEffectiveStringWidth(pageText, false);

            // Right side printed by
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, printedByText, pgW - 36, 20, 0);
            cb.endText();

            // Add template for total pages
            cb.addTemplate(totalPagesTemplate, 36 + len, 20);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPagesTemplate.beginText();
            try {
                if (baseFont != null) {
                    totalPagesTemplate.setFontAndSize(baseFont, 9);
                } else {
                    totalPagesTemplate.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 9);
                }
            } catch (Exception e) {
                // ignore
            }
            totalPagesTemplate.setColorFill(new Color(100, 116, 139));
            totalPagesTemplate.setTextMatrix(0, 0);
            totalPagesTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
            totalPagesTemplate.endText();
        }
    }
}
