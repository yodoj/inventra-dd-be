package io.ibuprofen.inventra_dd_be.PeminjamanAset.model;

import io.ibuprofen.inventra_dd_be.Aset.model.Aset;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "peminjaman_aset")
public class PeminjamanAset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_peminjaman")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User peminjam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aset", referencedColumnName = "id_aset", nullable = false)
    private Aset aset;

    @Column(name = "waktu_pengajuan", nullable = false)
    private LocalDateTime waktuPengajuan;

    @Column(name = "waktu_peminjaman", nullable = false)
    private LocalDateTime waktuPeminjaman;

    @Column(name = "waktu_pengembalian", nullable = false)
    private LocalDateTime waktuPengembalian;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "tujuan_peminjaman", nullable = false, columnDefinition = "TEXT")
    private String tujuanPeminjaman;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_peminjaman", nullable = false)
    private StatusPeminjaman statusPeminjaman;

    @Column(name = "unit_tujuan", nullable = false)
    private String unitTujuan;

    public enum StatusPeminjaman {
        DIAJUKAN,
        DISETUJUI,
        DITOLAK
    }
}
