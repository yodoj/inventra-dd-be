package io.ibuprofen.inventra_dd_be.PeninjauanPeminjamanAset.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset;
import io.ibuprofen.inventra_dd_be.PeminjamanAset.model.PeminjamanAset.StatusPeminjaman;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tinjau_peminjaman")
public class TinjauPeminjaman {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPeninjauan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_peninjau", referencedColumnName = "id") 
    private User peninjau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_peminjaman", referencedColumnName = "id_peminjaman", nullable = false)
    private PeminjamanAset peminjaman;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_peninjau", nullable = false)
    private Role rolePeninjau;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_peminjaman", nullable = false)
    private StatusPeminjaman statusPeminjaman;
    
    @Column(name = "alasan", nullable = false, columnDefinition = "TEXT")
    private String alasan;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getPeminjamanId() {
        return (peminjaman != null) ? peminjaman.getId() : null; 
    }

    public void setPeminjamanId(UUID peminjamanId) {
        if (peminjamanId != null) {
            this.peminjaman = PeminjamanAset.builder().id(peminjamanId).build();
        }
    }
}