package io.ibuprofen.inventra_dd_be.PengadaanAset.model;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pengadaan_aset")
public class PengadaanAset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_pengadaan")
    private UUID idPengadaan; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @Column(name = "nama_aset", nullable = false, columnDefinition = "VARCHAR(255)")
    private String namaAset;

    @Enumerated(EnumType.STRING)
    @Column(name = "kategori_aset", nullable = false)
    private KategoriAset kategoriAset; 

    @Column(name = "merk", nullable = false, columnDefinition = "VARCHAR(255)")
    private String merk;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "estimasi_harga", nullable = false)
    private Long estimasiHarga;

    @Column(name = "waktu_pengadaan", nullable = false)
    private LocalDate waktuPengadaan; 

    @Column(name = "link_gambar", columnDefinition = "TEXT", nullable =  false)
    private String linkGambar;

    @Column(name = "status_pengadaan", nullable = false)
    private String statusPengadaan; 

    @Column(name = "unit", columnDefinition = "VARCHAR(255)")
    private String unit; 

    @Column(nullable = false)
    private String namaPengaju; 

    @Column(name = "review_pengajuan", columnDefinition = "TEXT")
    private String reviewPengajuan;

    @Column(name = "waktu_pengajuan", nullable = false)
    private LocalDateTime waktuPengajuan;
}