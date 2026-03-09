package io.ibuprofen.inventra_dd_be.Profile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID changedByUserId;

    @Column(nullable = false)
    private String changedByFullName;

    @Column(nullable = false)
    private String changedByRole;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
