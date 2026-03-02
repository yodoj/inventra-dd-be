package io.ibuprofen.inventra_dd_be.PeninjauanPengadaanAset.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Column(name = "id_pengadaan", nullable = false)
  private Long pengadaanId;

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
}