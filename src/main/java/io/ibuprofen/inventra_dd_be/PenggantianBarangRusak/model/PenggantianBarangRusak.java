package io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model;


import io.ibuprofen.inventra_dd_be.Aset.model.KategoriAset;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "penggantian_barang_rusak")
public class PenggantianBarangRusak {

    @Id
    @Column(name = "id_penggantian")
    private String idPenggantian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewer_role", nullable = false)
    private Role rolePengaju;

    @Column(nullable = false)
    private String namaPengaju; 

    @Column(name = "nama_barang", nullable = false, columnDefinition = "VARCHAR(255)")
    private String namaBarang;

    @Column(name = "merk", nullable = false, columnDefinition = "VARCHAR(255)")
    private String merk;

    @Column(name = "qty", nullable = false)
    private Integer quantity;

    @Column(name = "waktu_penggantian", nullable = false)
    private LocalDate waktuPenggantian; 

    @Column(name = "contoh_barang", nullable =  false)
    private String contohBarang;

    @Column(name = "unit", columnDefinition = "VARCHAR(255)")
    private String unitPengaju; 

    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;

    @Column(name = "waktu_pengajuan", nullable = false)
    private LocalDateTime waktuPengajuan;

    @Column(name = "status", nullable = false)
    private String status; 

    @Column(name = "review_pengajuan", columnDefinition = "TEXT")
    private String reviewPengajuan;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (this.idPenggantian == null) {
            this.idPenggantian = "PBR-" + UUID.randomUUID().toString().substring(0,8);
        }

        if (this.waktuPengajuan == null) {
            this.waktuPengajuan = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}