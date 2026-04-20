package io.ibuprofen.inventra_dd_be.PeminjamanAset.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanAsetSpecification {

    public static Specification<PeminjamanAset> withFilters(
            StatusPeminjaman status,
            String unitTujuan,
            LocalDate tanggal,
            String kategoriGroup) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter Status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("statusPeminjaman"), status));
            }

            // Filter Unit Tujuan
            if (unitTujuan != null && !unitTujuan.isEmpty()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("unitTujuan")), unitTujuan.toLowerCase()));
            }

            // Filter Tanggal (Waktu Peminjaman)
            if (tanggal != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("DATE", LocalDate.class, root.get("waktuPeminjaman")), tanggal));
            }

            // 4. Filter Kategori (Barang vs Ruang)
            if (kategoriGroup != null && !kategoriGroup.isEmpty()) {
                Join<PeminjamanAset, Aset> asetJoin = root.join("aset");
                if (kategoriGroup.equalsIgnoreCase("BARANG")) {
                    predicates.add(criteriaBuilder.equal(asetJoin.get("kategoriAset"), KategoriAset.BARANG_TIDAK_HABIS_PAKAI));
                } else if (kategoriGroup.equalsIgnoreCase("RUANG")) {
                    predicates.add(asetJoin.get("kategoriAset").in(KategoriAset.RUANG_KELAS, KategoriAset.RUANG_NON_KELAS));
                }
            }

            // Sort by Waktu Pengajuan DESC secara default
            query.orderBy(criteriaBuilder.desc(root.get("waktuPengajuan")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}