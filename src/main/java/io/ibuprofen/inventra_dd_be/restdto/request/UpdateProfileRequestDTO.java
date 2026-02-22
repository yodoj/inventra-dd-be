package io.ibuprofen.inventra_dd_be.restdto.request;

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
    @Pattern(regexp = "^[0-9]+$", message = "No Telepon harus hanya berisi angka")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]+$", message = "NISN harus hanya berisi angka")
    private String nisn;

    private String kelas;
}
