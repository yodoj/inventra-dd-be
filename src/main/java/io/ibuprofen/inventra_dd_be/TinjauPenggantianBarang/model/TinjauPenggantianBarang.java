package io.ibuprofen.inventra_dd_be.TinjauPenggantianBarang.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.ibuprofen.inventra_dd_be.PengadaanAset.model.PengadaanAset;
import io.ibuprofen.inventra_dd_be.PenggantianBarangRusak.model.PenggantianBarangRusak;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import io.ibuprofen.inventra_dd_be.Profile.model.User;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
  name = "tinjau_penggantian_barang"
)
public class TinjauPenggantianBarang {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", referencedColumnName = "id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_penggantian", nullable = false)
  private PenggantianBarangRusak penggantian;

  @Enumerated(EnumType.STRING)
  @Column(name = "reviewer_role", nullable = false)
  private Role reviewerRole;

  @Enumerated(EnumType.STRING)
  private Status status;

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

  public String getIdPenggantian() {
    return (penggantian != null) ? penggantian.getIdPenggantian() : null;
  }

  public void setIdPenggantian(String penggantianId) {
    if (penggantianId != null) {
      this.penggantian = PenggantianBarangRusak.builder().idPenggantian(penggantianId).build();
    }
  }
}