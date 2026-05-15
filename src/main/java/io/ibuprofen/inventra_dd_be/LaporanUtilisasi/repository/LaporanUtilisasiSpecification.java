package io.ibuprofen.inventra_dd_be.LaporanUtilisasi.repository;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LaporanUtilisasiSpecification {

    public static Specification<PeminjamanAset> filterHistory(
            String userUnitForRbac,
            String unitFilter,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String search,
            String kategori,
            boolean onlyReturned,
            boolean isFrequency) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Selalu hanya ambil data yang berstatus DISETUJUI untuk utilisasi
            predicates.add(criteriaBuilder.equal(root.get("statusPeminjaman"), PeminjamanAset.StatusPeminjaman.DISETUJUI));

            // Jika onlyReturned true, maka waktu_pengembalian harus <= waktu sekarang
            if (onlyReturned) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("waktuPengembalian"), LocalDateTime.now()));
            }

            Join<PeminjamanAset, Aset> asetJoin = root.join("aset", JoinType.INNER);
            Join<PeminjamanAset, User> peminjamJoin = root.join("peminjam", JoinType.INNER);

            // Filter otomatis RBAC untuk Kepsek & Sarpras
            if (userUnitForRbac != null && !userUnitForRbac.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(asetJoin.get("unit")),
                        userUnitForRbac.trim().toLowerCase()
                ));
            }

            // Filter dropdown Unit dari frontend
            if (unitFilter != null && !unitFilter.trim().isEmpty() && !unitFilter.equalsIgnoreCase("Semua Unit")) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(asetJoin.get("unit")),
                        unitFilter.trim().toLowerCase()
                ));
            }

            // Filter rentang tanggal (Start Date - End Date)
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("waktuPeminjaman"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("waktuPeminjaman"), endDate));
            }

            // Filter Kategori Aset
            if (kategori != null && !kategori.trim().isEmpty() && !kategori.equalsIgnoreCase("Semua Kategori")) {
                String val = kategori.trim();
                if (val.equalsIgnoreCase("RUANGAN")) {
                    predicates.add(root.get("aset").get("kategoriAset").in(
                            KategoriAset.RUANG_KELAS, KategoriAset.RUANG_NON_KELAS
                    ));
                } else if (val.equalsIgnoreCase("BARANG")) {
                    predicates.add(root.get("aset").get("kategoriAset").in(
                            KategoriAset.BARANG_HABIS_PAKAI, KategoriAset.BARANG_TIDAK_HABIS_PAKAI
                    ));
                } else {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(asetJoin.get("kategoriAset").as(String.class)),
                            val.toLowerCase()
                    ));
                }
            }

            // Pencarian teks (Search bar)
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate matchNamaAset = criteriaBuilder.like(criteriaBuilder.lower(asetJoin.get("namaAset")), pattern);
                Predicate matchKodeAset = criteriaBuilder.like(criteriaBuilder.lower(asetJoin.get("kodeAset")), pattern);

                if (isFrequency) {
                    predicates.add(criteriaBuilder.or(matchNamaAset, matchKodeAset));
                } else {
                    Predicate matchNamaPeminjam = criteriaBuilder.like(criteriaBuilder.lower(peminjamJoin.get("name")), pattern);
                    predicates.add(criteriaBuilder.or(matchNamaPeminjam, matchNamaAset, matchKodeAset));
                }
            }

            // Urutkan berdasarkan waktu peminjaman terbaru secara default
            query.orderBy(criteriaBuilder.desc(root.get("waktuPeminjaman")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
