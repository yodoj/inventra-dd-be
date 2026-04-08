package io.ibuprofen.inventra_dd_be.Profile.restdto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequestDTO {
    
    @NotBlank(message = "Nama Lengkap tidak boleh kosong")
    private String name;

    @NotBlank(message = "No Telepon tidak boleh kosong")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]{10}$", message = "NISN harus tepat 10 digit angka")
    private String nisn;

    private String kelas;
}
