package io.ibuprofen.inventra_dd_be.restdto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequestDTO {
    
    @NotBlank(message = "Password saat ini tidak boleh kosong")
    private String currentPassword;

    @NotBlank(message = "Password baru tidak boleh kosong")
    private String newPassword;

    @NotBlank(message = "Konfirmasi password tidak boleh kosong")
    private String confirmPassword;
}
