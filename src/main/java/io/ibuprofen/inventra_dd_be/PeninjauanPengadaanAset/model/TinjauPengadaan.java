package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
  name = "tinjau_pengadaan",
  uniqueConstraints = @UniqueConstraint(
    name = "uk_pengadaan_reviewer_role",
    columnNames = {"id_pengadaan", "reviewer_role"}
  )
)
public class TinjauPengadaan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", referencedColumnName = "id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_pengadaan", nullable = false)
  private PengadaanAset pengadaan;

  @Enumerated(EnumType.STRING)
  @Column(name = "reviewer_role", nullable = false)
  private Role reviewerRole;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "alasan", nullable = false, columnDefinition = "TEXT")
  private String alasan;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "kepsek_first_reviewed_at")
  private LocalDateTime kepsekFirstReviewedAt;

  @Column(name = "yayasan_first_reviewed_at")
  private LocalDateTime yayasanFirstReviewedAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "bukti_pembelian")
  private String buktiPembelian;

  @Column(name = "harga")
  private Long harga;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();

    if (this.reviewerRole == Role.KEPSEK && this.kepsekFirstReviewedAt == null) {
      this.kepsekFirstReviewedAt = LocalDateTime.now();
    }
    if (this.reviewerRole == Role.YAYASAN && this.yayasanFirstReviewedAt == null) {
      this.yayasanFirstReviewedAt = LocalDateTime.now();
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public UUID getPengadaanId() {
    return (pengadaan != null) ? pengadaan.getIdPengadaan() : null;
  }

  public void setPengadaanId(UUID pengadaanId) {
    if (pengadaanId != null) {
      this.pengadaan = PengadaanAset.builder().idPengadaan(pengadaanId).build();
    }
  }
}