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

import io.ibuprofen.inventra_dd_be.Aset.model.AsetBarang;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class PeminjamanAsetSpecification {

    public static Specification<PeminjamanAset> withFilters(
            StatusPeminjaman status,
            String unitTujuan,
            LocalDate tanggal,
            LocalDate tanggalPengembalian,
            String kategoriGroup,
            String search) {

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

            // Filter Tanggal Pengembalian
            if (tanggalPengembalian != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("DATE", LocalDate.class, root.get("waktuPengembalian")), tanggalPengembalian));
            }

            // Check if we need Aset join
            Join<PeminjamanAset, Aset> asetJoin = null;
            if ((kategoriGroup != null && !kategoriGroup.isEmpty()) || (search != null && !search.isEmpty())) {
                asetJoin = root.join("aset");
            }

            // 4. Filter Kategori (Barang vs Ruang)
            if (kategoriGroup != null && !kategoriGroup.isEmpty()) {
                if (kategoriGroup.equalsIgnoreCase("BARANG")) {
                    predicates.add(criteriaBuilder.equal(asetJoin.get("kategoriAset"), KategoriAset.BARANG_TIDAK_HABIS_PAKAI));
                } else if (kategoriGroup.equalsIgnoreCase("RUANGAN")) {
                    predicates.add(asetJoin.get("kategoriAset").in(KategoriAset.RUANG_KELAS, KategoriAset.RUANG_NON_KELAS));
                }
            }

            // 5. Search by nama, kode, or merk
            if (search != null && !search.isEmpty()) {
                Predicate namaLike = criteriaBuilder.like(criteriaBuilder.lower(asetJoin.get("namaAset")), search);
                Predicate kodeLike = criteriaBuilder.like(criteriaBuilder.lower(asetJoin.get("kodeAset")), search);
                
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<AsetBarang> subRoot = subquery.from(AsetBarang.class);
                subquery.select(criteriaBuilder.literal(1));
                subquery.where(
                    criteriaBuilder.equal(subRoot.get("id"), asetJoin.get("id")),
                    criteriaBuilder.like(criteriaBuilder.lower(subRoot.get("merkAset")), search)
                );
                Predicate merkLike = criteriaBuilder.exists(subquery);

                predicates.add(criteriaBuilder.or(namaLike, kodeLike, merkLike));
            }

            // Sort by Waktu Pengajuan DESC secara default
            query.orderBy(criteriaBuilder.desc(root.get("waktuPengajuan")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}