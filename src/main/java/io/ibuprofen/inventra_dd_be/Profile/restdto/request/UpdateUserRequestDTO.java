package io.ibuprofen.inventra_dd_be.Profile.restdto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @JsonProperty("nama_lengkap")
    private String namaLengkap;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @JsonProperty("nomor_telepon")
    private String nomorTelepon;

    @NotNull(message = "Role tidak boleh kosong")
    private Role role;

    private String nisn;

    private String kelas;

    private String unit;
}
