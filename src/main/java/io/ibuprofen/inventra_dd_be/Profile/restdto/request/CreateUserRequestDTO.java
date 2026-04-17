package io.ibuprofen.inventra_dd_be.Profile.restdto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @JsonProperty("nama_lengkap")
    private String namaLengkap;

    @JsonProperty("nomor_telepon")
    private String nomorTelepon;

    @NotNull(message = "Role tidak boleh kosong")
    private Role role;

    private String unit;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, max = 64, message = "Password harus antara 8 sampai 64 karakter")
    private String password;

    private String nisn;

    private String kelas;
}
