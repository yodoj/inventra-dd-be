package io.ibuprofen.inventra_dd_be.PengadaanAset.model;

import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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
    private UUID idPengadaan; 

    @Column(nullable = false)
    private java.util.UUID userId;

    @Column(nullable = false)
    private String namaAset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KategoriAset kategoriAset; 

    @Column(nullable = false)
    private String merk;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private Long estimasiHarga;

    @Column(nullable = false)
    private String waktuPengadaan; 

    @Column(columnDefinition = "TEXT")
    private String linkGambar;

    @Column(nullable = false)
    private String statusPengadaan; 

    @Column(nullable = false)
    private String unit; 

    @Column(nullable = false)
    private String namaPengaju; 

    @CreationTimestamp
    @Column(name = "waktu_pengajuan", updatable = true)
    private LocalDateTime waktuPengajuan; 
}