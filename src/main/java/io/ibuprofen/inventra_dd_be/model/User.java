package io.ibuprofen.inventra_dd_be.model;

import io.ibuprofen.inventra_dd_be.model.validation.ValidateSiswaFields;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@ValidateSiswaFields
@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank
    private String unit;

    @NotBlank
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]+$", message = "NISN harus hanya berisi angka")
    private String nisn;

    private String kelas;
}
